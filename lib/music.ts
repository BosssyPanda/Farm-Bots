// File-based background soundtrack — rotates the supplied tracks Minecraft-style:
// a shuffled queue (no immediate repeat), each track played in full, then a random
// silent gap before the next. Uses a single HTMLAudioElement. Audio only starts on
// a user gesture (browsers block autoplay); setOn(true) is expected from a click.

export interface MusicHandle {
  setOn(on: boolean): void;
  isOn(): boolean;
  dispose(): void;
}

// served from public/music (see public/music/*.mp3)
const TRACKS = [
  "Steps_Through_the_Grove",
  "Rapid_Growth",
  "Dusk_on_the_Porch",
  "Porch_Light_Out",
  "Sun_on_the_Barley",
  "The_Pavement_Shuffle",
  "A_Proof_of_Autumn",
  "Morning_on_the_Unbroken_Soil",
  "Midnight_at_the_Weathered_Barn",
  "Midnight_at_the_Farmhouse",
].map((name) => `/music/${name}.mp3`);

const VOLUME = 0.5;
const GAP_MIN_MS = 30_000; // silent gap between tracks (Minecraft-style)
const GAP_MAX_MS = 90_000;

export function createMusic(): MusicHandle {
  let audio: HTMLAudioElement | null = null;
  let on = false;
  let gapTimer: ReturnType<typeof setTimeout> | null = null;
  let queue: string[] = [];
  let lastTrack: string | null = null;

  const clearGap = () => {
    if (gapTimer) {
      clearTimeout(gapTimer);
      gapTimer = null;
    }
  };

  // Fisher–Yates shuffle, then ensure the first track isn't the one just played.
  const reshuffle = () => {
    queue = [...TRACKS];
    for (let i = queue.length - 1; i > 0; i--) {
      const j = Math.floor(Math.random() * (i + 1));
      [queue[i], queue[j]] = [queue[j], queue[i]];
    }
    if (queue.length > 1 && queue[0] === lastTrack) {
      [queue[0], queue[1]] = [queue[1], queue[0]];
    }
  };

  const nextTrack = (): string => {
    if (queue.length === 0) reshuffle();
    const track = queue.shift() as string;
    lastTrack = track;
    return track;
  };

  const playNext = () => {
    if (!on || !audio) return;
    clearGap();
    audio.src = nextTrack();
    audio.currentTime = 0;
    void audio.play().catch(() => {
      /* autoplay/gesture blocked — wait for the next user toggle */
    });
  };

  const scheduleNext = () => {
    if (!on) return;
    clearGap();
    const gap = GAP_MIN_MS + Math.random() * (GAP_MAX_MS - GAP_MIN_MS);
    gapTimer = setTimeout(playNext, gap);
  };

  const ensure = () => {
    if (audio || typeof Audio === "undefined") return;
    audio = new Audio();
    audio.volume = VOLUME;
    audio.preload = "auto";
    audio.addEventListener("ended", scheduleNext);
  };

  return {
    setOn(next: boolean) {
      if (next === on) return;
      on = next;
      try {
        if (next) {
          ensure();
          playNext(); // start immediately on the user gesture (also covers mid-gap)
        } else {
          clearGap();
          audio?.pause();
        }
        if (typeof localStorage !== "undefined") localStorage.setItem("fwr_music", next ? "on" : "off");
      } catch {
        /* audio unavailable — no-op */
      }
    },
    isOn() {
      return on;
    },
    dispose() {
      on = false;
      clearGap();
      if (audio) {
        audio.removeEventListener("ended", scheduleNext);
        audio.pause();
        audio.src = "";
        audio = null;
      }
    },
  };
}
