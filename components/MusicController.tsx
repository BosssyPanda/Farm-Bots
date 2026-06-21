"use client";

import { useEffect, useRef } from "react";
import { createMusic, type MusicHandle } from "@/lib/music";

// Owns the Web Audio music handle and drives it from the `on` prop. Renders
// nothing. The toggle that flips `on` is a user click, satisfying the browser
// autoplay gesture requirement.
export default function MusicController({ on }: { on: boolean }) {
  const handle = useRef<MusicHandle | null>(null);

  useEffect(() => {
    handle.current = createMusic();
    return () => {
      handle.current?.dispose();
      handle.current = null;
    };
  }, []);

  useEffect(() => {
    handle.current?.setOn(on);
  }, [on]);

  return null;
}
