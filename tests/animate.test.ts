import { describe, expect, it } from "vitest";
import { deriveState } from "@/lib/animate";
import { createDefaultFarmState } from "@/lib/persist";
import type { Frame } from "@/lib/types";

describe("deriveState", () => {
  it("folds frame actions into drone, crop, watch, and resource state", () => {
    const farmState = createDefaultFarmState("first-sprout");
    farmState.tick = 12;
    farmState.resources = { WHEAT: 2 };
    farmState.tiles = [{ x: 0, y: 0, crop: "WHEAT", plantedTick: 6, ripe: true, moisture: 3 }];

    const frames: Frame[] = [
      { tick: 13, action: { type: "move", dir: "EAST", to: [1, 0] }, drone: { x: 1, y: 0, carrying: "NONE" } },
      { tick: 15, action: { type: "plant", at: [1, 0], crop: "CORN" }, drone: { x: 1, y: 0, carrying: "NONE" }, watch: { i: 0 } },
      { tick: 21, action: { type: "harvest", at: [1, 0], crop: "CORN" }, drone: { x: 1, y: 0, carrying: "CORN" }, resources: { WHEAT: 3 } },
    ];

    const state = deriveState(farmState, frames, 2);

    expect(state.tick).toBe(21);
    expect(state.drone).toEqual({ x: 1, y: 0, carrying: "CORN" });
    expect(state.planted.has("0,0")).toBe(true);
    expect(state.planted.get("0,0")).toBe("WHEAT");
    expect(state.planted.has("1,0")).toBe(false);
    expect(state.watch).toEqual({ i: 0 });
    expect(state.resources).toEqual({ WHEAT: 3 });
  });

  it("keeps future frame crops out of the pre-run baseline", () => {
    const farmState = createDefaultFarmState("first-sprout");
    const frames: Frame[] = [
      {
        tick: 2,
        action: { type: "plant", at: [1, 0], crop: "WHEAT" },
        drone: { x: 1, y: 0, carrying: "NONE" },
        watch: { planted: 1 },
      },
    ];

    const state = deriveState(farmState, frames, -1);

    expect(state.planted.has("1,0")).toBe(false);
    expect(state.tileStates?.has("1,0")).toBe(false);
    expect(state.tick).toBe(0);
  });

  it("derives tile stages, the active action, and the traversed path from frames", () => {
    const farmState = createDefaultFarmState("first-sprout");
    farmState.tiles = [{ x: 0, y: 0, crop: "WHEAT", plantedTick: 0, ripe: true, moisture: 4 }];

    const frames: Frame[] = [
      { tick: 1, action: { type: "move", dir: "EAST", to: [1, 0] }, drone: { x: 1, y: 0, carrying: "NONE" } },
      { tick: 3, action: { type: "plant", at: [1, 0], crop: "CORN" }, drone: { x: 1, y: 0, carrying: "NONE" } },
    ];

    const state = deriveState(farmState, frames, 1);

    expect(state.tileStates?.get("0,0")).toMatchObject({ crop: "WHEAT", stage: "ripe", ripe: true });
    expect(state.tileStates?.get("1,0")).toMatchObject({ crop: "CORN", stage: "planted", action: "plant" });
    expect(state.lastAction).toEqual(frames[1].action);
    expect(state.path).toEqual([
      [0, 0],
      [1, 0],
    ]);
  });

  it("ages replay-planted crops as ticks advance before final farm-state commit", () => {
    const farmState = createDefaultFarmState("first-sprout");
    const frames: Frame[] = [
      { tick: 1, action: { type: "plant", at: [0, 0], crop: "WHEAT" }, drone: { x: 0, y: 0, carrying: "NONE" } },
      { tick: 2, action: { type: "move", dir: "EAST", to: [1, 0] }, drone: { x: 1, y: 0, carrying: "NONE" } },
      { tick: 5, action: { type: "move", dir: "WEST", to: [0, 0] }, drone: { x: 0, y: 0, carrying: "NONE" } },
    ];

    expect(deriveState(farmState, frames, 0).tileStates.get("0,0")).toMatchObject({ stage: "planted", ripe: false });
    expect(deriveState(farmState, frames, 1).tileStates.get("0,0")).toMatchObject({ stage: "growing", ripe: false });
    expect(deriveState(farmState, frames, 2).tileStates.get("0,0")).toMatchObject({ stage: "ripe", ripe: true });
  });
});
