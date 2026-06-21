"use client";

import { useState, type PointerEvent, type ReactNode, type RefObject } from "react";
import { motion, useDragControls } from "motion/react";
import { DURATION, EASE } from "@/lib/motion";
import type { WindowMode } from "@/lib/persist";

// A game "IDE window" — the core chrome of the shell, modelled on *The Farmer
// Was Replaced*: a title bar with an optional ▶ run glyph, a status pill, and
// float/dock + minimise/close controls.
//
// Two modes:
//   • docked   → sits in a side rail in normal flow (no drag), tidy by default.
//   • floating → absolutely positioned, draggable from the title bar.
// The parent owns which mode each window is in; the ⤢ / ⤡ button flips it.

let zCounter = 30;

export default function GameWindow({
  title,
  icon = "▸",
  children,
  constraintsRef,
  mode,
  initial,
  width = 360,
  runningPill,
  onRun,
  onFloat,
  onDock,
  closable = false,
  onClose,
  className = "",
}: {
  title: string;
  icon?: string;
  children: ReactNode;
  constraintsRef: RefObject<HTMLDivElement | null>;
  mode: WindowMode;
  /** Spawn position when floating. */
  initial: { x: number; y: number };
  width?: number;
  runningPill?: ReactNode;
  onRun?: () => void;
  onFloat?: () => void;
  onDock?: () => void;
  closable?: boolean;
  onClose?: () => void;
  className?: string;
}) {
  // Stable initial z for SSR (a module counter would drift between the
  // long-lived server and a fresh client, causing hydration mismatch). Focus
  // bumps z on the client so a raised floating window comes to front.
  const [z, setZ] = useState(20);
  const [minimized, setMinimized] = useState(false);
  const dragControls = useDragControls();

  const floating = mode === "floating";
  const focus = () => setZ(++zCounter);
  const startDrag = (e: PointerEvent) => {
    focus();
    dragControls.start(e);
  };

  const style = floating ? { zIndex: z, width, left: initial.x, top: initial.y } : { width: "100%" as const };

  return (
    <motion.section
      className={`gwin ${floating ? "floating" : "docked"} ${className}`}
      style={style}
      initial={{ opacity: 0, scale: 0.97 }}
      animate={{ opacity: 1, scale: 1 }}
      transition={{ duration: DURATION.slow, ease: EASE.signature }}
      drag={floating}
      dragListener={false}
      dragControls={dragControls}
      dragMomentum={false}
      dragConstraints={constraintsRef}
      dragElastic={0.03}
      whileDrag={{ scale: 1.01 }}
      onPointerDownCapture={floating ? focus : undefined}
    >
      <header className="gwin-bar" onPointerDown={floating ? startDrag : undefined}>
        {onRun ? (
          <button className="gwin-run" onClick={onRun} aria-label={`Run ${title}`} title={`Run ${title}`}>
            ▶
          </button>
        ) : (
          <span className="gwin-icon" aria-hidden="true">
            {icon}
          </span>
        )}
        <span className="gwin-title">{title}</span>
        <span className="gwin-spacer" />
        {runningPill}
        {floating
          ? onDock && (
              <button className="gwin-btn" onClick={onDock} aria-label="Dock" title="Dock to side">
                ⤡
              </button>
            )
          : onFloat && (
              <button className="gwin-btn" onClick={onFloat} aria-label="Float" title="Pop out to a floating window">
                ⤢
              </button>
            )}
        <button className="gwin-btn" onClick={() => setMinimized((m) => !m)} aria-label={minimized ? "Restore" : "Minimize"}>
          {minimized ? "▢" : "—"}
        </button>
        {closable ? (
          <button className="gwin-btn" onClick={onClose} aria-label="Close">
            ×
          </button>
        ) : null}
      </header>
      {/* Collapse via CSS (max-height) rather than a Motion height:auto tween —
          the latter mis-measures and sticks at 0 when the layout flips between
          the docked grid and the mobile stack on a live resize. */}
      <div className={`gwin-body${minimized ? " min" : ""}`}>{children}</div>
    </motion.section>
  );
}
