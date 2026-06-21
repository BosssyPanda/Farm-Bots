// Skill Drills — short active-recall challenges per concept.
//
// Two formats: "predict" (read code, pick the output / true statement — checked
// instantly in the browser) and "write-line" (type one line; we compile + run it
// for real via the existing /api/run with a THROWAWAY farm state and read only
// compiled/stdout — the returned state is never committed). No engine, runner,
// or contract changes. Concept keys match lib/curriculum.ts + engine concepts.

import { buildStrategySource, createDefaultFarmState, emptyConceptPractice } from "./persist";
import type { ConceptPractice, PracticeState } from "./persist";
import type { RunResponse } from "./types";

export type DrillKind = "predict" | "write-line";

export interface PredictDrill {
  id: string;
  concept: string;
  kind: "predict";
  difficulty: 1 | 2 | 3;
  prompt: string;
  /** optional code snippet to read */
  code?: string;
  choices: string[];
  answerIndex: number;
  explain: string;
}

export interface WriteLineDrill {
  id: string;
  concept: string;
  kind: "write-line";
  difficulty: 1 | 2 | 3;
  prompt: string;
  /** full Strategy body with a single __LINE__ placeholder for the learner's line */
  template: string;
  /** light scaffold prefilled into the input (kept intentionally incomplete) */
  starter: string;
  /** regex source the typed line must match (proves the concept was used) */
  requirePattern: string;
  /** plain-English description of what must be used */
  patternHint: string;
  /** expected trimmed stdout when correct */
  expectStdout?: string;
  explain: string;
}

export type Drill = PredictDrill | WriteLineDrill;

const XP_PREDICT = 10;
const XP_WRITE = 20;
/** correct answers that fill a concept's practice mastery bar */
export const MASTERY_TARGET = 6;

// ----------------------------------------------------------------- content
const DRILLS: Drill[] = [
  // ---- methods ----
  {
    id: "methods-p1", concept: "methods", kind: "predict", difficulty: 1,
    prompt: "What does this print?",
    code: "int dbl(int x) { return x * 2; }\n// ...\nSystem.out.println(dbl(5) + 1);",
    choices: ["10", "11", "12", "52"], answerIndex: 1,
    explain: "dbl(5) returns 10, then + 1 makes 11. The method's return value is used in the expression.",
  },
  {
    id: "methods-p2", concept: "methods", kind: "predict", difficulty: 2,
    prompt: "What does this print?",
    code: "int add(int a, int b) { return a + b; }\n// ...\nSystem.out.println(add(add(1, 2), 4));",
    choices: ["6", "7", "3", "124"], answerIndex: 1,
    explain: "Inner add(1,2) is 3, then add(3,4) is 7. Calls evaluate inside-out.",
  },
  {
    id: "methods-w1", concept: "methods", kind: "write-line", difficulty: 2,
    prompt: "Finish the method so dbl(5) returns 10 (it should print 10).",
    template: "public void run(Drone drone, Farm farm) {\n    System.out.println(dbl(5));\n}\n\nint dbl(int x) {\n    __LINE__\n}",
    starter: "return x * ?;",
    requirePattern: "return", patternHint: "a return statement",
    expectStdout: "10",
    explain: "A method gives a value back with return. return x * 2 sends 10 back to the caller.",
  },

  // ---- for-loops ----
  {
    id: "for-p1", concept: "for-loops", kind: "predict", difficulty: 1,
    prompt: "What does this print?",
    code: "int s = 0;\nfor (int i = 1; i <= 3; i++) { s += i; }\nSystem.out.println(s);",
    choices: ["3", "6", "9", "10"], answerIndex: 1,
    explain: "The loop adds 1 + 2 + 3 into s, giving 6.",
  },
  {
    id: "for-p2", concept: "for-loops", kind: "predict", difficulty: 1,
    prompt: "What does this print?",
    code: "for (int i = 0; i < 3; i++) { System.out.print(i); }",
    choices: ["123", "012", "0 1 2", "0123"], answerIndex: 1,
    explain: "i runs 0, 1, 2 (stops at < 3). print (no ln) joins them: 012.",
  },
  {
    id: "for-w1", concept: "for-loops", kind: "write-line", difficulty: 2,
    prompt: "Write the for-loop header so it prints 0 1 2 3 4 (each on its own line).",
    template: "public void run(Drone drone, Farm farm) {\n    __LINE__\n        System.out.println(i);\n}",
    starter: "for (int i = 0; i < ?; i++)",
    requirePattern: "for\\s*\\(", patternHint: "a for loop (for ( ... ))",
    expectStdout: "0\n1\n2\n3\n4",
    explain: "for (int i = 0; i < 5; i++) runs i from 0 up to 4. Using < 5 (not <= 5) stops at 4.",
  },

  // ---- while-loops ----
  {
    id: "while-p1", concept: "while-loops", kind: "predict", difficulty: 2,
    prompt: "What does this print?",
    code: "int n = 8, c = 0;\nwhile (n > 1) { n = n / 2; c++; }\nSystem.out.println(c);",
    choices: ["2", "3", "4", "8"], answerIndex: 1,
    explain: "8 -> 4 -> 2 -> 1 is three halvings, so c is 3.",
  },
  {
    id: "while-p2", concept: "while-loops", kind: "predict", difficulty: 1,
    prompt: "What does this print?",
    code: "int n = 234;\nSystem.out.println(n % 10);",
    choices: ["2", "3", "4", "234"], answerIndex: 2,
    explain: "% 10 gives the remainder after dividing by 10 — the last digit, 4.",
  },
  {
    id: "while-w1", concept: "while-loops", kind: "write-line", difficulty: 2,
    prompt: "Fill the while condition so it prints 0 1 2 3 4.",
    template: "public void run(Drone drone, Farm farm) {\n    int i = 0;\n    while (__LINE__) {\n        System.out.println(i);\n        i++;\n    }\n}",
    starter: "i < ?",
    requirePattern: "<", patternHint: "a comparison like i < 5",
    expectStdout: "0\n1\n2\n3\n4",
    explain: "i < 5 keeps the loop going while i is 0..4, then stops. i++ each pass makes it end.",
  },

  // ---- arrays ----
  {
    id: "arrays-p1", concept: "arrays", kind: "predict", difficulty: 1,
    prompt: "What does this print?",
    code: "int[] a = {4, 7, 2, 9};\nSystem.out.println(a[1] + a[3]);",
    choices: ["6", "11", "16", "13"], answerIndex: 2,
    explain: "a[1] is 7 and a[3] is 9 (indexes start at 0), so 7 + 9 = 16.",
  },
  {
    id: "arrays-p2", concept: "arrays", kind: "predict", difficulty: 1,
    prompt: "What does this print?",
    code: "int[] a = {5, 5, 5};\nSystem.out.println(a.length);",
    choices: ["2", "3", "5", "15"], answerIndex: 1,
    explain: ".length is how many elements there are (3), not their values.",
  },
  {
    id: "arrays-w1", concept: "arrays", kind: "write-line", difficulty: 2,
    prompt: "Print the sum of the first and last elements of a (should print 13).",
    template: "public void run(Drone drone, Farm farm) {\n    int[] a = {4, 7, 2, 9};\n    __LINE__\n}",
    starter: "System.out.println(a[0] + a[?]);",
    requirePattern: "a\\[", patternHint: "array indexing like a[0]",
    expectStdout: "13",
    explain: "The last index is length - 1 = 3. a[0] + a[3] is 4 + 9 = 13.",
  },

  // ---- sequential-search ----
  {
    id: "seq-p1", concept: "sequential-search", kind: "predict", difficulty: 2,
    prompt: "What does this print?",
    code: "String[] c = {\"WHEAT\", \"CORN\", \"PUMPKIN\"};\nint idx = -1;\nfor (int i = 0; i < c.length; i++) {\n    if (c[i].equals(\"CORN\")) { idx = i; break; }\n}\nSystem.out.println(idx);",
    choices: ["-1", "0", "1", "2"], answerIndex: 2,
    explain: "CORN is at index 1; the loop finds it and breaks, so idx is 1.",
  },
  {
    id: "seq-p2", concept: "sequential-search", kind: "predict", difficulty: 2,
    prompt: "The target isn't in the array. What does this print?",
    code: "String[] c = {\"WHEAT\", \"CORN\"};\nint idx = -1;\nfor (int i = 0; i < c.length; i++) {\n    if (c[i].equals(\"CARROT\")) { idx = i; break; }\n}\nSystem.out.println(idx);",
    choices: ["-1", "0", "1", "2"], answerIndex: 0,
    explain: "No element matches, so idx keeps its starting value -1 — the 'not found' signal.",
  },

  // ---- binary-search ----
  {
    id: "bin-p1", concept: "binary-search", kind: "predict", difficulty: 2,
    prompt: "What is the first mid value looked at?",
    code: "int[] a = {1, 3, 5, 7, 9};\nint lo = 0, hi = 4;\nint mid = (lo + hi) / 2;\nSystem.out.println(a[mid]);",
    choices: ["1", "5", "7", "9"], answerIndex: 1,
    explain: "mid = (0 + 4) / 2 = 2, and a[2] is 5 — binary search always starts in the middle.",
  },
  {
    id: "bin-p2", concept: "binary-search", kind: "predict", difficulty: 3,
    prompt: "Searching for 9: after a[mid]=5 is too small, what does low become? (lo=0, hi=4, mid=2)",
    choices: ["1", "2", "3", "4"], answerIndex: 2,
    explain: "Too small means discard the left half: low = mid + 1 = 3. The window halves each step.",
  },

  // ---- bubble-sort ----
  {
    id: "bubble-p1", concept: "bubble-sort", kind: "predict", difficulty: 2,
    prompt: "What does this print after one pass?",
    code: "int[] a = {3, 1, 2};\nfor (int i = 0; i < a.length - 1; i++) {\n    if (a[i] > a[i + 1]) { int t = a[i]; a[i] = a[i + 1]; a[i + 1] = t; }\n}\nSystem.out.println(a[0] + \",\" + a[1] + \",\" + a[2]);",
    choices: ["3,1,2", "1,3,2", "1,2,3", "2,1,3"], answerIndex: 2,
    explain: "i=0: 3>1 swap -> 1,3,2. i=1: 3>2 swap -> 1,2,3. One pass sorted this small array.",
  },
  {
    id: "bubble-p2", concept: "bubble-sort", kind: "predict", difficulty: 1,
    prompt: "After one full pass of ascending bubble sort, what is guaranteed?",
    choices: [
      "The smallest value is at the start",
      "The largest value is at the end",
      "The array is fully sorted",
      "Nothing changes",
    ], answerIndex: 1,
    explain: "Each pass 'bubbles' the largest remaining value to the end — that's why later passes can stop earlier.",
  },

  // ---- selection-sort ----
  {
    id: "sel-p1", concept: "selection-sort", kind: "predict", difficulty: 2,
    prompt: "Descending selection sort, first slot. What does this print?",
    code: "int[] a = {9, 30, 14};\nint best = 0;\nfor (int i = 1; i < a.length; i++) { if (a[i] > a[best]) best = i; }\nint t = a[0]; a[0] = a[best]; a[best] = t;\nSystem.out.println(a[0]);",
    choices: ["9", "14", "30", "53"], answerIndex: 2,
    explain: "It scans for the biggest value (30) and swaps it into slot 0.",
  },
  {
    id: "sel-p2", concept: "selection-sort", kind: "predict", difficulty: 1,
    prompt: "Each pass of selection sort places which value into the next slot?",
    choices: [
      "The first element of the rest",
      "The best (min or max) of the remaining elements",
      "Two neighbours swapped",
      "A random element",
    ], answerIndex: 1,
    explain: "Selection sort scans the unsorted remainder, finds the best one, and puts it in place.",
  },
];

// ----------------------------------------------------------------- selectors
export function allDrillConcepts(): string[] {
  const seen = new Set<string>();
  const out: string[] = [];
  for (const d of DRILLS) {
    if (!seen.has(d.concept)) {
      seen.add(d.concept);
      out.push(d.concept);
    }
  }
  return out;
}

export function getDrillsForConcept(concept: string): Drill[] {
  return DRILLS.filter((d) => d.concept === concept);
}

export function hasDrills(concept: string): boolean {
  return DRILLS.some((d) => d.concept === concept);
}

/**
 * Adaptive pick: prefer drills the learner got wrong, then unseen, then the
 * least-recently practiced; avoid repeating `excludeId` back-to-back.
 */
export function pickDrill(concept: string, practice: PracticeState, excludeId?: string): Drill | null {
  const pool = getDrillsForConcept(concept).filter((d) => d.id !== excludeId);
  if (pool.length === 0) {
    // only one drill for the concept — allow the repeat
    return getDrillsForConcept(concept)[0] ?? null;
  }
  const cp = practice[concept] ?? emptyConceptPractice();
  const wrong = pool.filter((d) => cp.wrong.includes(d.id));
  if (wrong.length) return wrong[Math.floor(Math.random() * wrong.length)];
  const unseen = pool.filter((d) => !cp.seen.includes(d.id));
  if (unseen.length) return unseen[Math.floor(Math.random() * unseen.length)];
  return pool[Math.floor(Math.random() * pool.length)];
}

// ----------------------------------------------------------------- checking
export function checkPredict(drill: PredictDrill, choiceIndex: number): boolean {
  return choiceIndex === drill.answerIndex;
}

export interface WriteLineRun {
  compiled: boolean;
  runtimeError: string;
  stdout: string;
  patternOk: boolean;
}

/** Compile + run the learner's line via the existing /api/run (throwaway state). */
export async function runWriteLine(drill: WriteLineDrill, line: string): Promise<WriteLineRun> {
  const patternOk = safeMatch(drill.requirePattern, line);
  const body = drill.template.replace("__LINE__", line);
  const code = buildStrategySource(body);
  const response = await fetch("/api/run", {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ code, farmState: createDefaultFarmState() }),
  });
  const data = (await response.json()) as RunResponse;
  return {
    compiled: Boolean(data.compiled),
    runtimeError: (data.runtimeError ?? "").trim(),
    stdout: (data.stdout ?? "").trim(),
    patternOk,
  };
}

export interface DrillVerdict {
  pass: boolean;
  reason: string;
}

export function evaluateWriteLine(drill: WriteLineDrill, run: WriteLineRun): DrillVerdict {
  if (!run.patternOk) return { pass: false, reason: `Use ${drill.patternHint}.` };
  if (!run.compiled) return { pass: false, reason: "That doesn't compile yet — check the syntax (semicolons, brackets)." };
  if (run.runtimeError) return { pass: false, reason: `It compiled but errored when run: ${run.runtimeError}` };
  if (drill.expectStdout != null && run.stdout !== drill.expectStdout) {
    return { pass: false, reason: `Output didn't match.\nExpected:\n${drill.expectStdout}\nGot:\n${run.stdout || "(nothing)"}` };
  }
  return { pass: true, reason: "Correct!" };
}

function safeMatch(pattern: string, text: string): boolean {
  try {
    return new RegExp(pattern).test(text);
  } catch {
    return false;
  }
}

// ----------------------------------------------------------------- progress
/** Apply a drill result to the practice store, returning a new state. */
export function recordDrillResult(practice: PracticeState, drill: Drill, correct: boolean): PracticeState {
  const prev: ConceptPractice = practice[drill.concept] ?? emptyConceptPractice();
  const seen = prev.seen.includes(drill.id) ? prev.seen : [...prev.seen, drill.id];
  let wrong = prev.wrong;
  let next: ConceptPractice;
  if (correct) {
    wrong = prev.wrong.filter((id) => id !== drill.id);
    const gain = drill.kind === "write-line" ? XP_WRITE : XP_PREDICT;
    next = { ...prev, xp: prev.xp + gain, streak: prev.streak + 1, done: prev.done + 1, seen, wrong, lastTs: Date.now() };
  } else {
    wrong = prev.wrong.includes(drill.id) ? prev.wrong : [...prev.wrong, drill.id];
    next = { ...prev, streak: 0, seen, wrong, lastTs: Date.now() };
  }
  return { ...practice, [drill.concept]: next };
}

/** 0..1 practice mastery for a concept (correct answers toward MASTERY_TARGET). */
export function practiceMastery(practice: PracticeState, concept: string): number {
  const cp = practice[concept];
  if (!cp) return 0;
  return Math.max(0, Math.min(1, cp.done / MASTERY_TARGET));
}

export function totalXp(practice: PracticeState): number {
  return Object.values(practice).reduce((sum, cp) => sum + (cp?.xp ?? 0), 0);
}
