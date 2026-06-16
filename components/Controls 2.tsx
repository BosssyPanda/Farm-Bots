"use client";

export default function Controls({
  onRun,
  running,
}: {
  onRun: () => void;
  running: boolean;
}) {
  return (
    <div className="controls">
      <button className="btn run" onClick={onRun} disabled={running}>
        {running ? "Running…" : "▶ Run"}
      </button>
      <span className="muted small">mock execution — real Java wiring is next</span>
    </div>
  );
}
