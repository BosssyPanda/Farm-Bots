"use client";

import { useRef } from "react";
import gsap from "gsap";
import { useGSAP } from "@gsap/react";
import { useReducedMotion } from "@/lib/motion";

// The ONE choreographed GSAP timeline in the app: an objective-complete burst.
// It owns its own DOM only (banner + confetti pixels) — nothing here is touched
// by Motion, honouring the "no element driven by two systems" rule.

const CONFETTI_COLORS = ["#e0653c", "#e0962e", "#8ec96a", "#ffe9b0", "#f0826a"];
const CONFETTI = Array.from({ length: 28 }, (_, i) => i);

export default function Celebration({
  title,
  subtitle,
}: {
  // Remounted via a `key` by the parent each time a celebration should fire.
  title: string;
  subtitle?: string;
}) {
  const scope = useRef<HTMLDivElement | null>(null);
  const reduced = useReducedMotion();

  useGSAP(
    () => {
      const tl = gsap.timeline();

      if (reduced) {
        // No travel/scale theatrics — just show, hold, fade.
        tl.set(".celebrate-banner", { opacity: 1, scale: 1, y: 0 });
        tl.to(".celebrate-banner", { opacity: 0, duration: 0.3, delay: 1.4 });
        return;
      }

      tl.fromTo(
        ".celebrate-banner",
        { opacity: 0, scale: 0.82, y: 22 },
        { opacity: 1, scale: 1, y: 0, duration: 0.5, ease: "back.out(1.7)" },
      );
      tl.fromTo(
        ".confetti",
        { opacity: 1, x: 0, y: 0, scale: 1, rotation: 0 },
        {
          x: () => gsap.utils.random(-320, 320),
          y: () => gsap.utils.random(-280, 160),
          rotation: () => gsap.utils.random(-200, 200),
          scale: () => gsap.utils.random(0.6, 1.7),
          opacity: 0,
          duration: 1.15,
          ease: "power2.out",
          stagger: 0.012,
        },
        0.08,
      );
      tl.to(".celebrate-banner", { opacity: 0, y: -14, duration: 0.4, delay: 0.9 });
    },
    { scope, dependencies: [] },
  );

  return (
    <div className="celebrate" ref={scope} aria-hidden="true">
      {CONFETTI.map((i) => (
        <span
          key={i}
          className="confetti"
          style={{ background: CONFETTI_COLORS[i % CONFETTI_COLORS.length] }}
        />
      ))}
      <div className="celebrate-banner">
        {title}
        {subtitle ? <small>{subtitle}</small> : null}
      </div>
    </div>
  );
}
