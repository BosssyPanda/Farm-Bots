# MEMORY.md — project history & roadmap for agents

**Purpose:** a running record of what happened, why, and what to do next, so any future
agent can pick up cold. Append to the changelog at the bottom as work lands.
**Canonical spec is [AGENTS.md](AGENTS.md)**; [CLAUDE.md](CLAUDE.md) is the working guide;
[OUTLINE.md](OUTLINE.md) is the skimmable overview.

---

## TL;DR — current state (2026-06-16)

- We are building **Farm Bots**: a browser game that teaches **Java** by having the player
  write real Java to automate a farming drone, inspired by the compact coding-game cockpit
  feel of *The Farmer Was Replaced* without copying its assets or branding.
- The repo contains a working Next.js App Router cockpit, Monaco-based Strategy editor,
  procedural farm playback, live inspector, lesson/hint panels, `/api/run`,
  `/api/objectives`, Java engine/objective registry, local runner, Vercel Sandbox runner
  seam, persistent state codec, reference solutions, and Vitest coverage.
- Frontend behavior replays frames from the submitted pre-run farm state, ties checks to
  the objective that produced them, preserves progress on compiled runner failures with a
  valid returned state, keeps the `Strategy` wrapper locked around an editable class body,
  and keeps the farm as the mobile visual anchor.
- Current validation has passed `npm run engine:verify`, `npm run test`, `npm run lint`,
  `npm run typecheck`, `npm run build`, and local browser smoke checks.

---

## Product Direction

Farm Bots is a single-user browser game for learning beginner Java through real code:

- The player writes only the body of `Strategy`.
- The server compiles and runs that Java in a bounded runner.
- The farm is persistent, tick-based, and animated from returned frame data.
- Objectives teach and verify 8 core concepts: methods, `for` loops, arrays, `while` loops,
  sequential search, binary search, bubble sort, and selection sort.
- Mastery requires consistent correct reuse over time; repeated failures trigger recaps.
- The open-ended recursion and logic-puzzle phase unlocks only after all 8 cores are
  mastered.

See [AGENTS.md](AGENTS.md) for the canonical product and security spec.

---

## Changelog

- **2026-06-16 (rename cleanup)** — Removed old product naming from docs, package metadata,
  and the browser title. The project identity is now **Farm Bots**.
- **2026-06-16 (full implementation)** — Expanded the scaffold toward the full Farm Bots
  spec. Added the 8-objective Java registry plus Mastery Garden, richer persistent
  `farmState`/`stateCodec`, concept streak/fail/mastery data, reference solutions in
  `engine/solutions/`, `scripts/engine-verify.mjs`, static forbidden-code precheck,
  Vitest tests, local JVM runner, Vercel Sandbox runner seam, `/api/objectives`, Monaco
  editor integration, procedural farm rendering, and browser-verified run flow.
- **2026-06-16 (skeleton built)** — Scaffolded the app shell, Java engine seam, API route,
  and frame-driven UI. The initial focus was getting a real compile/run loop and cockpit
  layout in place before deeper curriculum and safety work.
- **2026-06-16 (direction locked)** — Finalized the browser game direction: continuous
  tick-based automation on one persistent farm; objectives unlock abilities; lessons and
  hints; 8 core Java concepts; mastery and adaptive recaps; live variable inspector; clean
  procedural animation; no story; single learner.
