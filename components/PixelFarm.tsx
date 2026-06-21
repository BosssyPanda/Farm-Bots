"use client";

import { useEffect, useRef } from "react";
import type { DerivedState } from "@/lib/animate";
import { BH, COZY_CARROTS, CROP_GROW_TICKS, CROP_TINT, DRONE_ACCENT, ISO, TH, TW } from "@/lib/farmPalette";
import { useReducedMotion } from "@/lib/motion";
import type { FarmState } from "@/lib/types";

// 2.5D isometric farm — the world the floating code windows sit over, modelled
// on *The Farmer Was Replaced*: raised soil-block tiles under a sky, low-poly
// crops, and a drone that flies above with a fading trail. Drawn directly to a
// dpr-scaled canvas via an art-space transform (no 3D engine).
//
// Stays a pure function of (farmState, state): the rAF loop only adds visual
// smoothing (drone glide, bob, rotor flicker, trail, plant/harvest sparkles),
// all disabled under prefers-reduced-motion.

interface Sparkle {
  ax: number;
  ay: number;
  kind: "plant" | "harvest";
  born: number;
}

const lerp = (a: number, b: number, t: number) => a + (b - a) * t;
const clamp = (v: number, a: number, b: number) => (v < a ? a : v > b ? b : v);

const LIFT = 26; // how high the drone floats above its tile (art units)

export default function PixelFarm({
  width,
  height,
  state,
  farmState,
}: {
  width: number;
  height: number;
  state: DerivedState;
  farmState: FarmState;
  running?: boolean;
}) {
  const canvasRef = useRef<HTMLCanvasElement | null>(null);
  const reduced = useReducedMotion();

  const dataRef = useRef({ width, height, state, farmState, reduced });
  useEffect(() => {
    dataRef.current = { width, height, state, farmState, reduced };
  }, [width, height, state, farmState, reduced]);

  const droneRef = useRef<{ x: number; y: number } | null>(null);
  const trailRef = useRef<Array<{ x: number; y: number }>>([]);
  const sparklesRef = useRef<Sparkle[]>([]);
  const lastActionRef = useRef<string>("");

  useEffect(() => {
    const canvas = canvasRef.current;
    if (!canvas) return;
    const ctx = canvas.getContext("2d");
    if (!ctx) return; // jsdom / no 2d context

    const pal = COZY_CARROTS;
    let raf = 0;
    let dpr = 1;
    let fit = { scale: 4, offX: 0, offY: 0 };

    // isometric projection (art units) for a tile's top-face centre
    const isoX = (c: number, r: number) => (c - r) * (TW / 2);
    const isoY = (c: number, r: number) => (c + r) * (TH / 2);

    const resize = () => {
      const rect = canvas.getBoundingClientRect();
      dpr = clamp(window.devicePixelRatio || 1, 1, 2);
      canvas.width = Math.max(1, Math.round(rect.width * dpr));
      canvas.height = Math.max(1, Math.round(rect.height * dpr));
      const { width: cols, height: rows } = dataRef.current;
      // art bounds of the whole field (incl. block height + drone lift headroom)
      const artW = (cols + rows) * (TW / 2);
      const artH = (cols + rows) * (TH / 2) + BH + LIFT + 16;
      const scale = Math.min(canvas.width / (artW + 24), canvas.height / (artH + 24));
      // centre on the midpoint tile of the field
      const midX = isoX((cols - 1) / 2, (rows - 1) / 2);
      const midY = isoY((cols - 1) / 2, (rows - 1) / 2);
      fit = {
        scale,
        offX: canvas.width / 2 - midX * scale,
        offY: canvas.height / 2 - midY * scale - (LIFT / 3) * scale,
      };
    };

    const ro = new ResizeObserver(resize);
    ro.observe(canvas);
    resize();

    // --- polygon helpers (art space; transform applied by caller) ---
    const diamond = (cx: number, cy: number, color: string) => {
      ctx.beginPath();
      ctx.moveTo(cx, cy - TH / 2);
      ctx.lineTo(cx + TW / 2, cy);
      ctx.lineTo(cx, cy + TH / 2);
      ctx.lineTo(cx - TW / 2, cy);
      ctx.closePath();
      ctx.fillStyle = color;
      ctx.fill();
    };

    const block = (cx: number, cy: number, top: string, left: string, right: string) => {
      // left face
      ctx.beginPath();
      ctx.moveTo(cx - TW / 2, cy);
      ctx.lineTo(cx, cy + TH / 2);
      ctx.lineTo(cx, cy + TH / 2 + BH);
      ctx.lineTo(cx - TW / 2, cy + BH);
      ctx.closePath();
      ctx.fillStyle = left;
      ctx.fill();
      // right face
      ctx.beginPath();
      ctx.moveTo(cx + TW / 2, cy);
      ctx.lineTo(cx, cy + TH / 2);
      ctx.lineTo(cx, cy + TH / 2 + BH);
      ctx.lineTo(cx + TW / 2, cy + BH);
      ctx.closePath();
      ctx.fillStyle = right;
      ctx.fill();
      // top
      diamond(cx, cy, top);
      // crisp edges
      ctx.strokeStyle = ISO.edge;
      ctx.lineWidth = 1;
      ctx.stroke();
    };

    const tintFor = (crop: string) => CROP_TINT[crop] ?? { body: pal.crop, hi: pal.cropHi };

    // crops drawn standing on the tile top-centre (cx,cy), height by grow gp
    const drawCrop = (cx: number, cy: number, crop: string, gp: number) => {
      const t = tintFor(crop);
      if (crop === "PUMPKIN") {
        const r = 3 + gp * 6;
        ctx.fillStyle = pal.leafDark;
        ctx.fillRect(cx - 1, cy - r - 3, 2, 3);
        ctx.fillStyle = t.body;
        ctx.beginPath();
        ctx.ellipse(cx, cy - r * 0.5, r, r * 0.8, 0, 0, Math.PI * 2);
        ctx.fill();
        if (gp > 0.6) {
          ctx.fillStyle = t.hi;
          ctx.fillRect(cx - 1, cy - r, 1, r);
        }
        return;
      }
      if (crop === "WHEAT" || crop === "CORN") {
        const h = 6 + gp * 18;
        const stalkColor = crop === "CORN" ? pal.leafDark : pal.leaf;
        for (const dx of [-4, 0, 4]) {
          ctx.strokeStyle = stalkColor;
          ctx.lineWidth = 1.5;
          ctx.beginPath();
          ctx.moveTo(cx + dx, cy);
          ctx.lineTo(cx + dx, cy - h);
          ctx.stroke();
          if (gp > 0.45) {
            ctx.fillStyle = t.body;
            ctx.fillRect(cx + dx - 1.5, cy - h, 3, gp * 7);
            ctx.fillStyle = t.hi;
            ctx.fillRect(cx + dx - 0.5, cy - h, 1, 2);
          }
        }
        return;
      }
      // CARROT / default: leafy fronds + orange tip
      const h = 5 + gp * 14;
      ctx.strokeStyle = pal.leaf;
      ctx.lineWidth = 1.5;
      for (const dx of [-3, 0, 3]) {
        ctx.beginPath();
        ctx.moveTo(cx, cy);
        ctx.lineTo(cx + dx, cy - h);
        ctx.stroke();
      }
      if (gp > 0.7) {
        ctx.fillStyle = t.body;
        ctx.beginPath();
        ctx.moveTo(cx - 2, cy);
        ctx.lineTo(cx + 2, cy);
        ctx.lineTo(cx, cy + 4);
        ctx.closePath();
        ctx.fill();
      }
    };

    const drone = (cx: number, cy: number, t: number, animate: boolean) => {
      // shadow on the ground tile
      ctx.fillStyle = ISO.shadow;
      ctx.beginPath();
      ctx.ellipse(cx, cy, 9, 4.5, 0, 0, Math.PI * 2);
      ctx.fill();
      const bob = animate ? Math.sin(t / 280) * 3 : 0;
      const dy = cy - LIFT + bob;
      // rotor
      ctx.strokeStyle = "rgba(210,224,232,0.8)";
      ctx.lineWidth = 1.5;
      const span = !animate || Math.floor(t / 45) % 2 ? 12 : 9;
      ctx.beginPath();
      ctx.moveTo(cx - span, dy - 7);
      ctx.lineTo(cx + span, dy - 7);
      ctx.stroke();
      ctx.fillStyle = pal.metal;
      ctx.fillRect(cx - 1, dy - 8, 2, 3);
      // body
      ctx.fillStyle = pal.droneDark;
      ctx.beginPath();
      ctx.ellipse(cx, dy, 8, 5.5, 0, 0, Math.PI * 2);
      ctx.fill();
      ctx.fillStyle = DRONE_ACCENT;
      ctx.beginPath();
      ctx.ellipse(cx, dy + 0.5, 6, 3.5, 0, 0, Math.PI * 2);
      ctx.fill();
      // glass dome
      ctx.fillStyle = pal.glass;
      ctx.beginPath();
      ctx.ellipse(cx - 1.5, dy - 1.5, 2.5, 2, 0, 0, Math.PI * 2);
      ctx.fill();
      // belly light
      ctx.fillStyle = !animate || Math.floor(t / 250) % 2 ? "#ffe27a" : "#9a6a20";
      ctx.fillRect(cx + 2, dy + 3, 2, 1.5);
    };

    const draw = (now: number) => {
      const { width: cols, height: rows, state: s, reduced: rm } = dataRef.current;
      const animate = !rm;

      // sky (device space)
      ctx.setTransform(1, 0, 0, 1, 0, 0);
      const sky = ctx.createLinearGradient(0, 0, 0, canvas.height);
      sky.addColorStop(0, ISO.skyTop);
      sky.addColorStop(1, ISO.skyBottom);
      ctx.fillStyle = sky;
      ctx.fillRect(0, 0, canvas.width, canvas.height);

      // art space
      ctx.setTransform(fit.scale, 0, 0, fit.scale, fit.offX, fit.offY);

      // tiles + crops, back-to-front (increasing c+r)
      for (let sum = 0; sum <= cols + rows - 2; sum++) {
        for (let c = 0; c <= sum; c++) {
          const r = sum - c;
          if (c >= cols || r >= rows) continue;
          const cx = isoX(c, r);
          const cy = isoY(c, r);
          const tile = s.tileStates.get(`${c},${r}`);
          if (tile) {
            block(cx, cy, pal.soil, ISO.soilLeft, ISO.soilRight);
            const grow = CROP_GROW_TICKS[tile.crop] ?? 5;
            const gp = tile.ripe ? 1 : clamp((s.tick - tile.plantedTick) / grow, 0.12, 1);
            drawCrop(cx, cy, tile.crop, gp);
          } else {
            const topAlt = (c + r) % 2 === 0 ? pal.grass : pal.grassAlt;
            block(cx, cy, topAlt, ISO.grassLeft, ISO.grassRight);
          }
        }
      }

      // drone glide
      const target = { x: s.drone.x, y: s.drone.y };
      if (!droneRef.current) droneRef.current = { ...target };
      const d = droneRef.current;
      const k = animate ? 0.2 : 1;
      d.x = lerp(d.x, target.x, k);
      d.y = lerp(d.y, target.y, k);
      const dcx = isoX(d.x, d.y);
      const dcy = isoY(d.x, d.y);

      // trail (fading coins behind the drone)
      if (animate) {
        const last = trailRef.current[trailRef.current.length - 1];
        if (!last || Math.hypot(last.x - dcx, last.y - dcy) > 3) {
          trailRef.current.push({ x: dcx, y: dcy });
          if (trailRef.current.length > 16) trailRef.current.shift();
        }
        trailRef.current.forEach((p, i) => {
          const a = (i / trailRef.current.length) * 0.5;
          ctx.fillStyle = `rgba(255,201,90,${a})`;
          ctx.beginPath();
          ctx.ellipse(p.x, p.y - LIFT * 0.5, 2.2, 1.6, 0, 0, Math.PI * 2);
          ctx.fill();
        });
      } else {
        trailRef.current = [];
      }

      // sparkles
      if (animate) {
        sparklesRef.current = sparklesRef.current.filter((sp) => now - sp.born < 460);
        for (const sp of sparklesRef.current) {
          const p = (now - sp.born) / 460;
          ctx.fillStyle = sp.kind === "harvest" ? `rgba(255,176,102,${1 - p})` : `rgba(140,194,87,${1 - p})`;
          const rad = 2 + p * 7;
          for (let k2 = 0; k2 < 4; k2++) {
            const ang = (k2 / 4) * Math.PI * 2;
            ctx.fillRect(sp.ax + Math.cos(ang) * rad, sp.ay - 4 + Math.sin(ang) * rad * 0.6, 1.6, 1.6);
          }
        }
      } else {
        sparklesRef.current = [];
      }

      drone(dcx, dcy, now, animate);

      raf = requestAnimationFrame(draw);
    };

    raf = requestAnimationFrame(draw);
    return () => {
      cancelAnimationFrame(raf);
      ro.disconnect();
    };
  }, []);

  // spawn a sparkle (iso) when a new plant/harvest action appears in playback
  useEffect(() => {
    const a = state.lastAction;
    if (!a) return;
    const id = `${a.type}:${"at" in a ? a.at.join(",") : "to" in a ? a.to.join(",") : ""}:${state.tick}`;
    if (id === lastActionRef.current) return;
    lastActionRef.current = id;
    if ((a.type === "plant" || a.type === "harvest") && !reduced) {
      const ax = (a.at[0] - a.at[1]) * (TW / 2);
      const ay = (a.at[0] + a.at[1]) * (TH / 2);
      sparklesRef.current.push({ ax, ay, kind: a.type, born: performance.now() });
    }
  }, [state.lastAction, state.tick, reduced]);

  return <canvas ref={canvasRef} className="farm-canvas" role="img" aria-label="isometric farm" />;
}
