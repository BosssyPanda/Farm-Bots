import { afterEach, describe, expect, it, vi } from "vitest";
import {
  checkPredict,
  evaluateWriteLine,
  getDrillsForConcept,
  pickDrill,
  practiceMastery,
  recordDrillResult,
  runWriteLine,
  totalXp,
  type PredictDrill,
  type WriteLineDrill,
} from "@/lib/drills";
import { clearPractice, loadPractice, savePractice, type PracticeState } from "@/lib/persist";

function predictDrill(): PredictDrill {
  const d = getDrillsForConcept("for-loops").find((x): x is PredictDrill => x.kind === "predict");
  if (!d) throw new Error("expected a predict drill for for-loops");
  return d;
}

function writeLineDrill(): WriteLineDrill {
  const d = getDrillsForConcept("for-loops").find((x): x is WriteLineDrill => x.kind === "write-line");
  if (!d) throw new Error("expected a write-line drill for for-loops");
  return d;
}

afterEach(() => {
  vi.restoreAllMocks();
  window.localStorage.clear();
});

describe("predict drills", () => {
  it("checks the chosen index against the answer", () => {
    const d = predictDrill();
    expect(checkPredict(d, d.answerIndex)).toBe(true);
    expect(checkPredict(d, (d.answerIndex + 1) % d.choices.length)).toBe(false);
  });
});

describe("write-line evaluation", () => {
  const d = writeLineDrill();

  it("rejects when the concept pattern is missing", () => {
    const v = evaluateWriteLine(d, { compiled: true, runtimeError: "", stdout: d.expectStdout ?? "", patternOk: false });
    expect(v.pass).toBe(false);
    expect(v.reason).toContain(d.patternHint);
  });

  it("rejects when the code does not compile", () => {
    const v = evaluateWriteLine(d, { compiled: false, runtimeError: "", stdout: "", patternOk: true });
    expect(v.pass).toBe(false);
    expect(v.reason.toLowerCase()).toContain("compile");
  });

  it("rejects when stdout does not match", () => {
    const v = evaluateWriteLine(d, { compiled: true, runtimeError: "", stdout: "nope", patternOk: true });
    expect(v.pass).toBe(false);
    expect(v.reason).toContain("didn't match");
  });

  it("passes when pattern, compile, and stdout all line up", () => {
    const v = evaluateWriteLine(d, { compiled: true, runtimeError: "", stdout: d.expectStdout ?? "", patternOk: true });
    expect(v.pass).toBe(true);
  });
});

describe("runWriteLine", () => {
  it("posts the wrapped line to /api/run and reports compile + stdout + pattern", async () => {
    const fetchMock = vi.fn(async () => ({
      json: async () => ({ compiled: true, runtimeError: "", stdout: "0\n1\n2\n3\n4\n" }),
    }));
    vi.stubGlobal("fetch", fetchMock as unknown as typeof fetch);

    const d = writeLineDrill();
    const out = await runWriteLine(d, "for (int i = 0; i < 5; i++)");

    expect(fetchMock).toHaveBeenCalledTimes(1);
    const call = fetchMock.mock.calls[0];
    expect(call).toBeDefined();
    const [url, init] = call as unknown as [string, RequestInit];
    expect(url).toBe("/api/run");
    expect(String(init.body)).toContain("public class Strategy");
    expect(out.compiled).toBe(true);
    expect(out.stdout).toBe("0\n1\n2\n3\n4"); // trimmed
    expect(out.patternOk).toBe(true);
  });
});

describe("drill selection", () => {
  it("returns a drill for a concept", () => {
    expect(pickDrill("for-loops", {})).not.toBeNull();
    expect(pickDrill("methods", {})).not.toBeNull();
  });

  it("avoids repeating the excluded id when alternatives exist", () => {
    const drills = getDrillsForConcept("for-loops");
    expect(drills.length).toBeGreaterThan(1);
    const excluded = drills[0].id;
    for (let i = 0; i < 8; i++) {
      expect(pickDrill("for-loops", {}, excluded)?.id).not.toBe(excluded);
    }
  });
});

describe("practice progress", () => {
  it("awards xp + streak on correct and clears the wrong flag", () => {
    const d = predictDrill();
    let state: PracticeState = {};
    state = recordDrillResult(state, d, false);
    expect(state[d.concept].streak).toBe(0);
    expect(state[d.concept].wrong).toContain(d.id);

    state = recordDrillResult(state, d, true);
    expect(state[d.concept].streak).toBe(1);
    expect(state[d.concept].done).toBe(1);
    expect(state[d.concept].xp).toBeGreaterThan(0);
    expect(state[d.concept].wrong).not.toContain(d.id);
  });

  it("computes mastery and total xp", () => {
    const d = predictDrill();
    let state: PracticeState = {};
    for (let i = 0; i < 6; i++) state = recordDrillResult(state, d, true);
    expect(practiceMastery(state, d.concept)).toBe(1);
    expect(totalXp(state)).toBeGreaterThan(0);
  });
});

describe("practice store round-trip", () => {
  it("saves and loads practice state", () => {
    clearPractice();
    const d = predictDrill();
    const state = recordDrillResult({}, d, true);
    savePractice(state);
    const loaded = loadPractice();
    expect(loaded[d.concept].done).toBe(1);
    expect(loaded[d.concept].xp).toBe(state[d.concept].xp);
  });
});
