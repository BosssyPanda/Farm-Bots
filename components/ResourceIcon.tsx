// Tiny crisp pixel glyphs per resource kind, echoing the real game's item bar.
// Pure SVG (16x16, crispEdges) so they stay sharp at any DPR.

const C = {
  gold: "#e0a82e",
  goldHi: "#f7d96a",
  wood: "#8a5630",
  woodHi: "#a36c3e",
  orange: "#e8743b",
  pumpkin: "#e8804f",
  green: "#46a233",
  greenHi: "#79c655",
  yellow: "#f2d35a",
  power: "#e6c34a",
  stone: "#9aa0ad",
};

function px(x: number, y: number, w: number, h: number, fill: string) {
  return <rect x={x} y={y} width={w} height={h} fill={fill} />;
}

function glyph(kind: string) {
  switch (kind) {
    case "WHEAT":
    case "HAY":
    case "GRASS":
      return (
        <>
          {px(7, 2, 2, 12, C.green)}
          {px(4, 4, 2, 3, C.gold)}
          {px(10, 4, 2, 3, C.gold)}
          {px(4, 8, 2, 3, C.goldHi)}
          {px(10, 8, 2, 3, C.goldHi)}
          {px(6, 6, 4, 6, C.gold)}
        </>
      );
    case "WOOD":
    case "TREES":
      return (
        <>
          {px(2, 5, 12, 3, C.wood)}
          {px(2, 9, 12, 3, C.woodHi)}
          {px(3, 5, 2, 3, C.woodHi)}
          {px(11, 9, 2, 3, C.wood)}
        </>
      );
    case "CARROT":
      return (
        <>
          {px(6, 2, 2, 2, C.green)}
          {px(9, 2, 2, 2, C.greenHi)}
          {px(6, 5, 5, 2, C.orange)}
          {px(7, 7, 3, 3, C.orange)}
          {px(8, 10, 1, 3, C.orange)}
        </>
      );
    case "PUMPKIN":
      return (
        <>
          {px(7, 2, 2, 2, C.green)}
          {px(4, 5, 8, 7, C.pumpkin)}
          {px(3, 7, 1, 3, C.pumpkin)}
          {px(12, 7, 1, 3, C.pumpkin)}
          {px(6, 6, 1, 5, C.goldHi)}
        </>
      );
    case "CORN":
      return (
        <>
          {px(5, 3, 2, 9, C.green)}
          {px(9, 3, 2, 9, C.greenHi)}
          {px(7, 4, 2, 8, C.yellow)}
        </>
      );
    case "SUNFLOWER":
    case "POWER":
    case "ENERGY":
      return kind === "SUNFLOWER" ? (
        <>
          {px(4, 4, 8, 8, C.yellow)}
          {px(6, 6, 4, 4, C.wood)}
          {px(7, 1, 2, 3, C.yellow)}
          {px(7, 12, 2, 3, C.green)}
        </>
      ) : (
        <>
          {px(8, 1, 4, 7, C.power)}
          {px(4, 7, 6, 2, C.power)}
          {px(5, 8, 4, 7, C.goldHi)}
        </>
      );
    case "GOLD":
    case "COIN":
      return (
        <>
          {px(5, 4, 6, 8, C.gold)}
          {px(4, 6, 1, 4, C.gold)}
          {px(11, 6, 1, 4, C.gold)}
          {px(6, 5, 2, 2, C.goldHi)}
        </>
      );
    default:
      return (
        <>
          {px(3, 4, 10, 9, C.wood)}
          {px(3, 4, 10, 2, C.woodHi)}
          {px(7, 6, 2, 7, C.stone)}
        </>
      );
  }
}

export default function ResourceIcon({ kind, size = 16 }: { kind: string; size?: number }) {
  return (
    <svg
      width={size}
      height={size}
      viewBox="0 0 16 16"
      shapeRendering="crispEdges"
      aria-hidden="true"
      style={{ display: "block", imageRendering: "pixelated" }}
    >
      {glyph(kind.toUpperCase())}
    </svg>
  );
}
