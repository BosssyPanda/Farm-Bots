import { beforeEach, describe, expect, it, vi } from "vitest";
import type { RunResponse } from "@/lib/types";

type CommandResult = {
  exitCode: number;
  stdout: () => Promise<string>;
  stderr: () => Promise<string>;
};

const sandboxControl = vi.hoisted(() => {
  const create = vi.fn();
  return { create };
});

vi.mock("@vercel/sandbox", () => ({
  Sandbox: {
    create: sandboxControl.create,
  },
}));

function command(exitCode: number, stdout = "", stderr = ""): CommandResult {
  return {
    exitCode,
    stdout: vi.fn(async () => stdout),
    stderr: vi.fn(async () => stderr),
  };
}

function runnerJson(overrides: Partial<Omit<RunResponse, "ok" | "compiled" | "compileErrors">> = {}) {
  return JSON.stringify({
    runtimeError: "",
    stdout: "",
    ticks: 0,
    tickLimit: 5000,
    frames: [],
    farmState: {
      version: 1,
      currentObjectiveId: "first-sprout",
      width: 6,
      height: 4,
      tick: 0,
      tiles: [],
      resources: {},
      unlocked: [],
      concepts: {},
    },
    objective: { id: "first-sprout", concept: "methods", checks: [], passed: false },
    unlocked: [],
    concepts: {},
    ...overrides,
  });
}

function makeSandbox(results: Array<CommandResult | Error>) {
  const runCommand = vi.fn(async () => {
    const next = results.shift();
    if (next instanceof Error) throw next;
    if (!next) throw new Error("unexpected command");
    return next;
  });
  const sandbox = {
    writeFiles: vi.fn(async () => undefined),
    updateNetworkPolicy: vi.fn(async () => undefined),
    runCommand,
    stop: vi.fn(async () => undefined),
  };
  sandboxControl.create.mockResolvedValueOnce(sandbox);
  return sandbox;
}

async function createRunner() {
  const { SandboxRunner } = await import("@/lib/runner/sandbox");
  return new SandboxRunner();
}

describe("SandboxRunner", () => {
  beforeEach(() => {
    sandboxControl.create.mockReset();
    vi.resetModules();
  });

  it("fails cleanly and stops the sandbox when JDK installation fails", async () => {
    const sandbox = makeSandbox([
      command(1, "", ""),
      command(1, "", "dnf failed"),
    ]);

    const result = await (await createRunner()).run({
      code: "public class Strategy { public void run(Drone drone, Farm farm) {} }",
      farmState: null,
    });

    expect(result.ok).toBe(false);
    expect(result.compiled).toBe(false);
    expect(result.runtimeError).toMatch(/java runtime|jdk/i);
    expect(sandbox.stop).toHaveBeenCalledOnce();
  });

  it("returns compile failures in the run contract and stops the sandbox", async () => {
    const sandbox = makeSandbox([
      command(0, "/usr/bin/javac", ""),
      command(1, "", "Strategy.java:3: error: ';' expected"),
    ]);

    const result = await (await createRunner()).run({
      code: "public class Strategy { public void run(Drone drone, Farm farm) { nope } }",
      farmState: null,
    });

    expect(result.ok).toBe(false);
    expect(result.compiled).toBe(false);
    expect(result.compileErrors).toContain("Strategy.java");
    expect(result.runtimeError).toBe("");
    expect(sandbox.stop).toHaveBeenCalledOnce();
  });

  it("reports command timeouts without throwing and stops the sandbox", async () => {
    const timeout = Object.assign(new Error("Command timed out"), { name: "TimeoutError" });
    const sandbox = makeSandbox([
      command(0, "/usr/bin/javac", ""),
      command(0, "", ""),
      timeout,
    ]);

    const result = await (await createRunner()).run({
      code: "public class Strategy { public void run(Drone drone, Farm farm) { while (true) {} } }",
      farmState: null,
    });

    expect(result.ok).toBe(false);
    expect(result.compiled).toBe(true);
    expect(result.runtimeError).toMatch(/timed out|kept running/i);
    expect(sandbox.stop).toHaveBeenCalledOnce();
  });

  it("reports missing runner JSON without throwing and stops the sandbox", async () => {
    const sandbox = makeSandbox([
      command(0, "/usr/bin/javac", ""),
      command(0, "", ""),
      command(0, "hello from java\n", ""),
    ]);

    const result = await (await createRunner()).run({
      code: "public class Strategy { public void run(Drone drone, Farm farm) {} }",
      farmState: null,
    });

    expect(result.ok).toBe(false);
    expect(result.compiled).toBe(true);
    expect(result.runtimeError).toMatch(/did not return json/i);
    expect(sandbox.stop).toHaveBeenCalledOnce();
  });

  it("reports malformed runner JSON without throwing and stops the sandbox", async () => {
    const sandbox = makeSandbox([
      command(0, "/usr/bin/javac", ""),
      command(0, "", ""),
      command(0, "{not-json}\n", ""),
    ]);

    const result = await (await createRunner()).run({
      code: "public class Strategy { public void run(Drone drone, Farm farm) {} }",
      farmState: null,
    });

    expect(result.ok).toBe(false);
    expect(result.compiled).toBe(true);
    expect(result.runtimeError).toMatch(/malformed json/i);
    expect(sandbox.stop).toHaveBeenCalledOnce();
  });

  it("returns runtime failures from Runner JSON and stops the sandbox", async () => {
    const sandbox = makeSandbox([
      command(0, "/usr/bin/javac", ""),
      command(0, "", ""),
      command(1, runnerJson({ runtimeError: "RuntimeException: Illegal move", stdout: "before crash" }), "stack trace"),
    ]);

    const result = await (await createRunner()).run({
      code: "public class Strategy { public void run(Drone drone, Farm farm) { drone.moveWest(); } }",
      farmState: null,
    });

    expect(result.ok).toBe(false);
    expect(result.compiled).toBe(true);
    expect(result.runtimeError).toBe("RuntimeException: Illegal move");
    expect(result.stdout).toBe("before crash");
    expect(sandbox.stop).toHaveBeenCalledOnce();
  });
});
