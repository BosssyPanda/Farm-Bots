"use client";

import { useCallback, useEffect, useMemo, useRef, useState } from "react";
import { AnimatePresence, motion } from "motion/react";
import Celebration from "@/components/Celebration";
import ConsolePanel from "@/components/Console";
import Controls from "@/components/Controls";
import Editor from "@/components/Editor";
import FarmView from "@/components/FarmView";
import GameWindow from "@/components/GameWindow";
import Inspector from "@/components/Inspector";
import LessonPanel from "@/components/LessonPanel";
import ResourceBar from "@/components/ResourceBar";
import Toolbar from "@/components/Toolbar";
import { deriveState } from "@/lib/animate";
import { DURATION, EASE } from "@/lib/motion";
import {
  buildStrategySource,
  clearAllStrategyCode,
  clearFarmState,
  clearStrategyCode,
  computeNewlyUnlocked,
  createDefaultFarmState,
  extractStrategyEditableSource,
  isValidRunStateTransition,
  loadFarmState,
  loadStrategyCode,
  resolveCommittedFarmStateAfterPlayback,
  saveFarmState,
  saveStrategyCode,
} from "@/lib/persist";
import { FALLBACK_CATALOG } from "@/lib/types";
import type { FarmState, Frame, ObjectiveCatalog, ObjectiveInfo, RunResponse } from "@/lib/types";

const PLAYBACK_MS = 450;
const EMPTY_FRAMES: Frame[] = [];

interface PlaybackRun {
  response: RunResponse;
  baseFarmState: FarmState;
  canCommit: boolean;
}

export default function Page() {
  const [catalog, setCatalog] = useState<ObjectiveCatalog>(FALLBACK_CATALOG);
  const [storageReady, setStorageReady] = useState(false);
  const [farmState, setFarmState] = useState<FarmState>(() => createDefaultFarmState(FALLBACK_CATALOG.objectives[0].id));
  const [result, setResult] = useState<RunResponse | null>(null);
  const [playback, setPlayback] = useState<PlaybackRun | null>(null);
  const [index, setIndex] = useState(-1);
  const [running, setRunning] = useState(false);
  const [playing, setPlaying] = useState(false);
  const [celebration, setCelebration] = useState<{ key: number; title: string; subtitle?: string } | null>(null);
  const [showInfo, setShowInfo] = useState(true);
  const stageRef = useRef<HTMLDivElement | null>(null);
  const [codeDrafts, setCodeDrafts] = useState<Record<string, string>>(() => {
    const initialObjective = FALLBACK_CATALOG.objectives[0];
    return {
      [initialObjective.id]: extractStrategyEditableSource(initialObjective.starter),
    };
  });
  const timer = useRef<ReturnType<typeof setInterval> | null>(null);

  const stopTimer = useCallback(() => {
    if (timer.current) {
      clearInterval(timer.current);
      timer.current = null;
    }
  }, []);

  useEffect(() => {
    let alive = true;
    queueMicrotask(() => {
      if (!alive) return;
      const persistedFarmState = loadFarmState(FALLBACK_CATALOG.objectives[0].id);
      const persistedObjective = resolveObjective(FALLBACK_CATALOG, persistedFarmState.currentObjectiveId);
      setFarmState(persistedFarmState);
      setCodeDrafts((drafts) => ({
        ...drafts,
        [persistedObjective.id]: extractStrategyEditableSource(loadStrategyCode(persistedObjective.id, persistedObjective.starter)),
      }));
      setStorageReady(true);
    });
    return () => {
      alive = false;
    };
  }, []);

  useEffect(() => {
    let alive = true;

    void fetch("/api/objectives")
      .then(async (response) => {
        if (!response.ok) throw new Error(`Objective catalog request failed: ${response.status}`);
        return (await response.json()) as ObjectiveCatalog;
      })
      .then((nextCatalog) => {
        if (alive) setCatalog(nextCatalog);
      })
      .catch(() => {
        if (alive) setCatalog(FALLBACK_CATALOG);
      });

    return () => {
      alive = false;
    };
  }, []);

  const objective = useMemo(() => resolveObjective(catalog, farmState.currentObjectiveId), [catalog, farmState.currentObjectiveId]);
  const code = codeDrafts[objective.id] ?? extractStrategyEditableSource(loadStrategyCode(objective.id, objective.starter));
  const runObjective = result?.objective ?? null;
  const resultObjective = result ? resolveObjective(catalog, result.objective.id) : null;
  const progress =
    result?.objective.id === objective.id ? result.concepts[objective.concept] ?? farmState.concepts[objective.concept] : farmState.concepts[objective.concept];
  const unlocked = farmState.unlocked;
  const newlyUnlocked = result?.newlyUnlocked ?? [];
  const frames = playback?.response.frames ?? EMPTY_FRAMES;
  const renderFarmState = playback?.baseFarmState ?? farmState;
  const state = useMemo(() => deriveState(renderFarmState, frames, index), [renderFarmState, frames, index]);
  useEffect(() => {
    if (storageReady) saveFarmState(farmState);
  }, [farmState, storageReady]);

  // Fire the GSAP celebration once per passing run (key bump remounts the overlay).
  const celebrateSeq = useRef(0);
  useEffect(() => {
    if (!result?.objective?.passed) return;
    celebrateSeq.current += 1;
    const unlocks = result.newlyUnlocked ?? [];
    setCelebration({
      key: celebrateSeq.current,
      title: "Objective complete!",
      subtitle: unlocks.length ? `Unlocked ${unlocks.join(", ")}` : undefined,
    });
  }, [result]);

  const runCode = useCallback(async () => {
    const submittedFarmState = farmState;
    setRunning(true);
    stopTimer();
    setPlayback(null);
    setResult(null);
    setIndex(-1);
    setPlaying(false);

    try {
      const response = await fetch("/api/run", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ code: buildStrategySource(code), farmState: submittedFarmState }),
      });
      const data = (await response.json()) as RunResponse;
      const runNewlyUnlocked = computeNewlyUnlocked(submittedFarmState, data);
      const normalizedResult: RunResponse = { ...data, newlyUnlocked: runNewlyUnlocked };
      const canCommit = isValidRunStateTransition(normalizedResult);

      setResult(normalizedResult);
      setPlayback({ response: normalizedResult, baseFarmState: submittedFarmState, canCommit });

      if (normalizedResult.frames.length > 0) {
        setPlaying(true);
      } else {
        setFarmState((current) => resolveCommittedFarmStateAfterPlayback(current, normalizedResult, canCommit));
        setPlayback(null);
      }
    } catch (error) {
      const failure: RunResponse = {
        ok: false,
        compiled: false,
        compileErrors: "",
        runtimeError: `Could not reach /api/run: ${String(error)}`,
        stdout: "",
        ticks: 0,
        tickLimit: 0,
        frames: [],
        farmState,
        objective: { id: "", concept: "", checks: [], passed: false },
        unlocked: [],
        newlyUnlocked: [],
        concepts: {},
      };
      setResult(failure);
      setPlayback({ response: failure, baseFarmState: submittedFarmState, canCommit: false });
    } finally {
      setRunning(false);
    }
  }, [code, farmState, stopTimer]);

  useEffect(() => {
    if (!playing) return;

    timer.current = setInterval(() => {
      setIndex((current) => {
        const next = Math.min(current + 1, frames.length - 1);
        if (next >= frames.length - 1) {
          setPlaying(false);
          if (playback) {
            setFarmState((currentFarmState) =>
              resolveCommittedFarmStateAfterPlayback(currentFarmState, playback.response, playback.canCommit),
            );
            setPlayback(null);
          }
        }
        return next;
      });
    }, PLAYBACK_MS);

    return () => stopTimer();
  }, [frames.length, playback, playing, stopTimer]);

  const handleCodeChange = useCallback(
    (next: string) => {
      setCodeDrafts((drafts) => ({ ...drafts, [objective.id]: next }));
      saveStrategyCode(objective.id, next);
    },
    [objective.id],
  );

  const resetPlayback = useCallback(() => {
    stopTimer();
    setResult(null);
    setPlayback(null);
    setIndex(-1);
    setPlaying(false);
  }, [stopTimer]);

  const resetCurrentCode = useCallback(() => {
    resetPlayback();
    clearStrategyCode(objective.id);
    setCodeDrafts((drafts) => ({ ...drafts, [objective.id]: extractStrategyEditableSource(objective.starter) }));
  }, [objective.id, objective.starter, resetPlayback]);

  const resetFarm = useCallback(() => {
    const firstObjectiveId = catalog.objectives[0]?.id ?? FALLBACK_CATALOG.objectives[0].id;
    const nextState = createDefaultFarmState(firstObjectiveId);
    resetPlayback();
    clearFarmState();
    clearAllStrategyCode();
    saveFarmState(nextState);
    setFarmState(nextState);
    setCodeDrafts({});
  }, [catalog.objectives, resetPlayback]);

  const runPill = (
    <AnimatePresence mode="wait" initial={false}>
      <motion.span
        key={running ? "running" : "idle"}
        className={`gwin-pill${running ? "" : " idle"}`}
        initial={{ opacity: 0, y: -3 }}
        animate={{ opacity: 1, y: 0 }}
        exit={{ opacity: 0, y: 3 }}
        transition={{ duration: DURATION.quick, ease: EASE.standard }}
      >
        {running ? "● running" : "✎ editing"}
      </motion.span>
    </AnimatePresence>
  );

  return (
    <main className="app shell">
      <FarmView
        width={renderFarmState.width}
        height={renderFarmState.height}
        farmState={renderFarmState}
        state={state}
        running={running || playing}
      />

      <ResourceBar resources={state.resources} tick={state.tick} />
      <Toolbar onRun={runCode} onResetFarm={resetFarm} onToggleInfo={() => setShowInfo((v) => !v)} running={running} />

      <div className="stage" ref={stageRef}>
        <AnimatePresence>
          {showInfo ? (
            <GameWindow title="Objective" icon="✦" constraintsRef={stageRef} initial={{ x: 20, y: 56 }} width={360} closable>
              <div className="gwin-pad">
                <LessonPanel
                  objective={objective}
                  result={runObjective}
                  resultObjective={resultObjective}
                  progress={progress}
                  unlocked={unlocked}
                  newlyUnlocked={newlyUnlocked}
                />
              </div>
            </GameWindow>
          ) : null}
        </AnimatePresence>

        <GameWindow
          title="Strategy.java"
          constraintsRef={stageRef}
          initial={{ x: 400, y: 56 }}
          width={470}
          onRun={runCode}
          runningPill={runPill}
        >
          <Editor value={code} onChange={handleCodeChange} />
          <div className="gwin-pad">
            <Controls
              onRun={runCode}
              onResetCurrent={resetCurrentCode}
              onResetFarm={resetFarm}
              running={running}
              objective={objective}
            />
          </div>
        </GameWindow>

        <GameWindow title="Inspector" icon="◉" constraintsRef={stageRef} initial={{ x: 892, y: 96 }} width={340}>
          <div className="gwin-pad">
            <Inspector state={state} farmState={farmState} objective={objective} result={result} />
          </div>
        </GameWindow>

        <GameWindow title="Console" icon="›_" constraintsRef={stageRef} initial={{ x: 400, y: 612 }} width={430}>
          <div className="gwin-pad">
            <ConsolePanel result={result} />
          </div>
        </GameWindow>
      </div>

      {celebration ? <Celebration key={celebration.key} title={celebration.title} subtitle={celebration.subtitle} /> : null}
    </main>
  );
}

function resolveObjective(catalog: ObjectiveCatalog, currentObjectiveId: string): ObjectiveInfo {
  return catalog.objectives.find((objective) => objective.id === currentObjectiveId) ?? catalog.objectives[0] ?? FALLBACK_CATALOG.objectives[0];
}
