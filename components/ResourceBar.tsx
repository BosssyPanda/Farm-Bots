"use client";

import { motion } from "motion/react";
import ResourceIcon from "@/components/ResourceIcon";
import { hudTick } from "@/lib/motion";

function fmt(n: number): string {
  if (n >= 1_000_000) return `${(n / 1_000_000).toFixed(n % 1_000_000 ? 1 : 0)}m`;
  if (n >= 1_000) return `${(n / 1_000).toFixed(n % 1_000 ? 1 : 0)}k`;
  return String(n);
}

// Top-left resource strip, like the real game's icon bar: a pixel item glyph
// per harvested resource plus the live tick. Counts pop on change via Motion.
export default function ResourceBar({
  resources,
  tick,
}: {
  resources: Record<string, number>;
  tick: number;
}) {
  const entries = Object.entries(resources).filter(([, v]) => Number(v) > 0);

  return (
    <div className="resbar">
      {entries.length === 0 ? (
        <span className="res-chip muted">
          <ResourceIcon kind="GRASS" />
          <span className="res-val">farm idle</span>
        </span>
      ) : (
        entries.map(([name, value]) => (
          <span className="res-chip" key={name} title={name}>
            <ResourceIcon kind={name} />
            <motion.span className="res-val" key={value} variants={hudTick} initial="hidden" animate="show">
              {fmt(Number(value))}
            </motion.span>
          </span>
        ))
      )}
      <span className="res-chip">
        <i className="res-glyph clock" />
        <motion.span className="res-val" key={tick} variants={hudTick} initial="hidden" animate="show">
          {fmt(tick)}
        </motion.span>
      </span>
    </div>
  );
}
