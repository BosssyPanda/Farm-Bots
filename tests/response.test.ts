import { describe, expect, it } from "vitest";
import { parseRunnerJson } from "@/lib/runner/response";

describe("runner response helpers", () => {
  it("normalizes missing farmState from fatal engine JSON into the run contract", () => {
    const parsed = parseRunnerJson(JSON.stringify({
      frames: [],
      objective: { id: "", concept: "", checks: [], passed: false },
      concepts: {},
      unlocked: [],
      farmState: null,
      ticks: 0,
      tickLimit: 5000,
      runtimeError: "RuntimeException: boom",
      stdout: "",
    }));

    expect(parsed.ok).toBe(true);
    if (!parsed.ok) return;
    expect(parsed.value.farmState).toMatchObject({
      version: 1,
      currentObjectiveId: "first-sprout",
      width: 6,
      height: 4,
    });
    expect(parsed.value.runtimeError).toBe("RuntimeException: boom");
  });
});
