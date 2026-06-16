import { afterEach, describe, expect, it, vi } from "vitest";
import type { RunResponse } from "@/lib/types";

function response(overrides: Partial<RunResponse> = {}): RunResponse {
  return {
    ok: true,
    compiled: true,
    compileErrors: "",
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
  };
}

async function importPostWithRunner(run: () => Promise<RunResponse>) {
  vi.resetModules();
  vi.doMock("@/lib/runner", () => ({
    getRunner: () => ({
      run,
      catalog: vi.fn(),
    }),
  }));
  return (await import("@/app/api/run/route")).POST;
}

async function parse(res: Response) {
  return {
    status: res.status,
    body: await res.json() as RunResponse,
  };
}

describe("POST /api/run", () => {
  afterEach(() => {
    vi.doUnmock("@/lib/runner");
    vi.resetModules();
  });

  it("returns a contract-shaped 400 for malformed JSON", async () => {
    const POST = await importPostWithRunner(vi.fn());
    const res = await POST(new Request("http://localhost/api/run", {
      method: "POST",
      body: "{",
      headers: { "content-type": "application/json" },
    }) as never);
    const parsed = await parse(res);

    expect(parsed.status).toBe(400);
    expect(parsed.body.ok).toBe(false);
    expect(parsed.body.compiled).toBe(false);
    expect(parsed.body.compileErrors).toMatch(/invalid json/i);
    expect(parsed.body.frames).toEqual([]);
    expect(parsed.body.farmState.currentObjectiveId).toBe("first-sprout");
  });

  it("validates request shape before invoking the runner", async () => {
    const run = vi.fn();
    const POST = await importPostWithRunner(run);
    const res = await POST(new Request("http://localhost/api/run", {
      method: "POST",
      body: JSON.stringify({ farmState: null }),
      headers: { "content-type": "application/json" },
    }) as never);
    const parsed = await parse(res);

    expect(parsed.status).toBe(400);
    expect(parsed.body.compileErrors).toMatch(/code must be a string/i);
    expect(run).not.toHaveBeenCalled();
  });

  it("rejects oversized code with 413 before invoking the runner", async () => {
    const run = vi.fn();
    const POST = await importPostWithRunner(run);
    const res = await POST(new Request("http://localhost/api/run", {
      method: "POST",
      body: JSON.stringify({ code: "x".repeat(140_001), farmState: null }),
      headers: { "content-type": "application/json" },
    }) as never);
    const parsed = await parse(res);

    expect(parsed.status).toBe(413);
    expect(parsed.body.compileErrors).toMatch(/too large/i);
    expect(run).not.toHaveBeenCalled();
  });

  it("wraps unexpected runner errors in the run contract", async () => {
    const POST = await importPostWithRunner(vi.fn(async () => {
      throw new Error("sandbox quota unavailable");
    }));
    const res = await POST(new Request("http://localhost/api/run", {
      method: "POST",
      body: JSON.stringify({ code: "public class Strategy { public void run(Drone drone, Farm farm) {} }", farmState: null }),
      headers: { "content-type": "application/json" },
    }) as never);
    const parsed = await parse(res);

    expect(parsed.status).toBe(500);
    expect(parsed.body.ok).toBe(false);
    expect(parsed.body.compiled).toBe(false);
    expect(parsed.body.runtimeError).toMatch(/sandbox quota unavailable/i);
    expect(parsed.body.objective).toEqual({ id: "", concept: "", checks: [], passed: false });
  });

  it("passes valid requests through to the selected runner", async () => {
    const run = vi.fn(async () => response({ ok: true, objective: { id: "first-sprout", concept: "methods", checks: [], passed: true } }));
    const POST = await importPostWithRunner(run);
    const res = await POST(new Request("http://localhost/api/run", {
      method: "POST",
      body: JSON.stringify({ code: "public class Strategy { public void run(Drone drone, Farm farm) {} }", farmState: null }),
      headers: { "content-type": "application/json" },
    }) as never);
    const parsed = await parse(res);

    expect(parsed.status).toBe(200);
    expect(parsed.body.ok).toBe(true);
    expect(run).toHaveBeenCalledOnce();
  });
});
