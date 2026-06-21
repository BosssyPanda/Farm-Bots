import type { ConceptProgress, FarmState, RunResponse } from "./types";

const FARM_STATE_KEY = "mm-farm-state";
const CODE_KEY_PREFIX = "mm-strategy-code:";
const LAYOUT_KEY = "fwr_layout";
const DEFAULT_OBJECTIVE_ID = "first-sprout";

// ----- Window layout (docked by default, floatable on demand) -----

export type WindowMode = "docked" | "floating";

export interface WindowLayout {
  mode: WindowMode;
  /** Remembered floating position (absolute left/top). */
  x?: number;
  y?: number;
  /** Whether the window is hidden (e.g. the optional Concepts roadmap). */
  closed?: boolean;
}

export type LayoutState = Record<string, WindowLayout>;

const VALID_MODES: WindowMode[] = ["docked", "floating"];

/** Read the persisted per-window layout. Returns {} (callers merge over their defaults). */
export function loadLayout(): LayoutState {
  if (typeof window === "undefined") return {};
  try {
    const raw = window.localStorage.getItem(LAYOUT_KEY);
    if (!raw) return {};
    const parsed = JSON.parse(raw) as unknown;
    if (!parsed || typeof parsed !== "object" || Array.isArray(parsed)) return {};

    const out: LayoutState = {};
    for (const [id, value] of Object.entries(parsed as Record<string, unknown>)) {
      if (!value || typeof value !== "object") continue;
      const v = value as Partial<WindowLayout>;
      out[id] = {
        mode: VALID_MODES.includes(v.mode as WindowMode) ? (v.mode as WindowMode) : "docked",
        x: typeof v.x === "number" ? v.x : undefined,
        y: typeof v.y === "number" ? v.y : undefined,
        closed: typeof v.closed === "boolean" ? v.closed : undefined,
      };
    }
    return out;
  } catch {
    return {};
  }
}

export function saveLayout(layout: LayoutState): void {
  if (typeof window === "undefined") return;
  try {
    window.localStorage.setItem(LAYOUT_KEY, JSON.stringify(layout));
  } catch {
    /* ignore quota / unavailable storage */
  }
}

export function clearLayout(): void {
  if (typeof window === "undefined") return;
  try {
    window.localStorage.removeItem(LAYOUT_KEY);
  } catch {
    /* ignore unavailable storage */
  }
}

// ----- Practice track (Skill Drills) -----
// A reinforcement track separate from engine concept mastery: drills earn their
// own XP/streak and remember which drills the learner has seen / got wrong, so
// selection can adapt. It NEVER writes engine mastery (golden rule).

const PRACTICE_KEY = "fwr_practice";

export interface ConceptPractice {
  xp: number;
  streak: number;
  /** correct answers logged */
  done: number;
  /** drill ids already shown */
  seen: string[];
  /** drill ids currently answered wrong (cleared when later correct) */
  wrong: string[];
  lastTs: number;
}

export type PracticeState = Record<string, ConceptPractice>;

export function emptyConceptPractice(): ConceptPractice {
  return { xp: 0, streak: 0, done: 0, seen: [], wrong: [], lastTs: 0 };
}

export function loadPractice(): PracticeState {
  if (typeof window === "undefined") return {};
  try {
    const raw = window.localStorage.getItem(PRACTICE_KEY);
    if (!raw) return {};
    const parsed = JSON.parse(raw) as unknown;
    if (!parsed || typeof parsed !== "object" || Array.isArray(parsed)) return {};

    const out: PracticeState = {};
    for (const [concept, value] of Object.entries(parsed as Record<string, unknown>)) {
      if (!value || typeof value !== "object") continue;
      const v = value as Partial<ConceptPractice>;
      out[concept] = {
        xp: typeof v.xp === "number" ? v.xp : 0,
        streak: typeof v.streak === "number" ? v.streak : 0,
        done: typeof v.done === "number" ? v.done : 0,
        seen: Array.isArray(v.seen) ? v.seen.filter((s) => typeof s === "string") : [],
        wrong: Array.isArray(v.wrong) ? v.wrong.filter((s) => typeof s === "string") : [],
        lastTs: typeof v.lastTs === "number" ? v.lastTs : 0,
      };
    }
    return out;
  } catch {
    return {};
  }
}

export function savePractice(state: PracticeState): void {
  if (typeof window === "undefined") return;
  try {
    window.localStorage.setItem(PRACTICE_KEY, JSON.stringify(state));
  } catch {
    /* ignore quota / unavailable storage */
  }
}

export function clearPractice(): void {
  if (typeof window === "undefined") return;
  try {
    window.localStorage.removeItem(PRACTICE_KEY);
  } catch {
    /* ignore unavailable storage */
  }
}

export function createDefaultFarmState(currentObjectiveId = DEFAULT_OBJECTIVE_ID): FarmState {
  return {
    version: 1,
    currentObjectiveId,
    width: 6,
    height: 4,
    tick: 0,
    tiles: [],
    resources: {},
    unlocked: [],
    concepts: {},
  };
}

export function loadFarmState(fallbackObjectiveId = DEFAULT_OBJECTIVE_ID): FarmState {
  if (typeof window === "undefined") return createDefaultFarmState(fallbackObjectiveId);

  try {
    const raw = window.localStorage.getItem(FARM_STATE_KEY);
    if (!raw) return createDefaultFarmState(fallbackObjectiveId);

    const parsed = JSON.parse(raw) as Partial<FarmState> | null;
    if (!parsed || typeof parsed !== "object") return createDefaultFarmState(fallbackObjectiveId);

    return {
      ...createDefaultFarmState(parsed.currentObjectiveId || fallbackObjectiveId),
      ...parsed,
      version: 1,
      currentObjectiveId: parsed.currentObjectiveId || fallbackObjectiveId,
      width: typeof parsed.width === "number" ? parsed.width : 6,
      height: typeof parsed.height === "number" ? parsed.height : 4,
      tick: typeof parsed.tick === "number" ? parsed.tick : 0,
      tiles: Array.isArray(parsed.tiles) ? parsed.tiles : [],
      resources: parsed.resources && typeof parsed.resources === "object" ? parsed.resources : {},
      unlocked: Array.isArray(parsed.unlocked) ? parsed.unlocked : [],
      concepts: normalizeConcepts(parsed.concepts),
    };
  } catch {
    return createDefaultFarmState(fallbackObjectiveId);
  }
}

export function saveFarmState(state: FarmState): void {
  if (typeof window === "undefined") return;
  try {
    window.localStorage.setItem(FARM_STATE_KEY, JSON.stringify(state));
  } catch {
    /* ignore quota / unavailable storage */
  }
}

export function clearFarmState(): void {
  if (typeof window === "undefined") return;
  try {
    window.localStorage.removeItem(FARM_STATE_KEY);
  } catch {
    /* ignore unavailable storage */
  }
}

export function strategyCodeKey(objectiveId: string): string {
  return `${CODE_KEY_PREFIX}${objectiveId}`;
}

export function loadStrategyCode(objectiveId: string, starter: string): string {
  if (typeof window === "undefined") return starter;

  try {
    const raw = window.localStorage.getItem(strategyCodeKey(objectiveId));
    return raw ?? starter;
  } catch {
    return starter;
  }
}

export function saveStrategyCode(objectiveId: string, code: string): void {
  if (typeof window === "undefined") return;

  try {
    window.localStorage.setItem(strategyCodeKey(objectiveId), code);
  } catch {
    /* ignore quota / unavailable storage */
  }
}

export function clearStrategyCode(objectiveId: string): void {
  if (typeof window === "undefined") return;

  try {
    window.localStorage.removeItem(strategyCodeKey(objectiveId));
  } catch {
    /* ignore unavailable storage */
  }
}

export function clearAllStrategyCode(): void {
  if (typeof window === "undefined") return;

  try {
    for (let i = window.localStorage.length - 1; i >= 0; i--) {
      const key = window.localStorage.key(i);
      if (key?.startsWith(CODE_KEY_PREFIX)) {
        window.localStorage.removeItem(key);
      }
    }
  } catch {
    /* ignore unavailable storage */
  }
}

function normalizeConcepts(value: unknown): Record<string, ConceptProgress> {
  if (!value || typeof value !== "object" || Array.isArray(value)) return {};

  const concepts: Record<string, ConceptProgress> = {};
  for (const [key, raw] of Object.entries(value as Record<string, unknown>)) {
    if (!raw || typeof raw !== "object") continue;
    const concept = raw as Partial<ConceptProgress>;
    concepts[key] = {
      correctStreak: typeof concept.correctStreak === "number" ? concept.correctStreak : 0,
      mastered: typeof concept.mastered === "boolean" ? concept.mastered : false,
      failCount: typeof concept.failCount === "number" ? concept.failCount : 0,
      recapDue: typeof concept.recapDue === "boolean" ? concept.recapDue : false,
    };
  }
  return concepts;
}

export function extractStrategyEditableSource(source: string): string {
  const classStart = source.search(/\bpublic\s+class\s+Strategy\s*\{/);
  if (classStart < 0) return source.trim();

  const openBrace = source.indexOf("{", classStart);
  if (openBrace < 0) return source.trim();

  const closeBrace = findMatchingBrace(source, openBrace);
  if (closeBrace < 0) return source.slice(openBrace + 1).trim();

  return source.slice(openBrace + 1, closeBrace).trim();
}

export function buildStrategySource(editableSource: string): string {
  const body = extractStrategyEditableSource(editableSource).trim();
  return `public class Strategy {\n${body}\n}\n`;
}

export function isValidRunStateTransition(response: RunResponse | null | undefined): response is RunResponse {
  const farmState = response?.farmState;
  return Boolean(
    response?.compiled &&
      farmState &&
      typeof farmState.currentObjectiveId === "string" &&
      Number.isFinite(farmState.tick) &&
      Array.isArray(farmState.tiles) &&
      farmState.resources &&
      typeof farmState.resources === "object" &&
      farmState.concepts &&
      typeof farmState.concepts === "object" &&
      response.objective &&
      typeof response.objective.id === "string" &&
      response.concepts &&
      typeof response.concepts === "object",
  );
}

export function resolveCommittedFarmStateAfterPlayback(
  current: FarmState,
  response: RunResponse | null,
  playbackComplete: boolean,
): FarmState {
  if (!playbackComplete || !isValidRunStateTransition(response)) return current;
  return response.farmState;
}

export function computeNewlyUnlocked(before: FarmState, response: RunResponse): string[] {
  if (Array.isArray(response.newlyUnlocked) && response.newlyUnlocked.length > 0) {
    return uniqueStrings(response.newlyUnlocked);
  }

  const beforeSet = new Set(before.unlocked);
  const fromFarmState = uniqueStrings((response.farmState?.unlocked ?? []).filter((item) => !beforeSet.has(item)));
  if (fromFarmState.length > 0) return fromFarmState;

  return uniqueStrings((response.unlocked ?? []).filter((item) => !beforeSet.has(item)));
}

function uniqueStrings(values: string[]): string[] {
  return Array.from(new Set(values.filter((value) => typeof value === "string" && value.length > 0)));
}

function findMatchingBrace(source: string, openBrace: number): number {
  let depth = 0;
  let inString: "\"" | "'" | "`" | null = null;
  let inLineComment = false;
  let inBlockComment = false;
  let escaped = false;

  for (let i = openBrace; i < source.length; i++) {
    const char = source[i];
    const next = source[i + 1];

    if (inLineComment) {
      if (char === "\n") inLineComment = false;
      continue;
    }

    if (inBlockComment) {
      if (char === "*" && next === "/") {
        inBlockComment = false;
        i++;
      }
      continue;
    }

    if (inString) {
      if (escaped) {
        escaped = false;
      } else if (char === "\\") {
        escaped = true;
      } else if (char === inString) {
        inString = null;
      }
      continue;
    }

    if (char === "/" && next === "/") {
      inLineComment = true;
      i++;
      continue;
    }

    if (char === "/" && next === "*") {
      inBlockComment = true;
      i++;
      continue;
    }

    if (char === "\"" || char === "'" || char === "`") {
      inString = char;
      continue;
    }

    if (char === "{") depth++;
    if (char === "}") {
      depth--;
      if (depth === 0) return i;
    }
  }

  return -1;
}
