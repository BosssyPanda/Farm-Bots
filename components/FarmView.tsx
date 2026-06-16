"use client";

import type { DerivedState } from "@/lib/animate";
import type { FarmState } from "@/lib/types";

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
  farmState,
  state,
}: {
  width: number;
  height: number;
  farmState: FarmState;
  state: DerivedState;
}) {
  const w = PAD * 2 + width * TILE + (width - 1) * GAP;
  const h = PAD * 2 + height * TILE + (height - 1) * GAP;
  const px = (x: number) => PAD + x * (TILE + GAP);
  const py = (y: number) => PAD + y * (TILE + GAP);
  const centerX = (x: number) => px(x) + TILE / 2;
  const centerY = (y: number) => py(y) + TILE / 2;

  const tiles: Array<[number, number]> = [];
  for (let y = 0; y < height; y++) {
    for (let x = 0; x < width; x++) {
      tiles.push([x, y]);
    }
  }

  const pathPoints = state.path.map(([x, y]) => `${centerX(x)},${centerY(y)}`).join(" ");

  return (
    <div className="panel farm">
      <div className="panel-head">
        <h2>Farm</h2>
        <span className="muted small">
          tick {state.tick} · {farmState.width}x{farmState.height}
        </span>
      </div>
      <svg className="farm-svg" viewBox={`0 0 ${w} ${h}`} role="img" aria-label="farm grid">
        <rect className="farm-bed" x={PAD / 2} y={PAD / 2} width={w - PAD} height={h - PAD} rx={14} />
        {pathPoints && state.path.length > 1 && <polyline className="drone-path" points={pathPoints} />}
        {tiles.map(([x, y]) => {
          const key = `${x},${y}`;
          const tileState = state.tileStates.get(key);
          const crop = tileState?.crop ?? state.planted.get(key);
          const tile = farmState.tiles.find((entry) => entry.x === x && entry.y === y);
          const hasCrop = Boolean(crop && crop !== "NONE");
          const ripe = tileState?.ripe ?? tile?.ripe ?? false;
          const cropName = crop && crop !== "NONE" ? crop : "NONE";
          const lastAction = state.lastAction;
          const actionAtTile =
            lastAction?.type === "plant" || lastAction?.type === "harvest"
              ? lastAction.at[0] === x && lastAction.at[1] === y
              : false;
          const stage = tileState?.stage ?? (ripe ? "ripe" : "growing");

          return (
            <g key={`${x},${y}`}>
              <rect
                className={`tile${ripe ? " ripe" : ""}${actionAtTile ? " active" : ""}`}
                x={px(x)}
                y={py(y)}
                width={TILE}
                height={TILE}
                rx={8}
              />
              <text className="tile-coord" x={px(x) + 7} y={py(y) + 14}>
                {x},{y}
              </text>
              {hasCrop && (
                <g className={`crop-group ${stage}${ripe ? " ripe" : ""}`}>
                  <path
                    className="crop-stem"
                    d={`M${centerX(x)} ${py(y) + TILE * 0.74} C${centerX(x) - 5} ${py(y) + TILE * 0.52}, ${centerX(x) + 5} ${py(y) + TILE * 0.42}, ${centerX(x)} ${py(y) + TILE * 0.28}`}
                  />
                  <ellipse
                    className="crop-leaf"
                    cx={centerX(x) - TILE * 0.13}
                    cy={py(y) + TILE * 0.5}
                    rx={stage === "planted" ? TILE * 0.08 : TILE * 0.14}
                    ry={stage === "planted" ? TILE * 0.04 : TILE * 0.08}
                  />
                  <ellipse
                    className="crop-leaf right"
                    cx={centerX(x) + TILE * 0.13}
                    cy={py(y) + TILE * 0.46}
                    rx={stage === "planted" ? TILE * 0.08 : TILE * 0.14}
                    ry={stage === "planted" ? TILE * 0.04 : TILE * 0.08}
                  />
                  <circle className="crop" cx={centerX(x)} cy={py(y) + TILE * 0.3} r={stage === "planted" ? TILE * 0.08 : TILE * 0.2} fill={CROP_COLOR[cropName] ?? "#8fd14f"} />
                  {ripe && <circle className="crop-glow" cx={centerX(x)} cy={py(y) + TILE * 0.3} r={TILE * 0.28} />}
                </g>
              )}
              {actionAtTile && (
                <circle
                  className={`action-ring ${lastAction?.type ?? ""}`}
                  cx={centerX(x)}
                  cy={centerY(y)}
                  r={TILE * 0.42}
                />
              )}
            </g>
          );
        })}
        <g
          className="drone"
          style={{ transform: `translate(${px(state.drone.x)}px, ${py(state.drone.y)}px)` }}
        >
          <rect className="drone-body" x={TILE * 0.18} y={TILE * 0.18} width={TILE * 0.64} height={TILE * 0.64} rx={12} />
          <path className="drone-wing" d={`M${TILE * 0.18} ${TILE * 0.42}h-${TILE * 0.1}v${TILE * 0.16}h${TILE * 0.1}`} />
          <path className="drone-wing" d={`M${TILE * 0.82} ${TILE * 0.42}h${TILE * 0.1}v${TILE * 0.16}h-${TILE * 0.1}`} />
          <circle className="drone-eye" cx={TILE * 0.5} cy={TILE * 0.5} r={TILE * 0.12} />
        </g>
      </svg>
    </div>
  );
}
