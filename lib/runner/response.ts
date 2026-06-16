import type { FarmState, ObjectiveResult, RunResponse } from "@/lib/types";

export const TICK_LIMIT = 5000;

export function emptyFarmState(): FarmState {
  return {
    version: 1,
    currentObjectiveId: "first-sprout",
    width: 6,
    height: 4,
    tick: 0,
    tiles: [],
    resources: {},
    unlocked: [],
    concepts: {},
  };
}

export function contractError({
  compileErrors = "",
  runtimeError = "",
  compiled = false,
}: {
  compileErrors?: string;
  runtimeError?: string;
  compiled?: boolean;
}): RunResponse {
  return {
    ok: false,
    compiled,
    compileErrors,
    runtimeError,
    stdout: "",
    ticks: 0,
    tickLimit: TICK_LIMIT,
    frames: [],
    farmState: emptyFarmState(),
    objective: { id: "", concept: "", checks: [], passed: false },
    unlocked: [],
    concepts: {},
  };
}

export function compileFailure(message: string): RunResponse {
  return contractError({ compileErrors: message, compiled: false });
}

export function runtimeFailure(message: string, compiled = true): RunResponse {
  return contractError({ runtimeError: message, compiled });
}

export function parseRunnerJson(stdout: string): { ok: true; value: Omit<RunResponse, "ok" | "compiled" | "compileErrors"> } | { ok: false; message: string } {
  const line = stdout.trim().split(/\r?\n/).find((candidate) => candidate.trim().startsWith("{"));
  if (!line) {
    return { ok: false, message: "Runner did not return JSON. The Java engine may have exited before reporting its result." };
  }

  try {
    return { ok: true, value: normalizeRunnerJson(JSON.parse(line) as Partial<Omit<RunResponse, "ok" | "compiled" | "compileErrors">>) };
  } catch {
    return { ok: false, message: "Runner returned malformed JSON. The Java engine result could not be read." };
  }
}

export function errorMessage(error: unknown): string {
  if (error instanceof Error && error.message) return error.message;
  if (typeof error === "string" && error) return error;
  return "Unknown backend error.";
}

function normalizeRunnerJson(raw: Partial<Omit<RunResponse, "ok" | "compiled" | "compileErrors">>): Omit<RunResponse, "ok" | "compiled" | "compileErrors"> {
  const farmState = isFarmState(raw.farmState) ? raw.farmState : emptyFarmState();
  const objective = isObjectiveResult(raw.objective) ? raw.objective : { id: "", concept: "", checks: [], passed: false };
  const concepts = raw.concepts && typeof raw.concepts === "object" && !Array.isArray(raw.concepts) ? raw.concepts : {};
  const unlocked = Array.isArray(raw.unlocked) ? raw.unlocked.filter((value): value is string => typeof value === "string") : [];
  const newlyUnlocked = Array.isArray(raw.newlyUnlocked) ? raw.newlyUnlocked.filter((value): value is string => typeof value === "string") : [];
  return {
    runtimeError: typeof raw.runtimeError === "string" ? raw.runtimeError : "",
    stdout: typeof raw.stdout === "string" ? raw.stdout : "",
    ticks: typeof raw.ticks === "number" && Number.isFinite(raw.ticks) ? raw.ticks : 0,
    tickLimit: typeof raw.tickLimit === "number" && Number.isFinite(raw.tickLimit) ? raw.tickLimit : TICK_LIMIT,
    frames: Array.isArray(raw.frames) ? raw.frames : [],
    farmState,
    objective,
    unlocked,
    newlyUnlocked,
    concepts,
  };
}

function isFarmState(value: unknown): value is FarmState {
  return Boolean(
    value &&
      typeof value === "object" &&
      !Array.isArray(value) &&
      typeof (value as FarmState).currentObjectiveId === "string" &&
      typeof (value as FarmState).width === "number" &&
      typeof (value as FarmState).height === "number" &&
      typeof (value as FarmState).tick === "number" &&
      Array.isArray((value as FarmState).tiles),
  );
}

function isObjectiveResult(value: unknown): value is ObjectiveResult {
  return Boolean(
    value &&
      typeof value === "object" &&
      !Array.isArray(value) &&
      typeof (value as ObjectiveResult).id === "string" &&
      typeof (value as ObjectiveResult).concept === "string" &&
      Array.isArray((value as ObjectiveResult).checks) &&
      typeof (value as ObjectiveResult).passed === "boolean",
  );
}
