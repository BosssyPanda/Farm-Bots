import type { NextRequest } from "next/server";
import { getRunner } from "@/lib/runner";
import { contractError, errorMessage } from "@/lib/runner/response";
import type { FarmState, RunRequest } from "@/lib/types";

// Node runtime: the real runner will spawn javac/java (LocalRunner) or call
// Vercel Sandbox (SandboxRunner). The skeleton returns mock frames.
export const runtime = "nodejs";

const MAX_BODY_CHARS = 300_000;
const MAX_CODE_CHARS = 140_000;
const MAX_STATE_CHARS = 160_000;

export async function POST(req: NextRequest) {
  try {
    const raw = await req.text();
    if (raw.length > MAX_BODY_CHARS) {
      return Response.json(contractError({ compileErrors: "Request body is too large." }), { status: 413 });
    }

    let body: unknown;
    try {
      body = JSON.parse(raw) as unknown;
    } catch {
      return Response.json(contractError({ compileErrors: "Invalid JSON body." }), { status: 400 });
    }

    const validation = validateRunRequest(body);
    if (!validation.ok) {
      return Response.json(contractError({ compileErrors: validation.message }), { status: validation.status });
    }

    const runner = getRunner();
    const result = await runner.run(validation.request);
    return Response.json(result);
  } catch (error) {
    return Response.json(contractError({
      runtimeError: errorMessage(error),
      compiled: false,
    }), { status: 500 });
  }
}

function validateRunRequest(body: unknown): { ok: true; request: RunRequest } | { ok: false; status: number; message: string } {
  if (!body || typeof body !== "object" || Array.isArray(body)) {
    return { ok: false, status: 400, message: "Request body must be an object with code and optional farmState." };
  }

  const value = body as { code?: unknown; farmState?: unknown };
  if (typeof value.code !== "string") {
    return { ok: false, status: 400, message: "code must be a string." };
  }
  if (value.code.length > MAX_CODE_CHARS) {
    return { ok: false, status: 413, message: "Strategy.java code is too large." };
  }
  if (value.farmState !== undefined && value.farmState !== null && (typeof value.farmState !== "object" || Array.isArray(value.farmState))) {
    return { ok: false, status: 400, message: "farmState must be an object or null." };
  }
  if (value.farmState !== undefined && value.farmState !== null && JSON.stringify(value.farmState).length > MAX_STATE_CHARS) {
    return { ok: false, status: 413, message: "farmState is too large." };
  }

  return {
    ok: true,
    request: {
      code: value.code,
      farmState: (value.farmState ?? null) as FarmState | null,
    },
  };
}
