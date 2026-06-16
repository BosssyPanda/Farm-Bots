import type { JavaRunner } from "./index";
import type { ObjectiveCatalog, RunRequest, RunResponse } from "../types";
import { Sandbox } from "@vercel/sandbox";
import { readEngineFiles, readSampleStrategy } from "./engineFiles";
import { precheckStrategyCode } from "./precheck";
import { compileFailure, errorMessage, parseRunnerJson, runtimeFailure } from "./response";
import { encodeFarmState } from "./stateCodec";

export class SandboxRunner implements JavaRunner {
  async run(req: RunRequest): Promise<RunResponse> {
    const precheck = precheckStrategyCode(req.code);
    if (!precheck.ok) {
      return compileFailure(precheck.message);
    }

    try {
      return await this.withSandbox(req.code, async (sandbox) => {
        const files = await readEngineFiles(req.code);
        const javaFiles = files.map((file) => file.path).filter((file) => file.endsWith(".java"));
        await sandbox.writeFiles([
          ...files,
          { path: "state.txt", content: Buffer.from(encodeFarmState(req.farmState), "utf8") },
        ]);

        const jdk = await ensureJdk(sandbox);
        if (!jdk.ok) return runtimeFailure(jdk.message, false);

        await sandbox.updateNetworkPolicy("deny-all");
        const compile = await sandbox.runCommand("javac", javaFiles, { timeoutMs: 8_000 });
        const compileErr = await compile.stderr();
        if (compile.exitCode !== 0) return compileFailure(compileErr);

        const run = await runJava(sandbox);
        if (!run.ok) return run.response;

        const parsed = parseRunnerJson(run.stdout);
        if (!parsed.ok) return runtimeFailure(parsed.message);

        return {
          ok: !parsed.value.runtimeError && run.exitCode === 0,
          compiled: true,
          compileErrors: "",
          ...parsed.value,
          runtimeError: parsed.value.runtimeError || (run.exitCode !== 0 ? run.stderr : ""),
        };
      });
    } catch (error) {
      return runtimeFailure(errorMessage(error), false);
    }
  }

  async catalog(): Promise<ObjectiveCatalog> {
    const strategy = await readSampleStrategy();
    return this.withSandbox(strategy, async (sandbox) => {
      const files = await readEngineFiles(strategy);
      const javaFiles = files.map((file) => file.path).filter((file) => file.endsWith(".java"));
      await sandbox.writeFiles(files);
      await ensureJdk(sandbox);
      const compile = await sandbox.runCommand("javac", javaFiles, { timeoutMs: 8_000 });
      if (compile.exitCode !== 0) throw new Error(await compile.stderr());
      const run = await sandbox.runCommand("java", ["Runner", "--catalog"], { timeoutMs: 10_000 });
      return JSON.parse(await run.stdout()) as ObjectiveCatalog;
    });
  }

  private async withSandbox<T>(strategyCode: string, fn: (sandbox: Sandbox) => Promise<T>): Promise<T> {
    void strategyCode;
    const sandbox = await Sandbox.create({ runtime: "node24" });
    try {
      return await fn(sandbox);
    } finally {
      await sandbox.stop().catch(() => undefined);
    }
  }
}

async function ensureJdk(sandbox: Sandbox): Promise<{ ok: true } | { ok: false; message: string }> {
  const check = await sandbox.runCommand("bash", ["-lc", "command -v javac"], { timeoutMs: 3_000 });
  if (check.exitCode === 0) return { ok: true };
  const install = await sandbox.runCommand({
    cmd: "sudo",
    args: ["dnf", "install", "-y", "java-17-amazon-corretto-devel"],
    timeoutMs: 60_000,
  });
  if (install.exitCode !== 0) {
    return { ok: false, message: `Could not prepare the Java runtime in Sandbox: ${await install.stderr()}` };
  }
  const recheck = await sandbox.runCommand("bash", ["-lc", "command -v javac"], { timeoutMs: 3_000 });
  if (recheck.exitCode !== 0) {
    return { ok: false, message: "Could not prepare the Java runtime in Sandbox: javac was still unavailable after JDK installation." };
  }
  return { ok: true };
}

async function runJava(sandbox: Sandbox): Promise<{ ok: true; exitCode: number; stdout: string; stderr: string } | { ok: false; response: RunResponse }> {
  try {
    const run = await sandbox.runCommand("java", ["Runner", "--state", "state.txt"], { timeoutMs: 10_000 });
    return {
      ok: true,
      exitCode: run.exitCode,
      stdout: await run.stdout(),
      stderr: await run.stderr(),
    };
  } catch (error) {
    const message = errorMessage(error);
    const timedOut = /time/i.test(message) || (error instanceof Error && /timeout/i.test(error.name));
    return {
      ok: false,
      response: runtimeFailure(timedOut
        ? "Your code kept running without spending ticks. Add a drone action or a condition so the run can finish safely."
        : message),
    };
  }
}
