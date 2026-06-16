"use client";

import type { ObjectiveInfo } from "@/lib/types";

export default function Controls({
  onRun,
  onResetCurrent,
  onResetFarm,
  running,
  objective,
}: {
  onRun: () => void;
  onResetCurrent: () => void;
  onResetFarm: () => void;
  running: boolean;
  objective: ObjectiveInfo;
}) {
  return (
    <div className="controls">
      <div className="controls-meta">
        <span className="section-label">current objective</span>
        <strong>{objective.title}</strong>
        <span className="muted small">{objective.concept}</span>
      </div>
      <div className="controls-actions">
        <button className="btn ghost" onClick={onResetCurrent} disabled={running}>
          Reset code
        </button>
        <button className="btn ghost" onClick={onResetFarm} disabled={running}>
          Reset farm
        </button>
        <button className="btn run" onClick={onRun} disabled={running}>
          {running ? "Running..." : "Run"}
        </button>
      </div>
    </div>
  );
}
