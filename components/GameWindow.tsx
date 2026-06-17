"use client";

import { useState, type PointerEvent, type ReactNode, type RefObject } from "react";
import { motion, useDragControls } from "motion/react";
import { DURATION, EASE } from "@/lib/motion";

// A draggable, focusable, minimizable "IDE window" — the core chrome of the
// game shell, modelled on *The Farmer Was Replaced*: a title bar with an
// optional ▶ run glyph, a status pill, and minimise / close controls, floating
// over the full-bleed farm. Position is set with absolute left/top; dragging is
// driven from the title bar via Motion drag controls (so clicking inside the
// body never starts a drag). Bringing a window forward bumps a shared z-index.

let zCounter = 30;

export default function GameWindow({
  title,
  icon = "▸",
  children,
  constraintsRef,
  initial,
  width = 360,
  runningPill,
  onRun,
  closable = false,
  className = "",
}: {
  title: string;
  icon?: string;
  children: ReactNode;
  constraintsRef: RefObject<HTMLDivElement | null>;
  initial: { x: number; y: number };
  width?: number;
  runningPill?: ReactNode;
  onRun?: () => void;
  closable?: boolean;
  className?: string;
}) {
  // Stable initial z for SSR (a module counter would drift between the
  // long-lived server and a fresh client, causing hydration mismatch). DOM
  // order stacks windows initially; focus bumps z on the client.
  const [z, setZ] = useState(20);
  const [minimized, setMinimized] = useState(false);
  const [closed, setClosed] = useState(false);
  const dragControls = useDragControls();

  if (closed) return null;

  const focus = () => setZ(++zCounter);
  const startDrag = (e: PointerEvent) => {
    focus();
    dragControls.start(e);
  };

  return (
    <motion.section
      className={`gwin ${className}`}
      style={{ zIndex: z, width, left: initial.x, top: initial.y }}
      initial={{ opacity: 0, scale: 0.96 }}
      animate={{ opacity: 1, scale: 1 }}
      transition={{ duration: DURATION.slow, ease: EASE.signature }}
      drag
      dragListener={false}
      dragControls={dragControls}
      dragMomentum={false}
      dragConstraints={constraintsRef}
      dragElastic={0.03}
      whileDrag={{ scale: 1.01 }}
      onPointerDownCapture={focus}
    >
      <header className="gwin-bar" onPointerDown={startDrag}>
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
        <button className="gwin-btn" onClick={() => setMinimized((m) => !m)} aria-label={minimized ? "Restore" : "Minimize"}>
          {minimized ? "▢" : "—"}
        </button>
        {closable ? (
          <button className="gwin-btn" onClick={() => setClosed(true)} aria-label="Close">
            ×
          </button>
        ) : null}
      </header>
      <motion.div
        className="gwin-body"
        initial={false}
        animate={{ height: minimized ? 0 : "auto", opacity: minimized ? 0 : 1 }}
        transition={{ duration: DURATION.standard, ease: EASE.standard }}
        style={{ overflow: "hidden" }}
      >
        {children}
      </motion.div>
    </motion.section>
  );
}
