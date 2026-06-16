"use client";

import type { DerivedState } from "@/lib/animate";
import { inspectorValueClassName } from "@/lib/animate";
import type { FarmState, ObjectiveInfo, RunResponse } from "@/lib/types";

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
        <b className={inspectorValueClassName(state.drone.x)}>{state.drone.x}</b>
      </div>
      <div className="kv">
        <span>drone.y</span>
        <b className={inspectorValueClassName(state.drone.y)}>{state.drone.y}</b>
      </div>
      <div className="kv">
        <span>carrying</span>
        <b className={inspectorValueClassName(state.drone.carrying)}>{state.drone.carrying}</b>
      </div>
      <div className="kv">
        <span>tick</span>
        <b className={inspectorValueClassName(state.tick)}>{state.tick}</b>
      </div>

      <div className="section-label">watch()</div>
      {watchEntries.length === 0 ? (
        <p className="muted small">Track variables with drone.watch(&quot;name&quot;, value).</p>
      ) : (
        watchEntries.map(([key, value]) => (
          <div className="kv" key={key}>
            <span>{key}</span>
            <b className={inspectorValueClassName(value)}>{String(value)}</b>
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
            <b className={inspectorValueClassName(value)}>{value}</b>
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
