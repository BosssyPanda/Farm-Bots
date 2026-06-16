import { describe, expect, it } from "vitest";
import { existsSync } from "node:fs";
import { join } from "node:path";
import { createDefaultFarmState } from "@/lib/persist";
import type { FarmState, RunResponse } from "@/lib/types";

type PersistHelpers = typeof import("@/lib/persist") & {
  buildStrategySource?: (editableSource: string) => string;
  computeNewlyUnlocked?: (before: FarmState, response: RunResponse) => string[];
  extractStrategyEditableSource?: (source: string) => string;
  resolveCommittedFarmStateAfterPlayback?: (
    current: FarmState,
    response: RunResponse | null,
    playbackComplete: boolean,
  ) => FarmState;
};

function response(overrides: Partial<RunResponse> = {}): RunResponse {
  const base = createDefaultFarmState("first-sprout");
  const next = createDefaultFarmState("the-long-rows");
  next.tick = 12;
  next.tiles = [{ x: 1, y: 0, crop: "WHEAT", plantedTick: 2, ripe: true, moisture: 3 }];
  next.unlocked = ["basic-planting"];

  return {
    ok: true,
    compiled: true,
    compileErrors: "",
    runtimeError: "",
    stdout: "",
    ticks: 12,
    tickLimit: 5000,
    frames: [],
    farmState: next,
    objective: {
      id: base.currentObjectiveId,
      concept: "methods",
      checks: [{ id: "plant", label: "Plant wheat", passed: true }],
      passed: true,
    },
    unlocked: ["basic-planting"],
    concepts: {},
    ...overrides,
  };
}

describe("frontend run-state helpers", async () => {
  const helpers = (await import("@/lib/persist")) as PersistHelpers;

  it("keeps Strategy.java wrapper locked while preserving editable class body", () => {
    const fullSource = `public class Strategy {
    public void run(Drone drone, Farm farm) {
        drone.moveEast();
    }

    private void helper() {
        System.out.println("ok");
    }
}`;

    const extract = helpers.extractStrategyEditableSource ?? (() => "__missing__");
    const build = helpers.buildStrategySource ?? ((body: string) => body);
    const editable = extract(fullSource);

    expect(editable).toContain("public void run(Drone drone, Farm farm)");
    expect(editable).toContain("private void helper()");
    expect(editable).not.toContain("public class Strategy");
    expect(build(editable)).toContain("public class Strategy");
    expect(build(editable)).toContain(editable.trim());
  });

  it("commits returned farm state only after a valid playback state transition completes", () => {
    const current = createDefaultFarmState("first-sprout");
    const successful = response();
    const compileFailure = response({
      ok: false,
      compiled: false,
      compileErrors: "Forbidden code",
    });
    const runtimeFailure = response({
      ok: false,
      runtimeError: "Drone tried to leave the field.",
    });
    const resolve =
      helpers.resolveCommittedFarmStateAfterPlayback ??
      ((_: FarmState, run: RunResponse | null) => run?.farmState ?? current);

    expect(resolve(current, successful, false)).toBe(current);
    expect(resolve(current, compileFailure, true)).toBe(current);
    expect(resolve(current, runtimeFailure, true)).toBe(runtimeFailure.farmState);
    expect(resolve(current, successful, true)).toBe(successful.farmState);
  });

  it("computes newly unlocked abilities from the submitted farm state", () => {
    const current = createDefaultFarmState("first-sprout");
    current.unlocked = ["basic-planting"];
    const next = response();
    next.farmState.unlocked = ["basic-planting", "bigger-field"];
    next.newlyUnlocked = [];
    next.unlocked = ["basic-planting", "bigger-field"];

    const compute = helpers.computeNewlyUnlocked ?? (() => []);

    expect(compute(current, next)).toEqual(["bigger-field"]);
  });

  it("keeps accidental stale TSX duplicates out of the TypeScript include set", () => {
    const root = process.cwd();

    expect(existsSync(join(root, "app/page 2.tsx"))).toBe(false);
    expect(existsSync(join(root, "components/LessonPanel 2.tsx"))).toBe(false);
  });
});
