# AGENTS.md — Farm Bots

**This file is the canonical spec — the exact thing we are building.** Any coding agent
(Claude, Codex, etc.) should be able to implement the project from this document alone. If
something here conflicts with another doc, this file wins. Operational/working notes live in
[CLAUDE.md](CLAUDE.md); a skimmable overview lives in [OUTLINE.md](OUTLINE.md); project
history + roadmap live in [MEMORY.md](MEMORY.md).

---

## 1. What we are building

A **browser** game in the spirit of *The Farmer Was Replaced*, but for learning **Java**.
The player **writes real Java code** that automates a farming **drone** on **one persistent,
continuously-running farm**. The player's code is compiled and executed **server-side inside
Vercel Sandbox**, which returns a **frame stream** (the drone's tick-by-tick actions and
state); the browser **animates** the farm and drives a **live variable inspector** from it.

Locked design (decided with the user, 2026-06-16):

| Dimension | Decision |
|-----------|----------|
| Learning mechanic | **Write & run real Java** — the player edits only `Strategy.java` |
| Execution model | **Continuous automation**, **tick-based** — the program runs continuously; actions cost ticks; crops grow over ticks; resources accumulate |
| World structure | **One persistent farm** — a single ever-running farm; objectives unlock abilities/areas/crops; concepts are introduced as the next objective requires them |
| Progression | **Objectives unlock abilities** — no currency/economy; beating an objective permanently unlocks a new ability, area, crop, or speed |
| Teaching support | **Concept lessons** (short lesson + worked example before a concept's first use) **+ progressive hints** when stuck. **No** AI tutor |
| Curriculum | **8 core concepts** (below) must be mastered; then an **open-ended phase** focused on **recursion & puzzles** that keeps growing |
| Mastery | **Consistent correct use over time** (a streak), with **adaptive recaps** if an already-taught core keeps failing |
| Debugging UX | **Live variable inspector** (drone state + player-watched variables + resources, updating as the program runs) |
| Aesthetic | **Clean, programmatically-rendered animated grid** (Canvas/SVG + CSS tweens; no PNG sprite assets) |
| Narrative | **None** — cozy automation sandbox |
| Audience | **Just the user** — single learner; no multiplayer / stranger-onboarding |

**Hard rule:** the player **only ever writes the body of `Strategy`** (one `run` method,
plus their own helper methods). The player never edits the engine, the world, the objectives,
or any infrastructure.

### The 8 core concepts (must each be mastered)

1. `for` loops 2. `while` loops 3. arrays 4. methods 5. **sequential search**
6. **binary search** 7. **bubble sort** (lighter core) 8. **selection sort** (lighter core)

Woven in throughout: `if`/`else`, nested loops, 2D arrays, `String` methods, `%` and `/`.
**After all 8 cores are mastered**, the **open-ended phase** unlocks, focused on **recursion
& logic puzzles**, and may keep expanding over time.

---

## 2. The player-facing Java API

The player writes:

```java
public class Strategy {
    public void run(Drone drone, Farm farm) {
        // Player code. Typically a continuous loop, e.g.:
        // while (true) { ... drive, plant, harvest ... }
        // Each drone action below advances the simulation by its tick cost.
        // The session ends when this method returns OR the tick budget runs out.
    }
}
```

Only the contents of `Strategy.java` come from the player. Everything below is provided by
the engine and is read-only to the player.

### Tick model (important)

The world advances in discrete **ticks**. Drone **actions are blocking and cost ticks** —
calling them advances simulation time, ages/grows crops, and records a frame. Sensors are
free (0 ticks). Example costs (tunable; keep consistent across engine + UI):

| Action | Tick cost |
|--------|-----------|
| `move(...)` | 1 |
| `plant(...)` / `harvest()` | 2 |
| `scan()`, `x()`, `y()`, all `farm.*` queries, `watch(...)` | 0 (free sensors) |

Crops planted now become ripe after a crop-specific number of ticks. The drone may loop
forever (`while (true)`) — the **per-run tick budget** (§6) bounds each session, and farm
state persists across runs so the farm genuinely progresses "over time."

### `Drone` — the thing the player controls (records every action into the frame stream)

| Member | Returns | Effect |
|--------|---------|--------|
| `move(Direction d)` | `void` | Move one tile (costs ticks). Moving off the field is an illegal action → clear runtime error. |
| `moveNorth()/moveSouth()/moveEast()/moveWest()` | `void` | Convenience wrappers over `move`. |
| `x()` / `y()` | `int` | Current column / row (0-based; free sensor). |
| `plant(Crop c)` | `void` | Plant crop `c` on the current tile (must be empty soil). |
| `harvest()` | `Crop` | Harvest the current tile if ripe; returns the crop or `Crop.NONE`. |
| `scan()` | `Tile` | Read-only info about the current tile (free sensor). |
| `watch(String name, int value)` | `void` | Surface a variable into the **live inspector** (free; e.g. `drone.watch("i", i)`). Overloads for `double`/`boolean`/`String`. |

### `Farm` — read-only world + per-objective data

| Member | Returns | Meaning |
|--------|---------|---------|
| `width()` / `height()` | `int` | Field dimensions (can grow as abilities unlock). |
| `tileAt(int x, int y)` | `Tile` | Read-only tile info at a coordinate. |
| `tick()` | `int` | Current world tick. |
| `crops()` | `String[]` | Crop names along a row/region (parallel with `prices()`). *Populated when relevant.* |
| `prices()` | `int[]` | Market prices; **sorted ascending** on binary-search objectives. *Populated when relevant.* |
| `moisture()` | `int[]` | Per-column moisture readings. *Populated when relevant.* |

> Data accessors return an empty array when the current objective doesn't use them; each
> objective's briefing states which data is available. Abilities are queried via
> `farm` too (e.g. unlocked sensors); locked features simply aren't available yet.

### Enums and `Tile`

- `enum Direction { NORTH, SOUTH, EAST, WEST }`
- `enum Crop { NONE, WHEAT, CORN, PUMPKIN, CARROT }` (more unlock over time)
- `Tile` (read-only): `Crop crop()`, `boolean ripe()`, `int moisture()`.

### Allowed standard Java (player)

`int double boolean char String`, 1D/2D arrays, `for`, `while`, `if`/`else`, helper methods
inside `Strategy`, `Math.*`, `String` methods, `System.out.println` (shown in the console),
recursion (open-ended phase), and the API above.

### Forbidden in player code (carried over from the prior version; enforced — see §6)

`ArrayList`, `HashMap`/`Map`/`Set`/any `java.util.Collection`, streams, lambdas, method
references, `Arrays.sort`, `Arrays.binarySearch`, `Collections.*`, `java.util.Random`, file
I/O, networking, reflection, `Thread`/concurrency, `System.exit`, and any `import` beyond
what the engine pre-supplies. **Searching and sorting must be written by hand** (that's the
whole point of the search/sort cores).

---

## 3. Curriculum: objectives, abilities, mastery & recap

There is **one persistent farm**. Progress is a sequence of **objectives** woven into that
farm. Each objective is tagged with a **primary concept**, teaches it via a lesson, and on
completion **unlocks an ability**. Objectives are **designed so the intended concept is the
realistic way to pass** (e.g. a binary-search objective caps comparisons below what a linear
scan needs; sort objectives check correct ordering within a budget) — so completing them is
evidence of real understanding.

### Concept → objective → unlock (suggested order; tunable)

| # | Concept (core) | Objective (in the persistent farm) | Unlocks |
|---|----------------|------------------------------------|---------|
| 1 | methods + sequential statements | **First Sprout** — drive a short path and plant your first tiles | Basic planting/harvesting + the starter field |
| 2 | `for` loops (+ nested, 2D grid) | **The Long Rows** — plant/harvest a whole row, then sweep the field | A **bigger field** |
| 3 | arrays + parallel arrays | **Stock the Stall** — read `crops()`/`prices()`; sum, average, max/min, count | The **market stall** + price/inventory sensors |
| 4 | `while` loops (+ `%` `/`) | **Harvest 'til Done** — harvest until empty; decode an irrigation code digit-by-digit | **Irrigation** (faster growth) + a new crop |
| 5 | sequential search | **Find the Crop** — scan `crops()` for a target; return index or `-1`; count comparisons | A **crop locator** |
| 6 | binary search | **Fast Market** — search sorted `prices()` (low/high/mid); comparison-budgeted | **Fast lookup** (speed boost) |
| 7 | **bubble sort** (light) | **Tidy the Stalls** — bubble-sort stalls/prices into order | **Sorted market view** |
| 8 | **selection sort** (light) | **Pick the Best** — selection-sort to rank crops by value | **Auto-prioritize** highest-value crop |
| — | **open-ended: recursion & puzzles** | unlocks only after all 8 cores mastered | recursive/puzzle "special fields"; keeps growing |

> Adding an objective = one entry in `Objectives.java` (world setup + concept tag + lesson +
> hints + objective checks + starter snippet + unlock) — nothing else changes.

### Mastery (consistent correct use over time)

Per concept, the engine tracks a `correctStreak` and a `failCount`:

- Passing an objective tagged with the concept (and, where relevant, later re-using it
  correctly) increments `correctStreak`. A concept is **mastered** when it has been used
  **correctly and consistently** — `correctStreak ≥ 3` with no recent attributable failure
  (not a single one-off pass).
- The **open-ended phase (recursion & puzzles) unlocks only when all 8 cores are mastered.**

### Adaptive recap (the "if they keep getting it wrong, recap" rule)

- A failure attributable to a concept increments that concept's `failCount` and resets its
  streak.
- If an **already-introduced core concept** accumulates repeated failures (e.g. `failCount ≥ 3`),
  the game **interrupts to recap it** — re-shows that concept's lesson and a short, focused
  mini-challenge — **even if play has moved on to later concepts.** Passing the recap clears
  the failure count and resumes normal progression.

---

## 4. Architecture

### 4.1 Components

- **Frontend** — Next.js (App Router) + TypeScript on Vercel.
  - **Monaco** editor configured for Java (the `Strategy` body).
  - **FarmView** — a clean, **programmatically rendered animated grid** (Canvas or SVG + CSS
    tweens; **no PNG sprite assets**): the drone glides between tiles, crops sprout/grow/sway,
    harvests pop. Renders purely from the frame stream.
  - **Inspector** — live variable inspector: drone position/state, player `watch(...)`
    values, resources, and current tick, updating as frames replay.
  - **LessonPanel / Hints** — the current objective's lesson + worked example, and a
    progressive **Hint** button.
  - **Console** — compiler errors, runtime errors, and `System.out` output (beginner-readable).
- **Run API** — Vercel Function at `app/api/run/route.ts` (Node.js runtime, Fluid Compute):
  takes the player's code + persistent farm state, runs a bounded **session** in Vercel
  Sandbox, returns the frame stream + updated state + progress (§5).
- **Vercel Sandbox** — secure Firecracker microVM that compiles & runs the untrusted player
  code (real `javac`/`java`).
- **Persistent farm state** — the farm carries across runs so the farm progresses "over
  time." Persist client-side (e.g. `localStorage`) and/or send round-trip in the request;
  this is a single-user game, so simple local persistence is fine.
- **Java engine** — server-side Java in `engine/`, **never edited by the player**.

### 4.2 The Java engine (`engine/`)

| File | Responsibility |
|------|----------------|
| `Direction.java`, `Crop.java` | Enums. |
| `Tile.java` | Read-only tile view. |
| `Farm.java` | The world: 2D grid, dimensions, tick, per-objective data, unlocked abilities; read-only to the player. |
| `Drone.java` | Player-controlled actor: position; `move/plant/harvest/scan/watch`; **advances ticks**, **ages/grows crops**, and **emits a frame** per action; enforces the per-run **tick budget** + hard step cap; throws clear errors on illegal moves. |
| `Objective.java` | One objective: id, title, primary concept, world setup, lesson text, hint ladder, objective checks, starter snippet, unlock. |
| `Objectives.java` | Ordered registry of all objectives. |
| `Progress.java` | Per-concept `correctStreak`/`failCount`, mastery, recap triggers, unlocked abilities. |
| `Runner.java` | `main`: load persistent state + current objective → build `Farm`/`Drone` → instantiate `Strategy` → call `run` (guarded by try/catch + tick budget) → evaluate objective checks + update progress → print **one line of JSON** (§5). |
| `Strategy.java` | The player's code (the only file swapped per run); `Strategy.template.java` provides per-objective starters. |
| `solutions/` | Reference solutions per objective — used by tests to confirm checks pass and that no forbidden features are needed. |

The engine writes JSON by hand (small helper) to stay dependency-free in the Sandbox. The
forbidden list in §2 applies to **player** code, not engine code.

### 4.3 Run flow (request → animation)

1. Browser `POST /api/run` with `{ code, farmState }`.
2. Function runs a **static pre-check** on `code` (reject forbidden tokens — §6). On failure,
   return immediately with `compiled: false`; never reach the JVM.
3. Function opens a Vercel Sandbox; writes `engine/*.java` + the player's `code` as
   `Strategy.java` + the serialized `farmState`.
4. Sandbox: `javac *.java` → on success `java Runner` (under wall-clock + tick + step caps).
5. Capture `javac` stderr, program stdout (Runner's JSON + player prints), and stderr.
6. Function returns the response (§5); browser animates `frames[]`, updates the inspector,
   shows objective/mastery progress and any unlocks, and persists the new `farmState`.

---

## 5. Run / validate contract

**Request** — `POST /api/run`

```json
{ "code": "public class Strategy { ... }", "farmState": { /* persisted farm, omit on first run */ } }
```

**Response**

```json
{
  "ok": true,
  "compiled": true,
  "compileErrors": "",
  "runtimeError": "",
  "stdout": "",
  "ticks": 240,
  "tickLimit": 5000,
  "frames": [
    {
      "tick": 1,
      "action": { "type": "move", "dir": "EAST", "to": [1, 0] },
      "drone": { "x": 1, "y": 0, "carrying": "NONE" },
      "watch": { "i": 0, "count": 3 },
      "resources": { "WHEAT": 12 }
    },
    {
      "tick": 3,
      "action": { "type": "plant", "at": [1, 0], "crop": "WHEAT" },
      "drone": { "x": 1, "y": 0, "carrying": "NONE" },
      "watch": { "i": 1, "count": 3 }
    }
  ],
  "farmState": { "...": "updated persistent farm to carry into the next run" },
  "objective": {
    "id": "the-long-rows",
    "concept": "for-loops",
    "checks": [ { "id": "row-planted", "label": "Plant every tile in row 0", "passed": true } ],
    "passed": true
  },
  "unlocked": ["bigger-field"],
  "concepts": {
    "for-loops": { "correctStreak": 3, "mastered": true, "failCount": 0, "recapDue": false }
  }
}
```

- `frames[]` is the **single source of truth** for both the animation and the live inspector:
  each frame carries the `action`, the `drone` state, the player's `watch` values, and
  (optionally) `resources`, all at a given `tick`.
- `runtimeError` is non-empty if the code threw, made an illegal move, or hit the tick/step
  cap — described in beginner-friendly terms.
- `objective.passed` + `concepts` drive unlocks, mastery, and recap decisions.

The **Runner** (Java) prints exactly one JSON line carrying `frames`, `objective`,
`concepts`, `unlocked`, `farmState`, `ticks`. The function wraps it with
`compiled`/`compileErrors`/`runtimeError`/`ok`.

---

## 6. Security & safety (running untrusted code)

The player's Java is untrusted, and `while (true)` is **expected** (continuous automation).
Defense in depth:

1. **Static pre-check (function host):** reject the forbidden tokens from §2 before compiling.
2. **Isolation:** player code compiles and runs **only inside Vercel Sandbox**, never on the
   function host.
3. **No network:** disable Sandbox egress.
4. **Bounded session, not "no infinite loops":** each run simulates up to a **tick budget**
   (e.g. ~5,000 ticks) and a wall-clock timeout (e.g. ~10s); a **hard statement/step cap**
   (e.g. ~1,000,000) is a final safety net against tight non-action loops. Hitting a cap ends
   the session cleanly with a clear message — it is not a crash.
5. **Output caps:** truncate `stdout`/stderr and cap `frames[]` length (downsample if huge).
6. **Fresh isolation per run:** never reuse a Sandbox's mutable runtime across requests;
   only the explicit serialized `farmState` carries forward.
7. **Restricted classpath:** compile player code against only the engine classes + a minimal
   JDK surface, so forbidden APIs fail to compile where feasible.

(All numbers above are tunable; keep the engine tick budget and the function timeout
consistent.)

---

## 7. Tech stack & conventions

- **Next.js** App Router + **TypeScript**; **Node.js runtime** for the API route (Fluid
  Compute). Configure with **`vercel.ts`** (preferred over `vercel.json`).
- **Vercel Sandbox** (`@vercel/sandbox`) for code execution. Provision a **JDK** (e.g.
  Temurin) in the sandbox — **confirm the JDK install/runtime image during implementation**.
- **Monaco** editor for Java; **Canvas or SVG + CSS** for the animated grid — **no static
  PNG sprite assets** (procedural shapes + tweened transitions so the farm feels alive and
  is cheap to build).
- **Single-user persistence**: `localStorage` (and/or request round-trip) for `farmState` and
  progress — no accounts, no database needed.
- Keep beginner Java front-and-center: starter code, lessons, hints, and error messages use
  only concepts unlocked so far. No story/narrative chrome.
- The prior terminal version + its docs are recoverable from git (`git show HEAD:<file>`);
  the beginner-Java constraints, the forbidden list, and the original six-concept progression
  came from there.

---

## 8. Definition of done (the game)

- A single persistent farm where the player writes Java, hits **Run**, and watches the drone
  **continuously** automate it tick-by-tick, with the **live inspector** updating live.
- All 8 core concepts have objectives that genuinely **require** the concept (hand-written
  sequential search, real low/high/mid binary search, real bubble & selection sort within
  budgets), each unlocking a concrete ability.
- **Mastery** tracks consistent correct use; **recap** resurfaces a core concept after
  repeated failures; the **recursion & puzzles** open-ended phase unlocks only after all 8
  cores are mastered.
- **Lessons + progressive hints** are present for each concept; the **live variable
  inspector** works; compiler/runtime errors are beginner-readable.
- Untrusted code cannot escape the Sandbox, reach the network, or hang the function; a
  `while (true)` program ends cleanly at the tick budget.
- Reference solutions in `engine/solutions/` pass every objective with **no** forbidden
  features.

> Build steps (scaffold Next.js, write the engine, wire the Sandbox function, deploy) are the
> implementation work that follows these docs — see [CLAUDE.md](CLAUDE.md) and the roadmap in
> [MEMORY.md](MEMORY.md).
