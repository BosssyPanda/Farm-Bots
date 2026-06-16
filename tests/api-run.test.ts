import { beforeEach, describe, expect, it, vi } from "vitest";
import type { FarmState, RunResponse } from "@/lib/types";

type ExecCallback = (error: Error | null, stdout?: string, stderr?: string) => void;
type ExecFileInvocation = [cmd: string, args: string[], options: unknown];
type ExecFileImplementation = (...args: [...ExecFileInvocation, ExecCallback]) => void;
type ExecFileResult = { stdout?: string; stderr?: string };

const customPromisify = Symbol.for("nodejs.util.promisify.custom");

const execFileMock = vi.hoisted(() => {
  const fn = vi.fn<ExecFileImplementation>() as ReturnType<typeof vi.fn<ExecFileImplementation>> & {
    [customPromisify]: (...args: ExecFileInvocation) => Promise<ExecFileResult>;
  };
  return fn;
});

execFileMock[customPromisify] = (...args: ExecFileInvocation) =>
  new Promise((resolve, reject) => {
    const impl = execFileMock.getMockImplementation();
    if (!impl) {
      reject(new Error("execFile mock is not configured"));
      return;
    }
    impl(...args, (error, stdout, stderr) => {
      if (error) {
        reject(error);
      } else {
        resolve({ stdout, stderr });
      }
    });
  });

vi.mock("node:child_process", () => ({
  __esModule: true,
  default: { execFile: execFileMock },
  execFile: execFileMock,
}));

type RunnerJson = Omit<RunResponse, "ok" | "compiled" | "compileErrors">;

function farmState(): FarmState {
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

function runnerJson(overrides: Partial<RunnerJson> = {}): RunnerJson {
  return {
    runtimeError: "",
    stdout: "",
    ticks: 9,
    tickLimit: 5000,
    frames: [],
    farmState: farmState(),
    objective: {
      id: "first-sprout",
      concept: "methods",
      checks: [{ id: "plant-targets", label: "Plant a crop on all 3 target tiles (3/3)", passed: true }],
      passed: true,
    },
    unlocked: [],
    concepts: {},
    ...overrides,
  };
}

async function createRunner() {
  const { LocalRunner } = await import("@/lib/runner/local");
  return new LocalRunner();
}

describe("LocalRunner", () => {
  beforeEach(() => {
    execFileMock.mockReset();
  });

  it("rejects forbidden code before any compile step runs", async () => {
    const runner = await createRunner();

    const result = await runner.run({
      code: "import java.util.ArrayList; public class Strategy { public void run(Drone drone, Farm farm) {} }",
      farmState: farmState(),
    });

    expect(result.compiled).toBe(false);
    expect(result.ok).toBe(false);
    expect(result.compileErrors).toContain("Imports are not allowed");
    expect(result.runtimeError).toBe("");
    expect(result.frames).toEqual([]);
    expect(execFileMock).not.toHaveBeenCalled();
  });

  it("returns a compile failure shape with beginner-friendly line rewriting", async () => {
    execFileMock.mockImplementation((cmd, _args, _options, callback) => {
      if (cmd === "javac") {
        callback(Object.assign(new Error("compile failed"), {
          code: 1,
          stdout: "",
          stderr: "Strategy.java:12: error: ';' expected\n",
        }));
      } else {
        callback(null, "", "");
      }
    });

    const runner = await createRunner();
    const result = await runner.run({
      code: "public class Strategy { public void run(Drone drone, Farm farm) { drone.watch(\"i\", 1); } }",
      farmState: farmState(),
    });

    expect(result.compiled).toBe(false);
    expect(result.ok).toBe(false);
    expect(result.compileErrors).toContain("Strategy.java line 12");
    expect(result.runtimeError).toBe("");
    expect(result.ticks).toBe(0);
    expect(result.frames).toEqual([]);
  });

  it("reports runtime errors while preserving stdout from the Java runner", async () => {
    execFileMock.mockImplementation((cmd, _args, _options, callback) => {
      if (cmd === "javac") {
        callback(null, "", "");
        return;
      }

      callback(Object.assign(new Error("runtime failed"), {
        code: 1,
        stdout: JSON.stringify(runnerJson({
          runtimeError: "IllegalStateException: boom",
          stdout: "drone x=1",
          objective: {
            id: "first-sprout",
            concept: "methods",
            checks: [{ id: "plant-targets", label: "Plant a crop on all 3 target tiles (3/3)", passed: false }],
            passed: false,
          },
        })),
        stderr: "",
      }));
    });

    const runner = await createRunner();
    const result = await runner.run({
      code: "public class Strategy { public void run(Drone drone, Farm farm) { drone.watch(\"x\", drone.x()); } }",
      farmState: farmState(),
    });

    expect(result.compiled).toBe(true);
    expect(result.ok).toBe(false);
    expect(result.runtimeError).toBe("IllegalStateException: boom");
    expect(result.compileErrors).toBe("");
    expect(result.stdout).toBe("drone x=1");
  });

  it("captures stdout from a successful run", async () => {
    execFileMock.mockImplementation((cmd, _args, _options, callback) => {
      if (cmd === "javac") {
        callback(null, "", "");
        return;
      }

      callback(null, JSON.stringify(runnerJson({
        stdout: "planted 3 rows",
        runtimeError: "",
        objective: {
          id: "first-sprout",
          concept: "methods",
          checks: [{ id: "plant-targets", label: "Plant a crop on all 3 target tiles (3/3)", passed: true }],
          passed: true,
        },
      })), "");
    });

    const runner = await createRunner();
    const result = await runner.run({
      code: "public class Strategy { public void run(Drone drone, Farm farm) { drone.watch(\"done\", 1); } }",
      farmState: farmState(),
    });

    expect(result.compiled).toBe(true);
    expect(result.ok).toBe(true);
    expect(result.stdout).toBe("planted 3 rows");
  });

  it("reports a passed objective and unlocks progression on a successful run", async () => {
    execFileMock.mockImplementation((cmd, _args, _options, callback) => {
      if (cmd === "javac") {
        callback(null, "", "");
        return;
      }

      callback(null, JSON.stringify(runnerJson({
        stdout: "complete",
        farmState: {
          ...farmState(),
          currentObjectiveId: "the-long-rows",
          unlocked: ["basic-planting"],
          concepts: {
            methods: { correctStreak: 3, mastered: true, failCount: 0, recapDue: false },
          },
        },
        objective: {
          id: "first-sprout",
          concept: "methods",
          checks: [{ id: "plant-targets", label: "Plant a crop on all 3 target tiles (3/3)", passed: true }],
          passed: true,
        },
        unlocked: ["basic-planting"],
        concepts: {
          methods: { correctStreak: 3, mastered: true, failCount: 0, recapDue: false },
        },
      })), "");
    });

    const runner = await createRunner();
    const result = await runner.run({
      code: "public class Strategy { public void run(Drone drone, Farm farm) { drone.moveEast(); drone.plant(Crop.WHEAT); } }",
      farmState: farmState(),
    });

    expect(result.compiled).toBe(true);
    expect(result.ok).toBe(true);
    expect(result.objective.passed).toBe(true);
    expect(result.farmState.currentObjectiveId).toBe("the-long-rows");
    expect(result.unlocked).toContain("basic-planting");
  });
});
