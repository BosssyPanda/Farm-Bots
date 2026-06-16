// Canned frame stream + run response so the UI renders and animates without
// invoking Java. This is the MOCK the skeleton's /api/run returns. Once the
// LocalRunner is wired, the route returns real engine output instead.

import type { Frame, RunResponse } from "./types";
import { FARM_WIDTH, FARM_HEIGHT } from "./types";

export const sampleFrames: Frame[] = [
  { tick: 1, action: { type: "move", dir: "EAST", to: [1, 0] }, drone: { x: 1, y: 0, carrying: "NONE" } },
  { tick: 3, action: { type: "plant", at: [1, 0], crop: "WHEAT" }, drone: { x: 1, y: 0, carrying: "NONE" }, watch: { planted: 1 } },
  { tick: 4, action: { type: "move", dir: "EAST", to: [2, 0] }, drone: { x: 2, y: 0, carrying: "NONE" }, watch: { planted: 1 } },
  { tick: 6, action: { type: "plant", at: [2, 0], crop: "WHEAT" }, drone: { x: 2, y: 0, carrying: "NONE" }, watch: { planted: 2 } },
  { tick: 7, action: { type: "move", dir: "EAST", to: [3, 0] }, drone: { x: 3, y: 0, carrying: "NONE" }, watch: { planted: 2 } },
  { tick: 9, action: { type: "plant", at: [3, 0], crop: "WHEAT" }, drone: { x: 3, y: 0, carrying: "NONE" }, watch: { planted: 3 } },
];

export const sampleRunResponse: RunResponse = {
  ok: true,
  compiled: true,
  compileErrors: "",
  runtimeError: "",
  stdout: "",
  ticks: 9,
  tickLimit: 5000,
  frames: sampleFrames,
  farmState: {
    version: 1,
    currentObjectiveId: "the-long-rows",
    width: FARM_WIDTH,
    height: FARM_HEIGHT,
    tick: 9,
    tiles: [],
    resources: {},
    unlocked: ["basic-planting"],
    concepts: { methods: { correctStreak: 3, mastered: true, failCount: 0, recapDue: false } },
  },
  objective: {
    id: "first-sprout",
    concept: "methods",
    checks: [{ id: "plant-targets", label: "Plant a crop on all 3 target tiles (3/3)", passed: true }],
    passed: true,
  },
  unlocked: [],
  concepts: { methods: { correctStreak: 3, mastered: true, failCount: 0, recapDue: false } },
};
