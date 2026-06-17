"use client";

import { motion } from "motion/react";
import type { DerivedState } from "@/lib/animate";
import { inspectorValueClassName } from "@/lib/animate";
import { hudTick } from "@/lib/motion";
import type { FarmState, ObjectiveInfo, RunResponse } from "@/lib/types";

// A value that re-pops whenever it changes (keyed on the rendered value).
function LiveValue({ value }: { value: string | number | boolean }) {
  return (
    <motion.b
      key={String(value)}
      className={inspectorValueClassName(value)}
      variants={hudTick}
      initial="hidden"
      animate="show"
      style={{ display: "inline-block", fontFamily: "var(--font-mono)", textAlign: "right" }}
    >
      {String(value)}
    </motion.b>
  );
}

export default function Inspector({
  state,
  farmState,
  objective,
  result,
}: {
  state: DerivedState;
  farmState: FarmState;
  objective: ObjectiveInfo;
  result: RunResponse | null;
}) {
  const watchEntries = Object.entries(state.watch);
  const resourceEntries = Object.entries(state.resources);
  const activeConcept =
    result?.objective.id === objective.id ? result.concepts[objective.concept] ?? farmState.concepts[objective.concept] : farmState.concepts[objective.concept];
  const dataEntries = [
    ["prices()", objective.prices],
    ["crops()", objective.crops],
    ["moisture()", objective.moisture],
  ] as const;

  return (
    <div className="panel inspector">
      <div className="panel-head">
        <h2>Inspector</h2>
        <span className="muted small">live</span>
      </div>

      <div className="kv">
        <span>objective</span>
        <b className={inspectorValueClassName(objective.id)}>{objective.id}</b>
      </div>
      <div className="kv">
        <span>drone.x</span>
        <LiveValue value={state.drone.x} />
      </div>
      <div className="kv">
        <span>drone.y</span>
        <LiveValue value={state.drone.y} />
      </div>
      <div className="kv">
        <span>carrying</span>
        <LiveValue value={state.drone.carrying} />
      </div>
      <div className="kv">
        <span>tick</span>
        <LiveValue value={state.tick} />
      </div>

      <div className="section-label">watch()</div>
      {watchEntries.length === 0 ? (
        <p className="muted small">Track variables with drone.watch(&quot;name&quot;, value).</p>
      ) : (
        watchEntries.map(([key, value]) => (
          <div className="kv" key={key}>
            <span>{key}</span>
            <LiveValue value={value} />
          </div>
        ))
      )}

      <div className="section-label">resources</div>
      {resourceEntries.length === 0 ? (
        <p className="muted small">No harvested resources yet.</p>
      ) : (
        resourceEntries.map(([key, value]) => (
          <div className="kv" key={key}>
            <span>{key}</span>
            <LiveValue value={value} />
          </div>
        ))
      )}

      <div className="section-label">available data</div>
      <div className="data-list">
        {dataEntries.map(([label, values]) => (
          <div className="kv" key={label}>
            <span>{label}</span>
            <b className={inspectorValueClassName(values)}>{values.length ? values.join(", ") : "empty"}</b>
          </div>
        ))}
      </div>

      <div className="section-label">mastery</div>
      {activeConcept ? (
        <div className="insight-grid compact">
          <div className="insight">
            <span>streak</span>
            <b>{activeConcept.correctStreak}</b>
          </div>
          <div className="insight">
            <span>fails</span>
            <b>{activeConcept.failCount}</b>
          </div>
          <div className="insight">
            <span>mastered</span>
            <b>{activeConcept.mastered ? "yes" : "no"}</b>
          </div>
          <div className="insight">
            <span>recap</span>
            <b>{activeConcept.recapDue ? "due" : "clear"}</b>
          </div>
        </div>
      ) : (
        <p className="muted small">Progress appears after the first run.</p>
      )}

      {result && (
        <div className="run-meta muted small">
          ran {result.ticks}/{result.tickLimit} ticks
        </div>
      )}
    </div>
  );
}
