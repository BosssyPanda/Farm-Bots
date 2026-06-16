import { describe, expect, it } from "vitest";
import type { CheckResult, ObjectiveInfo, ObjectiveResult } from "@/lib/types";

type DisplayHelpers = typeof import("@/lib/animate") & {
  getObjectiveCheckDisplay?: (
    currentObjective: Pick<ObjectiveInfo, "id" | "title">,
    result: ObjectiveResult | null,
    resultObjective?: Pick<ObjectiveInfo, "id" | "title"> | null,
  ) => {
    currentChecks: CheckResult[];
    completedChecks: CheckResult[];
    completedTitle: string;
    hasMismatchedCompletedResult: boolean;
  };
  hintLevelForObjective?: (hintState: { objectiveId: string; level: number }, objectiveId: string) => number;
  inspectorValueClassName?: (value: unknown) => string;
};

describe("frontend display helpers", async () => {
  const helpers = (await import("@/lib/animate")) as DisplayHelpers;

  it("keeps completed objective checks separate from the newly displayed objective", () => {
    const getDisplay =
      helpers.getObjectiveCheckDisplay ??
      (() => ({
        currentChecks: [{ id: "plant", label: "Plant wheat on all target tiles", passed: true }],
        completedChecks: [],
        completedTitle: "",
        hasMismatchedCompletedResult: false,
      }));

    const display = getDisplay(
      { id: "the-long-rows", title: "The Long Rows" },
      {
        id: "first-sprout",
        concept: "methods",
        checks: [{ id: "plant", label: "Plant wheat on all target tiles", passed: true }],
        passed: true,
      },
      { id: "first-sprout", title: "First Sprout" },
    );

    expect(display.currentChecks).toEqual([]);
    expect(display.completedChecks).toHaveLength(1);
    expect(display.completedTitle).toBe("First Sprout");
    expect(display.hasMismatchedCompletedResult).toBe(true);
  });

  it("resets progressive hints when the objective changes", () => {
    const hintLevel = helpers.hintLevelForObjective ?? ((state: { level: number }) => state.level);

    expect(hintLevel({ objectiveId: "first-sprout", level: 2 }, "first-sprout")).toBe(2);
    expect(hintLevel({ objectiveId: "first-sprout", level: 2 }, "the-long-rows")).toBe(0);
  });

  it("marks long inspector values as wrappable", () => {
    const className = helpers.inspectorValueClassName ?? (() => "");

    expect(className("short")).toContain("wrap-value");
    expect(className("value-that-should-wrap-inside-the-inspector-without-overflowing")).toContain("wrap-value");
  });
});
