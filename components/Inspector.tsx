"use client";

import { motion } from "motion/react";
import type { DerivedState } from "@/lib/animate";
import { inspectorValueClassName } from "@/lib/animate";
import { hudTick } from "@/lib/motion";
import type { RunResponse } from "@/lib/types";

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

// The Inspector is the live runtime debugger only: where the drone is, the
// variables you watch(), and what you've harvested — all updating frame by
// frame as your code replays. Mastery + objective data live in the Objective
// panel, so they're intentionally not duplicated here.
export default function Inspector({ state, result }: { state: DerivedState; result: RunResponse | null }) {
  const watchEntries = Object.entries(state.watch);
  const resourceEntries = Object.entries(state.resources);

  return (
    <div className="panel inspector">
      <div className="panel-head">
        <h2>Inspector</h2>
        <span className="muted small">live</span>
      </div>

      <p className="inspector-help muted small">Values update tick by tick as your code runs.</p>

      <div className="section-label">drone</div>
      <div className="kv">
        <span>x · column</span>
        <LiveValue value={state.drone.x} />
      </div>
      <div className="kv">
        <span>y · row</span>
        <LiveValue value={state.drone.y} />
      </div>
      <div className="kv">
        <span>carrying</span>
        <LiveValue value={state.drone.carrying} />
      </div>

      <div className="section-label">your watched variables</div>
      {watchEntries.length === 0 ? (
        <p className="muted small">
          Track a value with <code>drone.watch(&quot;name&quot;, value)</code> and it shows up here.
        </p>
      ) : (
        watchEntries.map(([key, value]) => (
          <div className="kv" key={key}>
            <span>{key}</span>
            <LiveValue value={value} />
          </div>
        ))
      )}

      <div className="section-label">harvested</div>
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

      <div className="kv">
        <span>tick</span>
        <LiveValue value={state.tick} />
      </div>

      {result && (
        <div className="run-meta muted small">
          ran {result.ticks}/{result.tickLimit} ticks
        </div>
      )}
    </div>
  );
}
