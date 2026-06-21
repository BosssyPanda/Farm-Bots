"use client";

import { motion } from "motion/react";
import { pressable } from "@/lib/motion";

// Top-right corner controls, echoing the real game's +/i/collapse cluster.
// Here they map to the global game actions.
export default function Toolbar({
  onRun,
  onResetFarm,
  onToggleInfo,
  onToggleConcepts,
  conceptsOn,
  onToggleDrills,
  drillsOn,
  onToggleMusic,
  musicOn,
  running,
}: {
  onRun: () => void;
  onResetFarm: () => void;
  onToggleInfo: () => void;
  onToggleConcepts: () => void;
  conceptsOn: boolean;
  onToggleDrills: () => void;
  drillsOn: boolean;
  onToggleMusic: () => void;
  musicOn: boolean;
  running: boolean;
}) {
  return (
    <div className="toolbar">
      <motion.button className="tool-btn run" onClick={onRun} disabled={running} {...pressable} aria-label="Run strategy" title="Run">
        {running ? "···" : "▶"}
      </motion.button>
      <motion.button className="tool-btn" onClick={onResetFarm} disabled={running} {...pressable} aria-label="Reset farm" title="Reset farm">
        ⟲
      </motion.button>
      <motion.button
        className={`tool-btn${conceptsOn ? " on" : ""}`}
        onClick={onToggleConcepts}
        {...pressable}
        aria-label="Toggle concepts roadmap"
        aria-pressed={conceptsOn}
        title="Concepts roadmap"
      >
        ▦
      </motion.button>
      <motion.button
        className={`tool-btn${drillsOn ? " on" : ""}`}
        onClick={onToggleDrills}
        {...pressable}
        aria-label="Toggle skill drills"
        aria-pressed={drillsOn}
        title="Skill drills"
      >
        ⚡
      </motion.button>
      <motion.button
        className={`tool-btn${musicOn ? " on" : ""}`}
        onClick={onToggleMusic}
        {...pressable}
        aria-label={musicOn ? "Mute music" : "Play music"}
        aria-pressed={musicOn}
        title={musicOn ? "Mute music" : "Play music"}
      >
        {musicOn ? "♪" : "🔇"}
      </motion.button>
      <motion.button className="tool-btn" onClick={onToggleInfo} {...pressable} aria-label="Toggle lesson panel" title="Lesson">
        i
      </motion.button>
    </div>
  );
}
