import type { JavaRunner } from "./index";
import type { ObjectiveCatalog, RunResponse } from "../types";
import { sampleRunResponse } from "../sampleFrames";
import { FALLBACK_CATALOG } from "../types";

/**
 * Skeleton runner: ignores the submitted code and replays a canned successful run
 * so the whole request -> response -> animation pipeline is exercised end to end
 * without a JVM. Replaced by LocalRunner / SandboxRunner once real execution is wired.
 */
export class MockRunner implements JavaRunner {
  async run(): Promise<RunResponse> {
    return sampleRunResponse;
  }

  async catalog(): Promise<ObjectiveCatalog> {
    return FALLBACK_CATALOG;
  }
}
