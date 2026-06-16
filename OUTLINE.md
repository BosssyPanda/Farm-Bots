# Market Mayhem: Farm Bots — Outline

A browser game in the spirit of *The Farmer Was Replaced*, but you learn **Java** instead of
Python. You **write real Java code** to automate a farming **drone** on **one persistent
farm**. Your program runs **continuously** — the drone keeps farming, crops grow, and
resources pile up over time, just like TFWR. Your code is compiled and run on a server, and
the browser **animates** the farm and shows a **live variable inspector** as it runs.

> You learn by writing code that makes the farm work better. No quizzes — completing
> objectives unlocks new abilities, bigger fields, and new crops.

## What you'll learn

**8 core concepts** (master each through play): `for` loops • `while` loops • arrays •
methods • **sequential search** • **binary search** • **bubble sort** • **selection sort** —
with `if`/`else`, nested loops, 2D arrays, and `String` methods woven in. **Once all 8 are
mastered**, an **open-ended phase of recursion & logic puzzles** unlocks (and keeps growing).

## How it works (one glance)

```
┌─────────────── Browser (Next.js on Vercel) ───────────────┐
│  Objective + lesson  │  Java editor (Monaco)              │
│  + Hint button       │  [ Run ▶ ]                         │
│                      │                                     │
│  Animated farm   ◀───┼── Console: compiler / runtime      │
│  (drone + crops)     │   errors + System.out              │
│  Live inspector  ◀───┼── (drone state, your watched vars, │
│                      │    resources, tick)                │
└───────┬──────────────┴─────────────────────────────────────┘
        │ POST /api/run { code, farmState }
        ▼
   Vercel Function ──▶ Vercel Sandbox (secure microVM)
                         • writes your code as Strategy.java
                         • javac + java Runner  (tick-bounded)
                         • returns JSON: frames[] + progress
        ▲                   (drone actions + state, per tick)
        └──── animate the frames + persist the farm ─────────┘
```

You write the body of one method, then it runs continuously:

```java
public class Strategy {
    public void run(Drone drone, Farm farm) {
        while (true) {              // your program keeps the drone working
            // drive, plant, harvest, search, sort...
            drone.watch("tick", farm.tick());   // surface vars to the live inspector
        }
    }
}
```

…using a small beginner-friendly API: `drone.move(Direction.EAST)`, `drone.plant(Crop.WHEAT)`,
`drone.harvest()`, `drone.scan()`, `drone.x()/y()`, `drone.watch(name, value)`;
`farm.width()/height()/tick()`, and data like `farm.crops()`, `farm.prices()`,
`farm.moisture()`. **Actions cost ticks**; crops ripen over ticks; the world keeps moving.

## The journey (one persistent farm)

Each objective teaches a concept (with a short lesson first) and unlocks an ability:

| Concept | Objective | Unlocks |
|---------|-----------|---------|
| methods | First Sprout — drive & plant your first tiles | basic planting + starter field |
| `for` loops | The Long Rows — sweep a row, then the field | a bigger field |
| arrays | Stock the Stall — sum/average/max from `crops()`+`prices()` | the market stall + sensors |
| `while` loops | Harvest 'til Done — harvest until empty; decode a code (`%` `/`) | irrigation + a new crop |
| sequential search | Find the Crop — scan for a target; index or `-1` | a crop locator |
| binary search | Fast Market — search sorted prices (low/high/mid) | fast lookup (speed) |
| bubble sort | Tidy the Stalls — bubble-sort the stalls | sorted market view |
| selection sort | Pick the Best — rank crops by value | auto-prioritize |
| → recursion & puzzles | unlocks after all 8 cores are mastered | special puzzle fields, growing |

## How mastery works

A concept is **mastered** by using it **correctly and consistently over time** — not a
one-off pass. If you keep getting an already-taught core concept wrong, the game **recaps**
it (re-shows the lesson + a focused mini-challenge) before moving on. The recursion/puzzle
phase opens only once all 8 cores are mastered.

## Look & feel

Cozy, **no story** — a compact automation cockpit built **just for you**. The farm is the
visual anchor, with dark code/lesson panels around it, inspired by *The Farmer Was Replaced*
without copying its assets or branding. Everything is rendered with code (no static PNG
sprites): the drone glides, crops sprout and sway, harvests pop, and unlock/progress feedback
feels permanent.

## Where the details live

- **[AGENTS.md](AGENTS.md)** — the exact, authoritative spec: full design, the drone/farm API
  + tick model, the curriculum / objectives / abilities / mastery & recap rules, the
  run/validate JSON contract, the tech stack, the beginner-Java allow/forbid lists, and the
  Sandbox security model. Build from this.
- **[CLAUDE.md](CLAUDE.md)** — how to work in this repo: layout, build/dev/validate commands,
  conventions, what not to touch.
- **[MEMORY.md](MEMORY.md)** — project history, the pivot, and the implementation roadmap.

## Status

The repo now contains a working scaffold and real engine work: Next.js app shell, Java engine,
8 core objective registry, persistent state codec, local/Sandbox runner seam, reference
solutions, and Vitest/engine verification. The active implementation focus is wiring the
frontend to the generated objective catalog and persistent farm state, polishing the cockpit
UI, and verifying the full browser run flow.
