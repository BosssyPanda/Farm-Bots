"use client";

import { useMemo } from "react";
import { motion } from "motion/react";
import { getConceptLesson, WOVEN_CONCEPTS } from "@/lib/curriculum";
import { panelEnter, staggerContainer } from "@/lib/motion";
import type { FarmState, ObjectiveCatalog } from "@/lib/types";

type Status = "mastered" | "current" | "available" | "locked";

const STATUS_META: Record<Status, { mark: string; label: string }> = {
  mastered: { mark: "✓", label: "mastered" },
  current: { mark: "●", label: "current" },
  available: { mark: "○", label: "started" },
  locked: { mark: "◌", label: "locked" },
};

interface Row {
  concept: string;
  title: string;
  summary: string;
  via: string;
  status: Status;
}

// The learning roadmap: every core concept in order with its status, plus the
// open-ended phase and the sub-topics woven throughout. Pure render from the
// objective catalog + saved concept progress + the curriculum content.
export default function ConceptsMap({ catalog, farmState }: { catalog: ObjectiveCatalog; farmState: FarmState }) {
  const rows = useMemo<Row[]>(() => {
    const objectives = catalog.objectives;
    const currentIndex = Math.max(0, objectives.findIndex((o) => o.id === farmState.currentObjectiveId));

    // Cores in curriculum order, plus the open-ended phase appended.
    const order = [...catalog.conceptOrder];
    const openObjective = objectives.find((o) => o.concept === "recursion-puzzles");
    if (openObjective && !order.includes("recursion-puzzles")) order.push("recursion-puzzles");

    return order.map((concept) => {
      const objective = objectives.find((o) => o.concept === concept);
      const objIndex = objective ? objectives.indexOf(objective) : Number.MAX_SAFE_INTEGER;
      const mastered = farmState.concepts[concept]?.mastered ?? false;
      let status: Status;
      if (mastered) status = "mastered";
      else if (objIndex === currentIndex) status = "current";
      else if (objIndex < currentIndex) status = "available";
      else status = "locked";

      const lesson = getConceptLesson(concept);
      return {
        concept,
        title: lesson?.title ?? concept.replace(/-/g, " "),
        summary: lesson?.summary ?? "",
        via: objective ? objective.title : "",
        status,
      };
    });
  }, [catalog, farmState.concepts, farmState.currentObjectiveId]);

  const woven = useMemo(
    () => WOVEN_CONCEPTS.map((c) => getConceptLesson(c)).filter((l): l is NonNullable<typeof l> => Boolean(l)),
    [],
  );

  const masteredCount = rows.filter((r) => r.status === "mastered").length;
  const coreCount = rows.filter((r) => r.concept !== "recursion-puzzles").length;

  return (
    <div className="panel concepts-map">
      <p className="muted small concepts-intro">
        Master the 8 core concepts to unlock the open-ended recursion phase.{" "}
        <b>
          {masteredCount}/{coreCount}
        </b>{" "}
        mastered.
      </p>

      <motion.ol className="concept-rows" variants={staggerContainer} initial="hidden" animate="show">
        {rows.map((row, i) => (
          <motion.li key={row.concept} className={`concept-row ${row.status}`} variants={panelEnter}>
            <span className="concept-num">{row.concept === "recursion-puzzles" ? "★" : i + 1}</span>
            <div className="concept-main">
              <div className="concept-row-head">
                <b className="concept-row-title">{row.title}</b>
                <span className={`concept-status ${row.status}`}>
                  {STATUS_META[row.status].mark} {STATUS_META[row.status].label}
                </span>
              </div>
              {row.summary ? <span className="concept-row-summary">{row.summary}</span> : null}
              {row.via ? <span className="concept-row-via">via {row.via}</span> : null}
            </div>
          </motion.li>
        ))}
      </motion.ol>

      {woven.length > 0 && (
        <section className="lesson-section">
          <div className="section-label">woven throughout</div>
          <div className="chip-row">
            {woven.map((l) => (
              <span className="status-pill" key={l.concept} title={l.summary}>
                {l.title}
              </span>
            ))}
          </div>
        </section>
      )}
    </div>
  );
}
