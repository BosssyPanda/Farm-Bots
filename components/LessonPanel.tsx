"use client";

import { useMemo, useState } from "react";
import { AnimatePresence, motion } from "motion/react";
import { getObjectiveCheckDisplay, hintLevelForObjective } from "@/lib/animate";
import { getConceptLesson } from "@/lib/curriculum";
import { popIn, pressable, tabIndicator, tabSwap } from "@/lib/motion";
import type { CheckResult, ConceptProgress, ObjectiveInfo, ObjectiveResult } from "@/lib/types";

type TabId = "goal" | "learn" | "progress";

const TABS: { id: TabId; label: string }[] = [
  { id: "goal", label: "Goal" },
  { id: "learn", label: "Learn" },
  { id: "progress", label: "Progress" },
];

export default function LessonPanel({
  objective,
  result,
  resultObjective,
  progress,
  unlocked,
  newlyUnlocked,
}: {
  objective: ObjectiveInfo;
  result: ObjectiveResult | null;
  resultObjective?: ObjectiveInfo | null;
  progress: ConceptProgress | undefined;
  unlocked: string[];
  newlyUnlocked?: string[];
}) {
  const [tab, setTab] = useState<TabId>("goal");
  const newUnlocks = newlyUnlocked ?? [];

  return (
    <div className="panel lesson">
      <div className="lesson-top">
        <div>
          <span className="concept-pill">{objective.concept.replace(/-/g, " ")}</span>
          <h2>{objective.title}</h2>
        </div>
        <div className="lesson-status">
          <span className={progress?.mastered ? "status-pill success" : "status-pill"}>streak {progress?.correctStreak ?? 0}</span>
          {newUnlocks.length > 0 ? <span className="status-pill success">unlock!</span> : null}
        </div>
      </div>

      <div className="tabbar" role="tablist" aria-label="Objective sections">
        {TABS.map((t) => (
          <button
            key={t.id}
            role="tab"
            aria-selected={tab === t.id}
            className={`tabbar-btn${tab === t.id ? " active" : ""}`}
            onClick={() => setTab(t.id)}
          >
            {t.label}
            {tab === t.id ? <motion.span layoutId="lesson-tab-underline" className="tabbar-underline" transition={tabIndicator} /> : null}
          </button>
        ))}
      </div>

      <AnimatePresence mode="wait" initial={false}>
        <motion.div key={tab} variants={tabSwap} initial="hidden" animate="show" exit="exit" className="tab-body">
          {tab === "goal" ? (
            <GoalTab objective={objective} result={result} resultObjective={resultObjective} />
          ) : tab === "learn" ? (
            <LearnTab objective={objective} />
          ) : (
            <ProgressTab objective={objective} progress={progress} unlocked={unlocked} newUnlocks={newUnlocks} />
          )}
        </motion.div>
      </AnimatePresence>
    </div>
  );
}

/* ----------------------------------------------------------------- Goal tab */
function GoalTab({
  objective,
  result,
  resultObjective,
}: {
  objective: ObjectiveInfo;
  result: ObjectiveResult | null;
  resultObjective?: ObjectiveInfo | null;
}) {
  const [hintState, setHintState] = useState({ objectiveId: objective.id, level: 0 });
  const hintLevel = hintLevelForObjective(hintState, objective.id);
  const checkDisplay = getObjectiveCheckDisplay(objective, result, resultObjective);

  const dataInPlay = useMemo(() => {
    const parts: string[] = [];
    if (objective.crops.length) parts.push(`crops() · ${objective.crops.length}`);
    if (objective.prices.length) parts.push(`prices() · ${objective.prices.length}`);
    if (objective.moisture.length) parts.push(`moisture() · ${objective.moisture.length}`);
    return parts;
  }, [objective.crops.length, objective.moisture.length, objective.prices.length]);

  return (
    <>
      <p className="lesson-text">{objective.lesson}</p>

      <section className="lesson-section">
        <div className="section-label">do this</div>
        <div data-testid="current-objective-checks">
          {checkDisplay.currentChecks.length === 0 ? (
            <p className="muted small">
              {checkDisplay.hasMismatchedCompletedResult
                ? "This is the next objective. Run the strategy to populate its checks."
                : "Run the strategy to see which steps you've completed."}
            </p>
          ) : (
            <ul className="checks">
              {checkDisplay.currentChecks.map((check) => (
                <CheckRow key={check.id} check={check} />
              ))}
            </ul>
          )}
        </div>
        {checkDisplay.hasMismatchedCompletedResult && (
          <div className="completed-result" data-testid="completed-objective-checks">
            <div className="section-label">Latest result: {checkDisplay.completedTitle}</div>
            <ul className="checks">
              {checkDisplay.completedChecks.map((check) => (
                <CheckRow key={check.id} check={check} />
              ))}
            </ul>
          </div>
        )}
      </section>

      {dataInPlay.length > 0 && (
        <section className="lesson-section">
          <div className="section-label">data in play</div>
          <div className="chip-row">
            {dataInPlay.map((d) => (
              <span className="status-pill" key={d}>
                {d}
              </span>
            ))}
          </div>
        </section>
      )}

      <section className="lesson-section">
        <div className="section-label">stuck?</div>
        <div className="hints">
          <AnimatePresence initial={false}>
            {objective.hints.slice(0, hintLevel).map((hint, index) => (
              <motion.p key={`${objective.id}-${index}`} className="hint" variants={popIn} initial="hidden" animate="show" exit="exit">
                {hint}
              </motion.p>
            ))}
          </AnimatePresence>
          {hintLevel < objective.hints.length && (
            <motion.button
              className="btn ghost"
              {...pressable}
              onClick={() =>
                setHintState((current) => ({
                  objectiveId: objective.id,
                  level: current.objectiveId === objective.id ? current.level + 1 : 1,
                }))
              }
            >
              {hintLevel === 0 ? "Need a hint?" : "Another hint"}
            </motion.button>
          )}
        </div>
      </section>
    </>
  );
}

/* ---------------------------------------------------------------- Learn tab */
function LearnTab({ objective }: { objective: ObjectiveInfo }) {
  // Players can branch into a woven sub-concept and come back.
  const [activeConcept, setActiveConcept] = useState(objective.concept);
  const lesson = getConceptLesson(activeConcept);
  const isPrimary = activeConcept === objective.concept;

  if (!lesson) {
    // No deep lesson authored — fall back to the engine's worked example.
    return (
      <section className="lesson-section">
        <div className="section-label">worked example</div>
        <pre className="example-code">{objective.workedExample}</pre>
      </section>
    );
  }

  return (
    <>
      {!isPrimary && (
        <motion.button className="btn ghost back-link" {...pressable} onClick={() => setActiveConcept(objective.concept)}>
          ← back to {objective.concept.replace(/-/g, " ")}
        </motion.button>
      )}

      <p className="learn-summary">{lesson.summary}</p>

      <section className="lesson-section">
        <div className="section-label">what it is</div>
        {lesson.explanation.map((para, i) => (
          <p className="lesson-text" key={i}>
            {para}
          </p>
        ))}
      </section>

      <section className="lesson-section">
        <div className="section-label">syntax</div>
        <pre className="example-code">{lesson.syntax}</pre>
      </section>

      <section className="lesson-section">
        <div className="section-label">step by step</div>
        <ol className="walkthrough">
          {lesson.walkthrough.map((step, i) => (
            <li key={i}>
              <code>{step.code}</code>
              <span className="walk-note">{step.note}</span>
            </li>
          ))}
        </ol>
      </section>

      {isPrimary && objective.workedExample ? (
        <section className="lesson-section">
          <div className="section-label">example for this objective</div>
          <pre className="example-code">{objective.workedExample}</pre>
        </section>
      ) : null}

      <section className="lesson-section">
        <div className="section-label">in the game</div>
        <p className="lesson-text">{lesson.gameMapping}</p>
      </section>

      <section className="lesson-section">
        <div className="section-label">common mistakes</div>
        <ul className="mistakes">
          {lesson.commonMistakes.map((m, i) => (
            <li key={i}>{m}</li>
          ))}
        </ul>
      </section>

      {lesson.related && lesson.related.length > 0 && (
        <section className="lesson-section">
          <div className="section-label">also used here</div>
          <div className="chip-row">
            {lesson.related.map((rc) => {
              const related = getConceptLesson(rc);
              if (!related) return null;
              return (
                <motion.button key={rc} className="concept-chip" {...pressable} onClick={() => setActiveConcept(rc)}>
                  {related.title}
                </motion.button>
              );
            })}
          </div>
        </section>
      )}
    </>
  );
}

/* ------------------------------------------------------------- Progress tab */
function ProgressTab({
  objective,
  progress,
  unlocked,
  newUnlocks,
}: {
  objective: ObjectiveInfo;
  progress: ConceptProgress | undefined;
  unlocked: string[];
  newUnlocks: string[];
}) {
  return (
    <>
      <AnimatePresence>
        {newUnlocks.length > 0 && (
          <motion.section className="unlock-banner" aria-live="polite" variants={popIn} initial="hidden" animate="show" exit="exit">
            <div>
              <span className="section-label">New unlock</span>
              <strong>{newUnlocks.join(", ")}</strong>
            </div>
          </motion.section>
        )}
      </AnimatePresence>

      <section className="lesson-section">
        <div className="section-label">mastery</div>
        <div className="insight-grid">
          <div className="insight">
            <span>correct streak</span>
            <b>{progress?.correctStreak ?? 0}</b>
          </div>
          <div className="insight">
            <span>fail count</span>
            <b>{progress?.failCount ?? 0}</b>
          </div>
          <div className="insight">
            <span>mastered</span>
            <b>{progress?.mastered ? "yes" : "no"}</b>
          </div>
          <div className="insight">
            <span>recap</span>
            <b>{progress?.recapDue ? "due" : "clear"}</b>
          </div>
        </div>
        <p className="muted small mastery-note">Master a concept by passing its objective and reusing it correctly (streak ≥ 3).</p>
      </section>

      <section className="lesson-section">
        <div className="section-label">unlocks</div>
        <div className="chip-row">
          <span className="status-pill">{objective.unlock}</span>
          {unlocked.length > 0 ? (
            unlocked.map((item) => (
              <span className="status-pill success" key={item}>
                {item}
              </span>
            ))
          ) : (
            <span className="muted small">No permanent unlocks yet.</span>
          )}
        </div>
      </section>
    </>
  );
}

function CheckRow({ check }: { check: CheckResult }) {
  return (
    <li className={check.passed ? "pass" : "fail"}>
      <span className="mark">{check.passed ? "✓" : "×"}</span>
      {check.label}
    </li>
  );
}
