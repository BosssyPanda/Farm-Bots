// Single-user farm-state persistence via localStorage (no accounts / DB needed).
// Stub for the skeleton; the continuous persistent farm is wired in a later step.

import type { FarmState } from "./types";

const KEY = "mm-farm-state";

export function loadFarmState(): FarmState | null {
  if (typeof window === "undefined") return null;
  try {
    const raw = window.localStorage.getItem(KEY);
    return raw ? (JSON.parse(raw) as FarmState) : null;
  } catch {
    return null;
  }
}

export function saveFarmState(state: FarmState): void {
  if (typeof window === "undefined") return;
  try {
    window.localStorage.setItem(KEY, JSON.stringify(state));
  } catch {
    /* ignore quota / unavailable storage */
  }
}
