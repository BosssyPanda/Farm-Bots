import { execFileSync, spawnSync } from "node:child_process";
import { mkdtempSync, copyFileSync, readdirSync, readFileSync, rmSync, writeFileSync } from "node:fs";
import { tmpdir } from "node:os";
import { join } from "node:path";

const root = process.cwd();
const engineDir = join(root, "engine");
const solutionsDir = join(engineDir, "solutions");
const forbiddenPattern =
  /\b(ArrayList|HashMap|HashSet|Map|Set|Collection|Collections|Arrays\.sort|Arrays\.binarySearch|java\.util\.Random|Thread|System\.exit)\b|stream\s*\(|->|::|import\s+/;

const solutions = readdirSync(solutionsDir)
  .filter((name) => name.endsWith(".java"))
  .sort();

if (solutions.length < 9) {
  throw new Error(`Expected at least 9 solutions, found ${solutions.length}`);
}

function compileAndRun(strategySource, objectiveId, stateText = "") {
  const work = mkdtempSync(join(tmpdir(), "farm-bots-engine-"));
  try {
    for (const file of engineJavaFiles()) {
      copyFileSync(join(engineDir, file), join(work, file));
    }
    writeFileSync(join(work, "Strategy.java"), strategySource);
    if (stateText) {
      writeFileSync(join(work, "state.txt"), stateText);
    }
    execFileSync("javac", ["-d", work, ...readdirSync(work).filter((f) => f.endsWith(".java"))], {
      cwd: work,
      stdio: "pipe",
    });
    const args = ["-cp", work, "Runner"];
    if (stateText) args.push("--state", join(work, "state.txt"));
    if (objectiveId) args.push("--objective", objectiveId);
    const output = execFileSync("java", args, { cwd: work, encoding: "utf8" }).trim();
    return JSON.parse(output);
  } finally {
  rmSync(work, { recursive: true, force: true });
  }
}

function engineJavaFiles() {
  return readdirSync(engineDir)
    .filter((file) => /^[A-Za-z_][A-Za-z0-9_]*\.java$/.test(file) && file !== "Strategy.java")
    .sort();
}

for (const file of solutions) {
  const objectiveId = file.replace(/\.java$/, "");
  const source = readFileSync(join(solutionsDir, file), "utf8");
  if (forbiddenPattern.test(source)) {
    throw new Error(`${file} uses a forbidden beginner-Java feature`);
  }
  const result = compileAndRun(source, objectiveId);
  if (!result.objective?.passed) {
    throw new Error(`${file} did not pass ${objectiveId}: ${JSON.stringify(result.objective?.checks)}`);
  }
  if (result.runtimeError) {
    throw new Error(`${file} had runtime error: ${result.runtimeError}`);
  }
  if (["stock-the-stall", "find-the-crop", "fast-market", "tidy-the-stalls", "pick-the-best", "mastery-garden"].includes(objectiveId)) {
    const final = result.frames[result.frames.length - 1];
    if (!final || final.action?.type !== "inspect") {
      throw new Error(`${file} should emit a final zero-tick inspector frame`);
    }
  }
  if (objectiveId === "first-sprout") {
    const methods = result.concepts?.methods;
    if (!methods || methods.correctStreak !== 1 || methods.mastered) {
      throw new Error("A single pass must not immediately master methods");
    }
  }
  console.log(`PASS ${objectiveId}`);
}

function assertFailsObjective(source, objectiveId, label) {
  const result = compileAndRun(source, objectiveId);
  if (result.objective?.passed) {
    throw new Error(`${label} should not pass ${objectiveId}`);
  }
  console.log(`PASS ${label}`);
}

assertFailsObjective(`public class Strategy {
  public void run(Drone drone, Farm farm) {
    drone.watch("foundIndex", 2);
    drone.watch("comparisons", 1);
  }
}`, "find-the-crop", "hard-coded-sequential-search-rejected");

assertFailsObjective(`public class Strategy {
  public void run(Drone drone, Farm farm) {
    drone.watch("foundIndex", 4);
    drone.watch("comparisons", 1);
  }
}`, "fast-market", "hard-coded-binary-search-rejected");

assertFailsObjective(`public class Strategy {
  public void run(Drone drone, Farm farm) {
    drone.watch("sortedPrices", "3,5,7,12,19");
    drone.watch("swaps", 1);
  }
}`, "tidy-the-stalls", "hard-coded-bubble-sort-rejected");

assertFailsObjective(`public class Strategy {
  public void run(Drone drone, Farm farm) {
    drone.watch("rankedPrices", "30,22,14,9,6");
    drone.watch("bestCrop", "CORN");
  }
}`, "pick-the-best", "hard-coded-selection-sort-rejected");

assertFailsObjective(`public class Strategy {
  public void run(Drone drone, Farm farm) {
    farm.crops();
    drone.watch("foundIndex", 2);
    drone.watch("comparisons", 1);
  }
}`, "find-the-crop", "array-read-without-sequential-probes-rejected");

assertFailsObjective(`public class Strategy {
  public void run(Drone drone, Farm farm) {
    farm.prices();
    drone.watch("foundIndex", 4);
    drone.watch("comparisons", 1);
  }
}`, "fast-market", "array-read-without-binary-probes-rejected");

assertFailsObjective(`public class Strategy {
  public void run(Drone drone, Farm farm) {
    farm.prices();
    drone.watch("sortedPrices", "3,5,7,12,19");
    drone.watch("swaps", 1);
  }
}`, "tidy-the-stalls", "array-read-without-bubble-trace-rejected");

assertFailsObjective(`public class Strategy {
  public void run(Drone drone, Farm farm) {
    farm.prices();
    farm.crops();
    drone.watch("rankedPrices", "30,22,14,9,6");
    drone.watch("bestCrop", "CORN");
  }
}`, "pick-the-best", "array-read-without-selection-trace-rejected");

const firstSprout = readFileSync(join(solutionsDir, "first-sprout.java"), "utf8");
const methodsOnce = compileAndRun(firstSprout, "first-sprout");
const methodsTwice = compileAndRun(firstSprout, "first-sprout", methodsOnce.farmState.stateCodec);
const methodsThrice = compileAndRun(firstSprout, "first-sprout", methodsTwice.farmState.stateCodec);
if (methodsTwice.concepts.methods.mastered || methodsTwice.concepts.methods.correctStreak !== 2) {
  throw new Error("Methods should not be mastered after two correct uses");
}
if (!methodsThrice.concepts.methods.mastered || methodsThrice.concepts.methods.correctStreak !== 3) {
  throw new Error("Methods should be mastered after three correct uses");
}
console.log("PASS mastery-requires-three-correct-uses");

const stockTheStall = readFileSync(join(solutionsDir, "stock-the-stall.java"), "utf8");
const recapState = [
  "version=1",
  "currentObjectiveId=fast-market",
  "width=8",
  "height=5",
  "tick=0",
  "tiles=",
  "resources=NONE:0,WHEAT:0,CORN:0,PUMPKIN:0,CARROT:0",
  "unlocked=basic-planting,bigger-field,market-stall,irrigation,crop-locator",
  "concepts=arrays:0:3:false:true",
  "",
].join("\n");
const recap = compileAndRun(stockTheStall, "", recapState);
if (recap.objective?.id !== "stock-the-stall" || !recap.objective?.passed) {
  throw new Error("A recap-due concept should interrupt the later objective");
}
if (recap.farmState.currentObjectiveId !== "fast-market") {
  throw new Error("Passing a recap should resume the interrupted objective");
}
if (recap.concepts.arrays.failCount !== 0 || recap.concepts.arrays.recapDue) {
  throw new Error("Passing a recap should clear failure count and recapDue");
}
console.log("PASS recap-interrupts-and-resumes");

const occupiedPlant = `public class Strategy {
  public void run(Drone drone, Farm farm) {
    drone.plant(Crop.WHEAT);
    drone.plant(Crop.CORN);
  }
}`;
const occupiedResult = compileAndRun(occupiedPlant, "first-sprout");
if (!occupiedResult.runtimeError.includes("already has WHEAT")) {
  throw new Error("Occupied planting should produce a clear runtime error");
}
console.log("PASS occupied-planting-error");

const illegalMove = `public class Strategy {
  public void run(Drone drone, Farm farm) {
    drone.moveWest();
  }
}`;
const illegalResult = compileAndRun(illegalMove, "first-sprout");
if (!illegalResult.runtimeError.includes("Illegal move")) {
  throw new Error("Illegal movement should produce a clear runtime error");
}
console.log("PASS illegal-move-error");

const tightLoop = `public class Strategy {
  public void run(Drone drone, Farm farm) {
    while (true) {
      int x = 1 + 1;
    }
  }
}`;
const work = mkdtempSync(join(tmpdir(), "farm-bots-tight-loop-"));
try {
  for (const file of engineJavaFiles()) {
    copyFileSync(join(engineDir, file), join(work, file));
  }
  writeFileSync(join(work, "Strategy.java"), tightLoop);
  execFileSync("javac", ["-d", work, ...readdirSync(work).filter((f) => f.endsWith(".java"))], {
    cwd: work,
    stdio: "pipe",
  });
  const run = spawnSync("java", ["-cp", work, "Runner"], {
    cwd: work,
    encoding: "utf8",
    timeout: 1200,
  });
  if (run.error?.code !== "ETIMEDOUT") {
    throw new Error("Tight non-action loop should be killed by the process timeout in the runner layer");
  }
  console.log("PASS tight-loop-timeout");
} finally {
  rmSync(work, { recursive: true, force: true });
}
