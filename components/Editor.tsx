"use client";

import dynamic from "next/dynamic";

const MonacoEditor = dynamic(() => import("@monaco-editor/react"), { ssr: false });

// Body-only editor — the surrounding window chrome (title bar, run, running
// pill) is provided by <GameWindow>. Keeps the locked Strategy.java wrapper
// rows so the player only edits the run-method body.
export default function Editor({
  value,
  onChange,
}: {
  value: string;
  onChange: (v: string) => void;
}) {
  return (
    <div className="editor">
      <div className="locked-code locked-code-open" aria-label="locked Strategy.java opening wrapper">
        <span>locked</span>
        <code>public class Strategy {"{"}</code>
      </div>
      <div className="editor-shell">
        <MonacoEditor
          height="100%"
          language="java"
          theme="vs-dark"
          value={value}
          onChange={(next) => onChange(next ?? "")}
          options={{
            automaticLayout: true,
            fontFamily: '"JetBrains Mono", ui-monospace, Menlo, Consolas, monospace',
            fontSize: 13,
            lineNumbers: "on",
            minimap: { enabled: false },
            scrollBeyondLastLine: false,
            tabSize: 4,
            wordWrap: "on",
            renderWhitespace: "selection",
            smoothScrolling: true,
            padding: { top: 12, bottom: 12 },
          }}
        />
      </div>
      <div className="locked-code locked-code-close" aria-label="locked Strategy.java closing wrapper">
        <span>locked</span>
        <code>{"}"}</code>
      </div>
    </div>
  );
}
