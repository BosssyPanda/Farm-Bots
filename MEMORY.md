# MEMORY.md — project history & roadmap for agents

**Purpose:** a running record of *what happened*, *why*, and *what to do next*, so any future
agent (or a later Claude session) can pick up cold. Append to the changelog at the bottom as
work lands. **Canonical spec is [AGENTS.md](AGENTS.md)** (the *what*); [CLAUDE.md](CLAUDE.md)
is the *how*; [OUTLINE.md](OUTLINE.md) is the overview. If those change, update this file too.

---

## TL;DR — current state (2026-06-16)

- We are building **Market Mayhem: Farm Bots** — a **browser** game that teaches **Java** by
  having the player **write real Java** to automate a farming **drone** (in the spirit of
  *The Farmer Was Replaced*).
- The repo now contains a working implementation scaffold: Next.js App Router cockpit,
  Monaco-based Strategy editor, procedural SVG farm playback, live inspector, lesson/hint
  panels, `/api/run`, `/api/objectives`, Java engine/objective registry, local runner,
  Sandbox runner seam, persistent state codec, reference solutions, and Vitest coverage.
- Current frontend behavior replays frames from the submitted pre-run farm state, ties checks
  to the objective that produced them, preserves progress on compiled runner failures with
  valid returned state, keeps the `Strategy` wrapper locked around an editable class body, and
  keeps the farm as the mobile-first visual anchor.
- Remaining work is full end-to-end Sandbox/deploy verification and any backend/API cleanup
  from parallel implementation work. See **Roadmap** below for the intended complete shape.

---

## What happened before (history)

This repo started as a **different** game and was deliberately pivoted.

1. **v0.1 → v0.2 (terminal quiz game).** The original `MarketMayhem.java` was a single-file
   **terminal** Java stock-trading game with a "Java Trading Academy." It taught Java through
   **quiz-style challenges** (predict output / trace / fix bug / fill blank) and **explicitly
   forbade the player from writing code** (no source editing, no `StudentWork.java`). It also
   had a Vercel **landing page** (`index.html`). These are all still in git at `HEAD`:

   ```bash
   git show HEAD:MarketMayhem.java        # ~1,928-line terminal quiz game (reference only)
   git show HEAD:MARKET_MAYHEM_SPEC.md    # its spec (six-concept progression)
   git show HEAD:CLAUDE.md                # its docs (beginner-Java + forbidden lists)
   git show HEAD:AGENTS.md                # its phase-based build instructions
   git show HEAD:index.html               # landing page (reusable as marketing)
   git show HEAD:REVIEW_REPORT.md         # teaching audit of the terminal game
   ```

   Relevant commits: `044eea7` initial v0.1 → `e7cd359` landing page → `33c9ecf` v0.2 learning
   edition → `ac80844`/`5d14a91`/`8a39775` teaching audit + review report.

2. **The pivot (2026-06-16).** The user deleted those files from the working tree (still in
   git) and restarted with a **fundamentally different, more authentic** mechanic: instead of
   answering quizzes, the player **writes real Java code to automate a drone**, like *The
   Farmer Was Replaced*. Decisions were locked via three rounds of clarifying questions:

   | Decision | Choice |
   |----------|--------|
   | Learning mechanic | **Write & run real Java** (player edits only `Strategy.java`) — not quizzes |
   | Theme | **Farming / drone** on a grid; light "market" framing keeps the repo identity |
   | Platform | **Web / browser** (Next.js on Vercel) |
   | Java execution | **Server-side via Vercel Sandbox** (real `javac`/`java`; returns a frame stream the browser animates) |
   | Execution model | **Continuous automation, tick-based** — program runs continuously; actions cost ticks; crops grow; resources accumulate |
   | World structure | **One persistent farm** — objectives unlock abilities/areas/crops; concepts introduced as the next objective needs them |
   | Progression | **Objectives unlock abilities** — **no currency/economy** |
   | Teaching | **Concept lessons + progressive hints**; **no** AI tutor |
   | Curriculum | **8 core concepts** (original 6 + **bubble sort** + **selection sort**); then an **open-ended phase focused on recursion & puzzles** that keeps growing |
   | Mastery | **Consistent correct use over time** (streak), with **adaptive recaps** if an already-taught core keeps failing; open-ended phase gated behind all 8 cores |
   | Debugging UX | **Live variable inspector** (drone state + player `watch(...)` vars + resources + tick) |
   | Aesthetic | **Clean, programmatically-rendered animated grid** (Canvas/SVG + CSS tweens; **no PNG sprite assets**) — agent's call, per user |
   | Narrative / audience | **No story** (cozy sandbox); **just the user** (single learner) |

3. **Docs written (2026-06-16).** `OUTLINE.md`, `AGENTS.md`, `CLAUDE.md`, and this file were
   created to fully specify the new direction. The beginner-Java constraints, the
   forbidden-features list, and the six-concept progression were **carried over** from the old
   terminal version (good reuse); everything else is new.

### What changed vs. the old version (so nobody rebuilds the wrong thing)

| Aspect | OLD (deleted, superseded) | NEW (build this) |
|--------|---------------------------|------------------|
| Platform | Terminal Java app | Browser (Next.js on Vercel) |
| How you learn | Answer **quizzes**; reading/menus | **Write real Java** that runs |
| Player edits code? | **No** (forbidden) | **Yes** — only `Strategy.java` |
| Theme | Stock trading | Farming / drone (+ light market framing) |
| Code execution | Local JVM in terminal | **Vercel Sandbox** microVM |
| Run model | One-shot per menu action | **Continuous, tick-based** program on a **persistent** farm |
| Progression | XP for correct answers | **Objectives unlock abilities** (no currency) |
| Concepts | 6, quiz-gated | **8 cores** (incl. bubble + selection sort) → open-ended **recursion & puzzles** |

---

## Roadmap — what to change & add next

Ordered so each step is testable before the next. Full detail in AGENTS.md §3–§8.

1. **Java engine (`engine/`), testable without the browser.** Implement `Direction`, `Crop`,
   `Tile`, `Farm` (2D grid + tick + data + unlocks), `Drone` (advances ticks, grows crops,
   emits a **frame** per action, supports `watch(...)`, enforces the tick + step caps),
   `Objective`, `Objectives` (ordered registry), `Progress` (streak/failCount/mastery/recap),
   and `Runner` (`main`: load state + current objective → run `Strategy` continuously for a
   tick-bounded session → evaluate checks + update progress → print one JSON line). Add
   `Strategy.template.java` + `engine/solutions/`.
   *Verify:* `javac *.java && java Runner` prints valid JSON; each solution reports
   `objective.passed: true`; a `while(true)` solution ends cleanly at the tick budget; no
   forbidden tokens in solutions.
2. **Objectives + curriculum.** Build the 8 core objectives (First Sprout → Pick the Best)
   per the table in AGENTS.md §3 — each tagged with its concept, with lesson + hint ladder +
   checks (designed so the concept is the realistic way to pass) + unlock. Wire mastery
   (consistent-use streak) + adaptive recap. Stub the open-ended recursion/puzzle phase
   behind the 8-core gate.
3. **Run API (`app/api/run/route.ts`).** Accept `{ code, farmState }`; run the static
   pre-check (reject forbidden tokens); open **Vercel Sandbox**, write engine + player's
   `Strategy.java` + serialized state, `javac` then `java Runner` under tick + wall + step
   limits, return the JSON contract (AGENTS.md §5). Enforce all safety rules in AGENTS.md §6.
4. **Frontend (`app/page.tsx` + `components/`).** Monaco editor (Java); **FarmView** — a
   clean **animated** grid (Canvas/SVG + CSS, no PNGs) driven by `frames[]`; **Inspector**
   (drone state + `watch` vars + resources + tick); **LessonPanel** + **Hint** button;
   **Console** (compile/runtime errors + `System.out`). Persist `farmState` in `localStorage`.
5. **Polish & deploy.** Beginner-readable error messages; reuse the old `index.html` as a
   landing page; deploy to the linked Vercel project `marketmayhem` (`vercel.ts` config).

---

## Open questions / caveats for implementers

- **JDK in Vercel Sandbox** — confirm how Java gets into the sandbox (install Temurin at init
  vs. a base image that includes a JDK). Flagged in AGENTS.md §7; resolve early in step 3.
- **Game name** — kept as "Market Mayhem" (framed as a farmers' market) to preserve the repo
  / Vercel project identity, but the theme is farming/drone. Final name can change at build
  time; if it does, update all docs.
- **Tick / time limits** — pick concrete numbers (e.g. ~5,000 ticks per run, ~10s wall
  clock, ~1M-statement hard step cap) and keep the engine tick budget and the function
  timeout consistent. A `while(true)` is expected, so the **tick budget**, not a loop ban, is
  the primary bound.
- **Detecting real concept use** — freeform code makes it hard to prove the player "used
  binary search." The approach: design each objective's checks so the concept is the
  realistic way to pass (comparison/swap budgets, sortedness checks, etc.). Decide concrete
  budgets per objective during step 2.
- **Live inspector scope** — server-side Java can't easily read arbitrary locals, so the
  inspector shows drone/farm state + resources (always emitted) + variables the player
  explicitly surfaces via `drone.watch(name, value)`. Confirm this is enough UX.

---

## Changelog (append newest at top)

- **2026-06-16 (full implementation in progress)** — Expanded the scaffold toward the full
  Farm Bots spec. Added the 8-objective Java registry plus Mastery Garden, richer persistent
  `farmState`/`stateCodec`, concept streak/fail/mastery data, reference solutions in
  `engine/solutions/`, `scripts/engine-verify.mjs`, static forbidden-code precheck, Vitest
  tests, local JVM runner, Vercel Sandbox runner seam, and `/api/objectives`. Verified
  `npm run engine:verify` and `npm run test`; remaining work at this checkpoint was frontend
  catalog/persistence/Monaco integration, final docs polish, and full `lint/typecheck/build`
  plus Browser verification. Browser inspection of the Steam page for *The Farmer Was
  Replaced* showed the relevant inspiration is a compact coding-game cockpit around a farm
  sim, not a marketing page. Google Drive course-material lookup was attempted but the
  connector timed out during MCP startup.
- **2026-06-16 (skeleton built)** — Scaffolded the app (structure-only, mock execution, local-
  Java seam). **Real & verified:** the Java engine compiles on JDK 8 and runs standalone
  (`npm run engine:build && npm run engine:run`) printing the frame-stream JSON for the one
  objective "First Sprout" (passed 3/3); the Next.js 15 app builds (`npm run build`) and runs
  (`npm run dev`) — the page renders all panels and animates the drone from the (mock) frame
  stream, with the live inspector updating (drone x/y, tick, `watch`, resources). **Stubbed:**
  `/api/run` returns canned frames via `MockRunner`; `LocalRunner`/`SandboxRunner` are TODO.
  **Deviations from the plan (deliberate, for a clean first scaffold):** editor is a styled
  `<textarea>` not Monaco yet (same `components/Editor.tsx` seam); `vercel.ts` skipped (Next is
  auto-detected); the starter template is `engine/Strategy.template.txt` (`.txt` so the
  `engine/*.java` glob doesn't try to compile a second `Strategy`). **Next:** wire `LocalRunner`
  to write the player's code as `Strategy.java`, run `javac`/`java`, and stream real frames.
- **2026-06-16 (later)** — Expanded the design via two more rounds of questions: **continuous
  tick-based** automation on **one persistent farm**; **objectives unlock abilities** (no
  currency); **lessons + hints** (no AI tutor); **8 core concepts** (added bubble + selection
  sort) gating an **open-ended recursion & puzzles** phase; **mastery = consistent correct
  use** with **adaptive recaps**; **live variable inspector**; **clean animated grid** (no
  PNGs); cozy/no-story; single-user. Rewrote `AGENTS.md` + `OUTLINE.md` and updated
  `CLAUDE.md` accordingly. The run/validate contract changed from a one-shot
  events+objectives shape to a **frame stream + persistent farmState + progress** shape.
- **2026-06-16** — Pivoted from the terminal quiz game to a browser "write real Java to
  automate a drone" game (Vercel Sandbox execution). Wrote `OUTLINE.md`, `AGENTS.md`,
  `CLAUDE.md`, and this `MEMORY.md`. No app code yet — engine is the next step.
- **(earlier)** — Built and audited the v0.1/v0.2 terminal quiz game (`MarketMayhem.java`) +
  Vercel landing page. Now superseded; recoverable from git `HEAD`.
