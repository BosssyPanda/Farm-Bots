# CLAUDE.md — working in this repo

Operational guide for Claude and other agents. **The canonical spec is
[AGENTS.md](AGENTS.md)** — read it first for *what* we're building. This file covers *how* to
work here: layout, commands, conventions, and what not to touch. [OUTLINE.md](OUTLINE.md) is
the quick overview.

## What this is

**Farm Bots** — a browser game (Next.js on Vercel) that teaches **Java** the
way *The Farmer Was Replaced* teaches Python: the player **writes real Java** to automate a
farming **drone** on **one persistent, continuously-running, tick-based farm**. Player code is
compiled and run **server-side in Vercel Sandbox**; the browser animates the farm from the
returned **frame stream** and drives a **live variable inspector**. Progress = completing
objectives that unlock abilities (no currency). See AGENTS.md §1 for the full locked-design
table.

## Current state

The repo now contains the browser-game scaffold plus a real Java engine and runner seam.
Implemented so far:

- Next.js App Router shell with editor, farm view, lesson panel, inspector, controls, console,
  `/api/run`, and `/api/objectives`.
- Java engine in `engine/` with the 8 core objectives, reference solutions, persistent
  state codec, concept mastery, frame emission, and objective catalog output.
- Runner seam in `lib/runner/`: local JVM execution for development, Vercel Sandbox execution
  for production, mock fallback, static forbidden-code precheck, and TypeScript tests.

Active remaining work is frontend integration/polish and deployed Sandbox verification.

## Golden rules

- **The player only writes `Strategy.java`** (the body of one `run` method + helpers). Never
  add a workflow where the player edits the engine, objectives, or infra. (No `StudentWork.java`.)
- **AGENTS.md is the source of truth.** Keep `OUTLINE.md`, `MEMORY.md`, and this file
  consistent with it — if the curriculum/objectives, the drone/farm API + tick model, or the
  JSON contract change, update all of them.
- **Continuous, tick-based, persistent.** The program runs continuously (a `while (true)` is
  expected); actions cost ticks; the farm state persists across runs. Bound each run by a
  **tick budget** (not by forbidding loops). See AGENTS.md §6.
- **Untrusted code runs only in Vercel Sandbox** — never execute player code on the function
  host. Honor every safety rule in AGENTS.md §6 (static pre-check, no network, tick + wall +
  step caps, fresh isolation, output caps).
- **Beginner Java only** for player-facing code, starters, lessons, and examples. Respect the
  allow/forbid lists in AGENTS.md §2; searching and sorting are written **by hand** (they're
  core concepts). Recursion is for the open-ended phase only.
- **8 cores gate the rest.** The recursion & puzzles phase unlocks only after all 8 core
  concepts are mastered; mastery = consistent correct use, with adaptive recaps (AGENTS.md §3).

## Intended repo layout (once implemented)

```
/
├─ AGENTS.md  CLAUDE.md  OUTLINE.md
├─ app/
│  ├─ page.tsx              # game UI: objective+lesson, editor, animated farm, inspector, console
│  └─ api/run/route.ts      # POST /api/run → Vercel Sandbox → JSON (AGENTS.md §5)
├─ components/              # Editor, FarmView (animated grid), Inspector, LessonPanel, Console
├─ lib/                     # client types + frame-stream → animation helpers + farmState persistence
├─ engine/                  # Java engine (server-side; NEVER player-edited)
│  ├─ Direction.java  Crop.java  Tile.java
│  ├─ Farm.java  Drone.java
│  ├─ Objective.java  Objectives.java  Progress.java  Runner.java
│  ├─ Strategy.template.java   # per-objective starter
│  └─ solutions/            # reference solutions used by tests
├─ scripts/                 # engine verification helpers
├─ tests/                   # Vitest coverage for runner/UI helpers
├─ public/                  # optional landing/static assets
├─ vercel.ts                # optional Vercel config if runtime tuning is needed
└─ package.json  next.config.*  tsconfig.json
```

## Commands

```bash
# Web app
npm install
npm run dev            # Next.js dev server
npm run lint
npm run typecheck
npm run test
npm run build

# Java engine — develop/test it without the browser
npm run engine:build
npm run engine:run
npm run engine:catalog
npm run engine:verify

# Confirm no forbidden features in any sample/solution Strategy (expect no matches)
grep -nE "ArrayList|HashMap|stream\(|->|::|Collections\.|Arrays\.sort|Arrays\.binarySearch|java\.util\.Random" engine/solutions/*.java
```

Development uses `FARM_BOTS_RUNNER=local` by default so the API can compile/run Java with the
local JDK. Production should use `FARM_BOTS_RUNNER=sandbox` or the default production selector
so untrusted player code runs in Vercel Sandbox.

## Deploy

Linked Vercel project is configured in `.vercel/project.json` (gitignored). Deploy via
the `vercel:deploy` skill or `vercel` CLI. Use Node.js runtime + Fluid Compute for the
function. Production Sandbox verification may require Vercel credentials and quota.

## Definition of done

See AGENTS.md §8. In short: one persistent farm where the player writes Java and watches the
drone continuously automate it tick-by-tick with a live inspector; all 8 core concepts have
objectives that genuinely require them (hand-written search + bubble/selection sort within
budgets); mastery + adaptive recap work; the recursion & puzzles phase unlocks only after the
8 cores; lessons + hints present; clear compiler/runtime errors; sandbox-isolated execution
(a `while(true)` ends cleanly at the tick budget); reference solutions pass with no forbidden
features.

## Conventions

- TypeScript for all web code; keep components small and the animation pure (render the farm
  and inspector purely from the `frames[]` stream).
- Add an objective by adding one entry to `Objectives.java` (world setup + concept tag +
  lesson + hint ladder + objective checks + starter + unlock) and its UI metadata — nothing
  else (AGENTS.md §3). Design each objective so the intended concept is the realistic way to
  pass it.
- Beginner-readable error messages: translate `javac`/runtime errors and illegal-move /
  tick-budget endings into plain language in the console.

## Motion and Animation Rules

This project is motion-heavy. Animation is a core design system.

Use the motion-design skill before implementing any major animated feature.

Tool routing:
- Use Motion for React UI animation: hover, tap, layout, cards, panels, menus, progress bars, screen transitions.
- Use GSAP for choreographed timelines, cinematic sequences, intro/victory/level-complete moments.
- Use Rive for interactive mascot, character, and state-machine animation.
- Use LottieFiles MCP for reusable animation assets like XP bursts, badge unlocks, loading animations, icons, and reward effects.
- Use dotLottieReact to render exported .lottie assets in React.

Rules:
- Do not animate the same element/property with both Motion and GSAP.
- Put reusable timing, easing, and spring values in /src/motion.
- Every major animation must have reduced-motion behavior.
- Motion should guide attention, reward progress, and clarify game state.
- Avoid random bouncing, spinning, or excessive motion.

## Plugin Status

### 🚫 DO NOT USE — Higgsfield Skills (pending security review)

The four Higgsfield skills (`higgsfield-generate`, `higgsfield-marketplace-cards`,
`higgsfield-product-photoshoot`, `higgsfield-soul-id`) were flagged **Critical Risk by Snyk**
and **High Risk by Gen scanner** during installation. They have been **quarantined** and must
not be invoked by any agent or workflow until the owner reviews and clears them.

- Symlinks removed from `.claude/skills/`
- Source preserved (not deleted) in `.agents/skills-quarantined/`
- Reinstate only after explicit owner sign-off

### ⚠️ Pending Auth — Tools installed but not yet activated

These tools are installed but cannot be used until credentials are provided:

| Tool | What's needed |
|------|--------------|
| Firecrawl | `FIRECRAWL_API_KEY` env var, then `/firecrawl:setup` |
| Morph MCP | `MORPH_API_KEY` from morph.so, then `claude mcp add filesystem-with-morph` |
| BuildPartner.ai | Account/token at buildpartner.ai, then re-run install |

# CLAUDE.md

## Project Operating Rules

You are Claude Code working inside this project. Your job is to build, improve, review, and verify the project like a serious product engineer and frontend game designer.

This project is a motion-heavy frontend educational game. The final result should feel like a polished game, not a static React demo, generic SaaS dashboard, or school project.

Always prioritize:

1. working code
2. strong frontend design
3. meaningful animation and interaction
4. clear educational value
5. reliable verification
6. clean project structure
7. security-aware implementation

Do not guess. Inspect the codebase first, use available tools intentionally, and verify before calling work complete.

---

## Active Claude Code Tool Stack

The following tools are currently verified and available:

| Tool                 | Status                                                       |
| -------------------- | ------------------------------------------------------------ |
| Caveman              | enabled; `/caveman` available; hooks wired                   |
| Exa MCP              | connected; web search available                              |
| Compound Engineering | enabled                                                      |
| Skill Creator        | enabled                                                      |
| Frontend Design      | enabled                                                      |
| Security Guidance    | enabled                                                      |
| Legal                | enabled for drafting/workflow support only, not legal advice |
| Morph Compact        | enabled                                                      |
| CodeBurn             | enabled; reads local sessions only                           |
| motion-design skill  | active in `.claude/skills/motion-design`                     |

Use only these verified tools by default.

Do not assume Firecrawl, Codex, BuildPartner, Higgsfield, Morph MCP, or any other unverified plugin is available unless the user confirms a later verified install.

---

## Global Behavior Rules

* Do not reinstall tools unless explicitly asked.
* Do not invent API keys, credentials, plugin IDs, commands, package names, or setup status.
* Do not expose secrets in logs, commits, screenshots, summaries, or generated files.
* Do not claim a task is complete unless the code has been built, tested, or otherwise verified.
* Do not make frontend changes without reviewing the actual UI state when possible.
* Do not use random one-off animation values when shared motion tokens should exist.
* Do not use legal plugin output as legal advice.
* Do not use unverified plugins just because they appeared in an earlier plan.

If a tool requires login, API key, account creation, credits, or external permissions, stop and ask before continuing.

---

## Default Workflow for Every Task

Before coding:

1. Read the user request carefully.
2. Inspect the relevant files.
3. Check `package.json`, existing scripts, project structure, and current conventions.
4. Decide which verified tools are relevant.
5. Briefly state the tool routing if the task is complex.

During implementation:

1. Make the smallest complete set of changes.
2. Keep code readable and maintainable.
3. Preserve existing architecture unless there is a clear reason to change it.
4. Use reusable components, tokens, utilities, and hooks instead of scattered one-off code.
5. Update tests or add tests when behavior changes.
6. Keep accessibility and reduced-motion behavior in mind.

Before finishing:

1. Run available verification commands.
2. Fix errors instead of hiding them.
3. Summarize what changed.
4. Summarize how it was verified.
5. List any remaining blockers honestly.

---

## Tool Routing

### Caveman

Use Caveman when:

* the session is becoming too verbose
* context needs compression
* a short implementation loop is better than a long discussion
* the task needs token discipline

Do not use Caveman to skip verification or hide uncertainty.

---

### Exa MCP

Use Exa MCP when:

* library or framework behavior may have changed
* current documentation is needed
* working with unfamiliar APIs, SDKs, packages, or CLI tools
* debugging an issue that may involve current dependency behavior
* checking best practices for frontend, animation, testing, or deployment

Rules:

* Prefer official documentation.
* Do not rely on stale memory for package APIs.
* Record important source URLs in the final summary when research affected implementation.
* Use Exa before adding unfamiliar packages.

---

### Compound Engineering

Use Compound Engineering for significant engineering tasks, including:

* multi-file features
* architecture changes
* refactors
* bug hunts
* repeated failures
* test/review/autofix loops
* production-readiness work

Default Compound Engineering loop:

1. understand objective
2. inspect current implementation
3. identify constraints
4. plan the work
5. implement in small steps
6. verify with available commands
7. review the result
8. capture durable lessons if needed

---

### Skill Creator

Use Skill Creator when a repeated workflow should become reusable.

Create or update skills for:

* frontend game screen design
* motion review
* Java-learning pedagogy
* UI polish audits
* review/autofix loops
* accessibility checks
* animation-system checks

Do not create a skill for one-off tasks.

---

### Frontend Design

Use Frontend Design before major UI work.

Use it for:

* visual direction
* layout hierarchy
* typography
* spacing
* color systems
* component hierarchy
* screen composition
* game UI polish
* interaction states
* responsive behavior

Frontend design standard:

* The UI must feel like a polished educational game.
* Avoid generic cards, default dashboards, basic forms, and template-looking layouts.
* Every screen needs a clear visual hierarchy.
* Important actions should feel interactive and rewarding.
* Game state should be visually obvious.
* Success, failure, progress, unlocks, and rewards should have distinct visual treatment.
* Use design tokens rather than scattered styling.

Core screens to treat as game screens:

1. title screen
2. main menu
3. mission or level select
4. gameplay HUD
5. lesson panel
6. quiz or challenge panel
7. reward screen
8. progress screen
9. settings screen
10. victory state
11. failure state
12. loading or transition state

---

### motion-design Skill

Use the motion-design skill before implementing any meaningful animation.

Animation is a core system in this project, not decoration.

Use animation to:

* guide attention
* explain state changes
* reward progress
* make interactions feel responsive
* clarify success and failure
* make learning feel game-like
* reduce confusion during transitions

Do not add random bouncing, spinning, shaking, or excessive motion.

---

## Motion and Animation System

For animation-heavy work, create or maintain a shared motion system.

Expected structure when appropriate:

```text
src/motion/
  animationTokens.ts
  durations.ts
  easings.ts
  springs.ts
  variants.ts
  transitions.ts
  reducedMotion.ts
  MotionProvider.tsx
```

Use shared tokens for:

* duration
* easing
* spring settings
* stagger timing
* hover behavior
* tap behavior
* entrance transitions
* exit transitions
* screen transitions
* reward animations
* reduced-motion fallbacks

Animation rules:

* Every major animation must have reduced-motion behavior.
* Do not animate the same element/property with both Motion and GSAP.
* Use Motion for normal React UI animation.
* Use GSAP only for choreographed timeline sequences.
* Use Rive only for interactive mascot or state-machine animation if the project has Rive assets.
* Use Lottie/dotLottie only for reusable animation assets if the project has Lottie assets.
* Do not add a new animation library without checking existing dependencies first.
* Do not hardcode random durations and easings across files.
* Prefer subtle but meaningful motion over chaotic movement.

Use Motion for:

* buttons
* hover/tap feedback
* cards
* menus
* panels
* layout transitions
* progress bars
* screen transitions
* draggable UI

Use GSAP for:

* title intro
* level-complete sequence
* victory sequence
* reward reveal
* XP bar cinematic fill
* chained timeline animations
* staged scene transitions

Use Rive for:

* mascot reactions
* character states
* idle/thinking/correct/wrong/level-up animation
* state-machine-controlled game feedback

Use Lottie/dotLottie for:

* XP burst
* badge unlock
* loading animation
* reward particles
* animated icons
* reusable celebration effects

Use Higgsfield for:

* Generating videos
* Generating audio
* generating images
* Generating 3D 

---

## Educational Game Rules

This project should teach through gameplay, not just contain a quiz.

For every learning feature:

* connect the game mechanic to the Java concept
* show the player why an answer is right or wrong
* give immediate feedback
* use visual examples where possible
* keep explanations short during gameplay
* allow deeper explanation after the attempt
* reward mastery, not just clicking through
* make failure useful and recoverable

For Java-learning content, prioritize:

* loops
* arrays
* methods
* conditionals
* sequential search
* binary search
* debugging logic
* code tracing
* variable state changes
* input/output reasoning

When building a teaching mechanic, include:

1. concept being taught
2. game action mapped to concept
3. player goal
4. feedback loop
5. success condition
6. failure condition
7. explanation after attempt
8. replay or retry path

---

## Security Guidance

Use Security Guidance after or during changes involving:

* authentication
* authorization
* user data
* APIs
* server actions
* file uploads
* environment variables
* external packages
* payment/subscription code
* database writes
* deployment configuration
* secrets
* browser storage
* third-party scripts

Security checks must include:

* exposed secrets
* unsafe environment variable usage
* client/server data leakage
* unsafe eval or dynamic script execution
* XSS risks
* command injection risks
* unsafe file access
* dependency risk
* excessive permissions
* insecure logging

Never commit `.env` files, tokens, local auth files, session files, or generated secrets.

---

## Legal Plugin

The Legal plugin is enabled only for drafting and workflow support.

Use it for:

* contract structure
* issue spotting
* plain-language summaries
* legal-adjacent drafting support
* compliance-style checklists
* reviewing wording for clarity

Do not:

* present legal conclusions as final
* claim to be a lawyer
* give jurisdiction-specific legal advice without research
* treat plugin output as professional legal advice

For actual legal decisions, tell the user to confirm with qualified counsel.

---

## Morph Compact

Use Morph Compact when:

* context is getting large
* the session has many files/results
* the task spans many components
* prior decisions need to be preserved concisely
* the conversation needs compaction before continuing

Before compacting:

* preserve current goal
* preserve files changed
* preserve commands run
* preserve errors
* preserve unresolved blockers
* preserve decisions the user made

Do not compact away test failures, warnings, or uncertainty.

---

## CodeBurn

Use CodeBurn after:

* long sessions
* repeated loops
* major refactors
* expensive debugging attempts
* major milestones
* many failed prompts
* high-token workflows

Use it to identify:

* wasted context
* repeated mistakes
* inefficient prompting
* files causing excessive context load
* workflow improvements
* CLAUDE.md updates that would prevent repeated problems

CodeBurn reads local sessions only. Do not upload session data or share logs externally without user approval.

---

## Package and Dependency Rules

Before installing any package:

1. inspect `package.json`
2. check existing dependencies
3. use Exa MCP if package syntax or best practice may be current-version dependent
4. explain why the dependency is needed
5. prefer existing libraries when they already solve the problem
6. avoid adding overlapping animation libraries without clear separation of responsibility

After installing or changing dependencies:

1. run install command
2. check lockfile changes
3. run typecheck/build/test where available
4. fix any errors caused by the dependency change

---

## Verification Commands

Do not assume command names. Inspect `package.json` first.

Common commands to try only if available:

```bash
npm run lint
npm run typecheck
npm run test
npm run build
npm run dev
```

If the project uses pnpm, yarn, bun, or another package manager, follow the existing lockfile and scripts.

A task is not complete until the relevant verification has passed or the remaining blocker is clearly documented.

---

## UI Review Checklist

For frontend work, review:

* does the screen look polished?
* does it look like a game, not a dashboard?
* is the hierarchy obvious?
* are primary actions clear?
* are hover/tap/focus states present?
* are loading/empty/error states handled?
* are success/failure states visually distinct?
* is the layout responsive?
* does animation support the user goal?
* is reduced motion respected?
* does the feature teach or reinforce the learning objective?

---

## Final Response Format

At the end of a coding task, respond with:

1. what changed
2. files changed
3. verification run
4. result of verification
5. remaining blockers, if any
6. suggested next step only if it is directly useful

Do not say “done” unless the implementation is actually working and verified.

---

## Current Tool Policy Summary

Ready and usable:

* Caveman
* Exa MCP
* Compound Engineering
* Skill Creator
* Frontend Design
* Security Guidance
* Legal
* Morph Compact
* CodeBurn
* motion-design skill

Not assumed usable unless later verified:

* Firecrawl
* OpenAI Codex
* BuildPartner.ai
* Higgsfield
* Morph MCP
* any unlisted third-party plugin

Primary workflow for this project:

1. Frontend Design for UI direction.
2. motion-design skill for animation design.
3. Compound Engineering for serious implementation.
4. Exa MCP for current docs.
5. Morph Compact for large context.
6. Security Guidance for risky changes.
7. CodeBurn after long sessions.
8. Skill Creator when a repeated workflow becomes reusable.
9. Caveman when context or verbosity needs compression.

Build the full solution, verify it, and leave no avoidable dangling thread.

