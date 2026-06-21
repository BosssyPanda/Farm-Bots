"use client";

import { useCallback, useEffect, useRef, useState } from "react";
import dynamic from "next/dynamic";
import { motion } from "motion/react";
import PixelFarm from "@/components/PixelFarm";
import type { DerivedState } from "@/lib/animate";
import { hudTick } from "@/lib/motion";
import type { FarmState } from "@/lib/types";

// 3D island scene is client-only + heavy, so load it lazily and never on the server.
const IslandFarm = dynamic(() => import("@/components/IslandFarm"), { ssr: false });

function phaseLabel(state: DerivedState): string {
  const a = state.lastAction;
  if (!a) return "ready";
  if (a.type === "plant") return "planting";
  if (a.type === "harvest") return "harvesting";
  if (a.type === "inspect") return "reading";
  return "working";
}

function canRender3D(): boolean {
  if (typeof window === "undefined") return false;
  try {
    const c = document.createElement("canvas");
    return !!(c.getContext("webgl2") || c.getContext("webgl"));
  } catch {
    return false;
  }
}

// Full-bleed farm: a fixed background layer the floating windows sit on top of.
// Renders the 3D floating-island scene whenever WebGL is available, falling back
// to the crisp 2D canvas only when the browser cannot create a WebGL renderer.
// Starts on the 2D path so server and first client render match, then upgrades
// after mount.
export default function FarmView({
  width,
  height,
  farmState,
  state,
  running = false,
}: {
  width: number;
  height: number;
  farmState: FarmState;
  state: DerivedState;
  running?: boolean;
}) {
  const [use3D, setUse3D] = useState(false);
  const [failed, setFailed] = useState(false);
  const backdropRef = useRef<HTMLDivElement | null>(null);

  useEffect(() => {
    const frame = window.requestAnimationFrame(() => setUse3D(canRender3D()));
    return () => window.cancelAnimationFrame(frame);
  }, []);

  // Ready/error handshake: the island calls onReady once it has rendered a frame,
  // and onError only when WebGL itself cannot start. Slow chunk startup should not
  // permanently downgrade the public build to the simpler canvas view.
  const handleReady = useCallback(() => undefined, []);
  const handleError = useCallback(() => setFailed(true), []);

  const harvested = Object.values(state.resources).reduce((sum, n) => sum + (Number(n) || 0), 0);
  const label = phaseLabel(state);

  return (
    <div className="farm-backdrop" ref={backdropRef}>
      {use3D && !failed ? (
        <IslandFarm
          state={state}
          playWidth={width}
          playHeight={height}
          running={running}
          onReady={handleReady}
          onError={handleError}
        />
      ) : (
        <PixelFarm width={width} height={height} state={state} farmState={farmState} running={running} />
      )}

      <div className="hud left">
        {farmState.width} &times; {farmState.height} plots
      </div>

      <div className="hud right">
        <motion.div className="hud-label" key={label} variants={hudTick} initial="hidden" animate="show">
          {running ? label : "ready"}
        </motion.div>
        <div className="hud-count">
          <motion.span key={harvested} variants={hudTick} initial="hidden" animate="show" style={{ display: "inline-block" }}>
            {harvested}
          </motion.span>{" "}
          <span className="unit">crops</span>
        </div>
      </div>

      <div className="farm-vignette" />
    </div>
  );
}
