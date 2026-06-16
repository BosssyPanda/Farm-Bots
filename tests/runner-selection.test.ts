import { afterEach, describe, expect, it, vi } from "vitest";

async function loadGetRunner() {
  vi.resetModules();
  return import("@/lib/runner");
}

describe("getRunner", () => {
  afterEach(() => {
    vi.unstubAllEnvs();
    vi.resetModules();
  });

  it("rejects local and mock runners in production", async () => {
    vi.stubEnv("NODE_ENV", "production");

    vi.stubEnv("FARM_BOTS_RUNNER", "local");
    await expect(async () => (await loadGetRunner()).getRunner()).rejects.toThrow(/cannot use FARM_BOTS_RUNNER=local/i);

    vi.stubEnv("FARM_BOTS_RUNNER", "mock");
    await expect(async () => (await loadGetRunner()).getRunner()).rejects.toThrow(/cannot use FARM_BOTS_RUNNER=mock/i);
  });

  it("defaults production to the sandbox runner", async () => {
    vi.stubEnv("NODE_ENV", "production");
    vi.stubEnv("FARM_BOTS_RUNNER", undefined);

    const { getRunner } = await loadGetRunner();
    expect(getRunner().constructor.name).toBe("SandboxRunner");
  });
});
