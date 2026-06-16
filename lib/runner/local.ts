import type { JavaRunner } from "./index";
import type { ObjectiveCatalog, RunRequest, RunResponse } from "../types";
import { execFile } from "node:child_process";
import { mkdtemp, rm, writeFile } from "node:fs/promises";
import { tmpdir } from "node:os";
import path from "node:path";
import { promisify } from "node:util";
import { readEngineFiles, readSampleStrategy } from "./engineFiles";
import { precheckStrategyCode } from "./precheck";
import { compileFailure, parseRunnerJson, runtimeFailure } from "./response";
import { encodeFarmState } from "./stateCodec";

const execFileAsync = promisify(execFile);
const RUN_TIMEOUT_MS = 10_000;
const COMPILE_TIMEOUT_MS = 8_000;

export class LocalRunner implements JavaRunner {
  async run(req: RunRequest): Promise<RunResponse> {
    const precheck = precheckStrategyCode(req.code);
    if (!precheck.ok) return compileFailure(precheck.message);

    return this.withTempEngine(req.code, async (dir) => {
      const compile = await runProcess("javac", javaFiles(await readEngineFiles(req.code)), dir, COMPILE_TIMEOUT_MS);
      if (compile.code !== 0) {
        return compileFailure(beginnerCompileMessage(compile.stderr || compile.stdout));
      }

      const statePath = path.join(dir, "state.txt");
      await writeFile(statePath, encodeFarmState(req.farmState), "utf8");
      const run = await runProcess("java", ["-cp", dir, "Runner", "--state", statePath], dir, RUN_TIMEOUT_MS);
      if (run.timedOut) {
        return runtimeFailure("Your code kept running without spending ticks. Add a drone action or a condition so the run can finish safely.");
      }
      const parsed = parseRunnerJson(run.stdout);
      if (!parsed.ok) return runtimeFailure(parsed.message);
      return {
        ok: !parsed.value.runtimeError && run.code === 0,
        compiled: true,
        compileErrors: "",
        ...parsed.value,
        runtimeError: parsed.value.runtimeError || (run.stderr ? beginnerRuntimeMessage(run.stderr) : ""),
      };
    });
  }

  async catalog(): Promise<ObjectiveCatalog> {
    const strategy = await readSampleStrategy();
    return this.withTempEngine(strategy, async (dir) => {
      const compile = await runProcess("javac", javaFiles(await readEngineFiles(strategy)), dir, COMPILE_TIMEOUT_MS);
      if (compile.code !== 0) throw new Error(compile.stderr || compile.stdout);
      const run = await runProcess("java", ["-cp", dir, "Runner", "--catalog"], dir, RUN_TIMEOUT_MS);
      return JSON.parse(run.stdout) as ObjectiveCatalog;
    });
  }

  private async withTempEngine<T>(strategyCode: string, fn: (dir: string) => Promise<T>): Promise<T> {
    const dir = await mkdtemp(path.join(tmpdir(), "farm-bots-run-"));
    try {
      const files = await readEngineFiles(strategyCode);
      await Promise.all(files.map((file) => writeFile(path.join(dir, file.path), file.content)));
      return await fn(dir);
    } finally {
      await rm(dir, { recursive: true, force: true });
    }
  }
}

function javaFiles(files: Array<{ path: string }>): string[] {
  return files.map((file) => file.path).filter((file) => file.endsWith(".java"));
}

async function runProcess(cmd: string, args: string[], cwd: string, timeout: number) {
  try {
    const { stdout, stderr } = await execFileAsync(cmd, args, { cwd, timeout, maxBuffer: 1024 * 1024 });
    return { code: 0, stdout, stderr, timedOut: false };
  } catch (error) {
    const e = error as { code?: number | string; stdout?: string; stderr?: string; killed?: boolean; signal?: string };
    return {
      code: typeof e.code === "number" ? e.code : 1,
      stdout: e.stdout ?? "",
      stderr: e.stderr ?? "",
      timedOut: e.killed || e.signal === "SIGTERM",
    };
  }
}

function beginnerCompileMessage(stderr: string): string {
  return stderr.replace(/Strategy\.java:/g, "Strategy.java line ");
}

function beginnerRuntimeMessage(stderr: string): string {
  return stderr.trim() || "Your code stopped with a runtime error.";
}
