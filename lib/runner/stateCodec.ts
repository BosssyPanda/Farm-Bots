import type { FarmState } from "@/lib/types";

export function encodeFarmState(state: FarmState | null | undefined): string {
  if (!state) return "";
  const safe = sanitizeFarmState(state);

  const rows: string[] = [];
  for (let y = 0; y < safe.height; y++) {
    const cells: string[] = [];
    for (let x = 0; x < safe.width; x++) {
      const tile = safe.tiles.find((t) => t.x === x && t.y === y);
      cells.push(`${tile?.crop ?? "NONE"}:${tile?.plantedTick ?? -1}:${tile?.moisture ?? 0}`);
    }
    rows.push(cells.join(","));
  }

  const resources = CROPS
    .map((crop) => `${crop}:${safe.resources[crop] ?? 0}`)
    .join(",");

  const concepts = Object.entries(safe.concepts)
    .map(([id, value]) => `${id}:${value.correctStreak}:${value.failCount}:${value.mastered}:${value.recapDue}`)
    .join(",");

  return [
    "version=1",
    `currentObjectiveId=${safe.currentObjectiveId}`,
    `width=${safe.width}`,
    `height=${safe.height}`,
    `tick=${safe.tick}`,
    `tiles=${rows.join("|")}`,
    `resources=${resources}`,
    `unlocked=${safe.unlocked.join(",")}`,
    `concepts=${concepts}`,
    "",
  ].join("\n");
}

const OBJECTIVES = [
  "first-sprout",
  "the-long-rows",
  "stock-the-stall",
  "harvest-til-done",
  "find-the-crop",
  "fast-market",
  "tidy-the-stalls",
  "pick-the-best",
  "mastery-garden",
] as const;

const OBJECTIVE_UNLOCKS = [
  "basic-planting",
  "bigger-field",
  "market-stall",
  "irrigation",
  "crop-locator",
  "fast-lookup",
  "sorted-market-view",
  "auto-prioritize",
  "recursion-puzzles",
] as const;

const CORE_OBJECTIVE_UNLOCKS = OBJECTIVE_UNLOCKS.slice(0, 8);

const CONCEPTS = [
  "methods",
  "for-loops",
  "arrays",
  "while-loops",
  "sequential-search",
  "binary-search",
  "bubble-sort",
  "selection-sort",
] as const;

const CROPS = ["NONE", "WHEAT", "CORN", "PUMPKIN", "CARROT"] as const;

const objectiveSet = new Set<string>(OBJECTIVES);
const unlockSet = new Set<string>(OBJECTIVE_UNLOCKS);
const cropSet = new Set<string>(CROPS);

export function sanitizeFarmState(state: FarmState): FarmState {
  const raw: Record<string, unknown> = isRecord(state) ? state : {};
  const width = clampInt(raw.width, 6, 1, 12);
  const height = clampInt(raw.height, 4, 1, 12);
  const tick = clampInt(raw.tick, 0, 0, 1_000_000);
  const rawTiles = Array.isArray(raw.tiles) ? raw.tiles : [];
  const tiles = rawTiles
    .map((tile) => {
      if (!isRecord(tile)) return null;
      return {
        x: clampInt(tile.x, -1, -1, width - 1),
        y: clampInt(tile.y, -1, -1, height - 1),
        crop: typeof tile.crop === "string" && cropSet.has(tile.crop) ? tile.crop : "NONE",
        plantedTick: clampInt(tile.plantedTick, -1, -1, tick),
        ripe: Boolean(tile.ripe),
        moisture: clampInt(tile.moisture, 0, 0, 100),
      };
    })
    .filter((tile): tile is FarmState["tiles"][number] => Boolean(tile))
    .filter((tile, index, all) => tile.x >= 0 && tile.y >= 0 && all.findIndex((candidate) => candidate.x === tile.x && candidate.y === tile.y) === index);

  const resources: Record<string, number> = {};
  const rawResources = isRecord(raw.resources) ? raw.resources : {};
  for (const crop of CROPS) {
    resources[crop] = clampInt(rawResources[crop], 0, 0, 999_999);
  }

  const unlocked: string[] = Array.isArray(raw.unlocked)
    ? Array.from(new Set(raw.unlocked.filter((unlock): unlock is string => typeof unlock === "string" && unlockSet.has(unlock))))
    : [];

  const concepts: FarmState["concepts"] = {};
  const rawConcepts = isRecord(raw.concepts) ? raw.concepts : {};
  for (const concept of CONCEPTS) {
    const value = rawConcepts[concept];
    if (!isRecord(value)) continue;
    const correctStreak = clampInt(value.correctStreak, 0, 0, 3);
    const failCount = clampInt(value.failCount, 0, 0, 3);
    concepts[concept] = {
      correctStreak,
      failCount,
      mastered: correctStreak >= 3 && failCount === 0,
      recapDue: failCount >= 3,
    };
  }

  const currentObjectiveId = typeof raw.currentObjectiveId === "string" && objectiveSet.has(raw.currentObjectiveId)
    ? raw.currentObjectiveId
    : "first-sprout";
  const safeCurrentObjectiveId = clampObjectiveToProgress(currentObjectiveId, unlocked, concepts);

  return {
    version: 1,
    currentObjectiveId: safeCurrentObjectiveId,
    width,
    height,
    tick,
    tiles,
    resources,
    unlocked,
    concepts,
  };
}

function clampObjectiveToProgress(
  requestedObjectiveId: string,
  unlocked: string[],
  concepts: FarmState["concepts"],
): string {
  const requestedIndex = OBJECTIVES.indexOf(requestedObjectiveId as (typeof OBJECTIVES)[number]);
  const contiguousUnlocks = countContiguousUnlocks(unlocked);
  const allCoreMastered = CONCEPTS.every((concept) => concepts[concept]?.mastered);
  const maxIndex = allCoreMastered && contiguousUnlocks >= CORE_OBJECTIVE_UNLOCKS.length ? OBJECTIVES.length - 1 : Math.min(contiguousUnlocks, CORE_OBJECTIVE_UNLOCKS.length - 1);
  const clampedIndex = Math.max(0, Math.min(requestedIndex < 0 ? 0 : requestedIndex, maxIndex));
  return OBJECTIVES[clampedIndex];
}

function countContiguousUnlocks(unlocked: string[]): number {
  const unlockedSet = new Set(unlocked);
  let count = 0;
  for (const unlock of CORE_OBJECTIVE_UNLOCKS) {
    if (!unlockedSet.has(unlock)) break;
    count++;
  }
  return count;
}

function isRecord(value: unknown): value is Record<string, unknown> {
  return Boolean(value && typeof value === "object" && !Array.isArray(value));
}

function clampInt(raw: unknown, fallback: number, min: number, max: number): number {
  const value = typeof raw === "number" && Number.isFinite(raw) ? Math.trunc(raw) : fallback;
  return Math.max(min, Math.min(max, value));
}
