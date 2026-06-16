"use client";

import type { DerivedState } from "@/lib/animate";
import type { RunResponse } from "@/lib/types";

export default function Inspector({
  state,
  result,
}: {
  state: DerivedState;
  result: RunResponse | null;
}) {
  const watchEntries = Object.entries(state.watch);
  const resourceEntries = Object.entries(state.resources);

  return (
    <div className="panel inspector">
      <div className="panel-head">
        <h2>Inspector</h2>
        <span className="muted small">live</span>
      </div>

      <div className="kv">
        <span>drone.x</span>
        <b>{state.drone.x}</b>
      </div>
      <div className="kv">
        <span>drone.y</span>
        <b>{state.drone.y}</b>
      </div>
      <div className="kv">
        <span>carrying</span>
        <b>{state.drone.carrying}</b>
      </div>
      <div className="kv">
        <span>tick</span>
        <b>{state.tick}</b>
      </div>

      <div className="section-label">watch()</div>
      {watchEntries.length === 0 ? (
        <p className="muted small">call drone.watch(&quot;name&quot;, value) to track a variable</p>
      ) : (
        watchEntries.map(([k, v]) => (
          <div className="kv" key={k}>
            <span>{k}</span>
            <b>{String(v)}</b>
          </div>
        ))
      )}

      <div className="section-label">resources</div>
      {resourceEntries.length === 0 ? (
        <p className="muted small">none harvested yet</p>
      ) : (
        resourceEntries.map(([k, v]) => (
          <div className="kv" key={k}>
            <span>{k}</span>
            <b>{v}</b>
          </div>
        ))
      )}

      {result && (
        <div className="run-meta muted small">
          ran {result.ticks}/{result.tickLimit} ticks
        </div>
      )}
    </div>
  );
}
