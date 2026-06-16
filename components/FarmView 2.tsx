"use client";

import type { DerivedState } from "@/lib/animate";

const TILE = 64;
const GAP = 6;
const PAD = 16;

const CROP_COLOR: Record<string, string> = {
  WHEAT: "#e3b341",
  CORN: "#f2d35a",
  PUMPKIN: "#e8804f",
  CARROT: "#e8743b",
};

export default function FarmView({
  width,
  height,
  state,
}: {
  width: number;
  height: number;
  state: DerivedState;
}) {
  const w = PAD * 2 + width * TILE + (width - 1) * GAP;
  const h = PAD * 2 + height * TILE + (height - 1) * GAP;
  const px = (x: number) => PAD + x * (TILE + GAP);
  const py = (y: number) => PAD + y * (TILE + GAP);

  const tiles: Array<[number, number]> = [];
  for (let y = 0; y < height; y++) {
    for (let x = 0; x < width; x++) {
      tiles.push([x, y]);
    }
  }

  return (
    <div className="panel farm">
      <div className="panel-head">
        <h2>Farm</h2>
        <span className="muted small">tick {state.tick}</span>
      </div>
      <svg className="farm-svg" viewBox={`0 0 ${w} ${h}`} role="img" aria-label="farm grid">
        {tiles.map(([x, y]) => {
          const crop = state.planted.get(`${x},${y}`);
          const hasCrop = crop && crop !== "NONE";
          return (
            <g key={`${x},${y}`}>
              <rect className="tile" x={px(x)} y={py(y)} width={TILE} height={TILE} rx={10} />
              {hasCrop && (
                <circle
                  className="crop"
                  cx={px(x) + TILE / 2}
                  cy={py(y) + TILE / 2}
                  r={TILE * 0.22}
                  fill={CROP_COLOR[crop] ?? "#8fd14f"}
                />
              )}
            </g>
          );
        })}
        <g
          className="drone"
          style={{ transform: `translate(${px(state.drone.x)}px, ${py(state.drone.y)}px)` }}
        >
          <rect
            className="drone-body"
            x={TILE * 0.18}
            y={TILE * 0.18}
            width={TILE * 0.64}
            height={TILE * 0.64}
            rx={12}
          />
          <circle className="drone-eye" cx={TILE * 0.5} cy={TILE * 0.5} r={TILE * 0.12} />
        </g>
      </svg>
    </div>
  );
}
