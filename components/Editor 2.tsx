"use client";

// Skeleton editor: a styled code textarea. The Editor boundary is intentional so
// swapping in Monaco (@monaco-editor/react, Java mode) later is a one-component change.

export default function Editor({
  value,
  onChange,
}: {
  value: string;
  onChange: (v: string) => void;
}) {
  return (
    <div className="panel editor">
      <div className="panel-head">
        <h2>Strategy.java</h2>
        <span className="muted small">your code</span>
      </div>
      <textarea
        className="code"
        spellCheck={false}
        value={value}
        onChange={(e) => onChange(e.target.value)}
      />
    </div>
  );
}
