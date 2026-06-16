// Pure helper: fold the frame stream up to a playback index into the farm state
// the UI should render (drone position, planted tiles, tick, watched vars,
// resources). The browser advances the index on a timer to animate.

import type { Action, CheckResult, Coord, Frame, ObjectiveInfo, ObjectiveResult } from "./types";
import type { FarmState } from "./types";

export interface DerivedState {
  drone: DroneSnapshot;
  planted: Map<string, string>; // "x,y" -> crop name
  tileStates: Map<string, RenderTileState>;
  tick: number;
  watch: Record<string, number | boolean | string>;
  resources: Record<string, number>;
  lastAction: Action | null;
  path: Coord[];
}

interface DroneSnapshot {
  x: number;
  y: number;
  carrying: string;
}

export type CropStage = "planted" | "growing" | "ripe";

export interface RenderTileState {
  crop: string;
  plantedTick: number;
  ripe: boolean;
  moisture: number;
  stage: CropStage;
  action?: "plant" | "harvest";
}

export interface ObjectiveCheckDisplay {
  currentChecks: CheckResult[];
  completedChecks: CheckResult[];
  completedTitle: string;
  hasMismatchedCompletedResult: boolean;
}

const CROP_GROW_TICKS: Record<string, number> = {
  WHEAT: 4,
  CORN: 6,
  PUMPKIN: 10,
  CARROT: 5,
};

export function deriveState(farmState: FarmState, frames: Frame[], index: number): DerivedState {
  const planted = new Map<string, string>();
  const tileStates = new Map<string, RenderTileState>();
  for (const tile of farmState.tiles) {
    if (tile.crop && tile.crop !== "NONE") {
      const key = `${tile.x},${tile.y}`;
      planted.set(key, tile.crop);
      tileStates.set(key, {
        crop: tile.crop,
        plantedTick: tile.plantedTick,
        ripe: tile.ripe,
        moisture: tile.moisture,
        stage: stageForCrop(tile.crop, tile.plantedTick, farmState.tick, tile.ripe),
      });
    }
  }

  let drone: DroneSnapshot = { x: 0, y: 0, carrying: "NONE" };
  let tick = farmState.tick;
  let watch: Record<string, number | boolean | string> = {};
  let resources: Record<string, number> = { ...farmState.resources };
  let lastAction: Action | null = null;
  const path: Coord[] = [[drone.x, drone.y]];

  for (let i = 0; i <= index && i < frames.length; i++) {
    const f = frames[i];
    drone = f.drone;
    tick = f.tick;
    lastAction = f.action;
    if (f.watch) watch = f.watch;
    if (f.resources) resources = f.resources;
    if (f.action.type === "plant") {
      const key = `${f.action.at[0]},${f.action.at[1]}`;
      planted.set(key, f.action.crop);
      tileStates.set(key, {
        crop: f.action.crop,
        plantedTick: f.tick,
        ripe: false,
        moisture: tileStates.get(key)?.moisture ?? 0,
        stage: "planted",
        action: "plant",
      });
    } else if (f.action.type === "harvest") {
      const key = `${f.action.at[0]},${f.action.at[1]}`;
      planted.delete(key);
      tileStates.delete(key);
    } else if (f.action.type === "move") {
      const last = path[path.length - 1];
      if (!last || last[0] !== f.action.to[0] || last[1] !== f.action.to[1]) {
        path.push(f.action.to);
      }
    }
  }

  for (const [key, tile] of tileStates) {
    const stage = stageForCrop(tile.crop, tile.plantedTick, tick, tile.ripe);
    tileStates.set(key, {
      ...tile,
      ripe: stage === "ripe",
      stage,
    });
  }

  return { drone, planted, tileStates, tick, watch, resources, lastAction, path };
}

export function getObjectiveCheckDisplay(
  currentObjective: Pick<ObjectiveInfo, "id" | "title">,
  result: ObjectiveResult | null,
  resultObjective?: Pick<ObjectiveInfo, "id" | "title"> | null,
): ObjectiveCheckDisplay {
  if (!result) {
    return {
      currentChecks: [],
      completedChecks: [],
      completedTitle: "",
      hasMismatchedCompletedResult: false,
    };
  }

  if (result.id === currentObjective.id) {
    return {
      currentChecks: result.checks,
      completedChecks: [],
      completedTitle: "",
      hasMismatchedCompletedResult: false,
    };
  }

  return {
    currentChecks: [],
    completedChecks: result.checks,
    completedTitle: resultObjective?.title ?? result.id,
    hasMismatchedCompletedResult: true,
  };
}

export function hintLevelForObjective(hintState: { objectiveId: string; level: number }, objectiveId: string): number {
  return hintState.objectiveId === objectiveId ? hintState.level : 0;
}

export function inspectorValueClassName(_value: unknown): string {
  void _value;
  return "wrap-value";
}

function stageForCrop(crop: string, plantedTick: number, currentTick: number, forcedRipe: boolean): CropStage {
  if (forcedRipe) return "ripe";
  const growTicks = CROP_GROW_TICKS[crop] ?? 5;
  const age = currentTick - plantedTick;
  if (age >= growTicks) return "ripe";
  if (age <= 0) return "planted";
  return "growing";
}
