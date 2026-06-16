/**
 * Persistent farm world. The browser sends this state back on every run; Runner
 * restores it, runs Strategy, then serializes it again. Player code only sees the
 * read-only sensor methods. Mutating methods are package-private engine calls.
 */
public class Farm {
    private final int width;
    private final int height;
    private final Crop[][] crop;        // [row][col]
    private final int[][] plantedTick;  // tick when planted, or -1
    private final int[][] moistureGrid; // per-tile moisture
    private int tick = 0;
    private final int[] resources = new int[Crop.values().length];

    // Optional per-objective data. Empty when not relevant.
    private int[] prices = new int[0];
    private String[] cropNames = new String[0];
    private int[] moistureData = new int[0];
    private int pricesReadCount = 0;
    private int cropsReadCount = 0;
    private int moistureReadCount = 0;

    public Farm(int width, int height) {
        this.width = width;
        this.height = height;
        crop = new Crop[height][width];
        plantedTick = new int[height][width];
        moistureGrid = new int[height][width];
        for (int r = 0; r < height; r++) {
            for (int c = 0; c < width; c++) {
                crop[r][c] = Crop.NONE;
                plantedTick[r][c] = -1;
                moistureGrid[r][c] = 0;
            }
        }
    }

    // ===== Player read-only API =====

    public int width() { return width; }
    public int height() { return height; }
    public int tick() { return tick; }

    public Tile tileAt(int x, int y) {
        if (!isInside(x, y)) {
            throw new RuntimeException("tileAt out of range: (" + x + ", " + y + ")");
        }
        Crop c = crop[y][x];
        boolean ripe = isRipe(x, y);
        return new Tile(c, ripe, moistureGrid[y][x]);
    }

    public int[] prices() {
        pricesReadCount++;
        int[] copy = new int[prices.length];
        for (int i = 0; i < prices.length; i++) copy[i] = prices[i];
        return copy;
    }

    public String[] crops() {
        cropsReadCount++;
        String[] copy = new String[cropNames.length];
        for (int i = 0; i < cropNames.length; i++) copy[i] = cropNames[i];
        return copy;
    }

    public int[] moisture() {
        moistureReadCount++;
        int[] copy = new int[moistureData.length];
        for (int i = 0; i < moistureData.length; i++) copy[i] = moistureData[i];
        return copy;
    }

    // ===== Engine-internal =====

    public boolean isInside(int x, int y) {
        return x >= 0 && x < width && y >= 0 && y < height;
    }

    public Crop cropAt(int x, int y) { return crop[y][x]; }
    public int plantedTickAt(int x, int y) { return plantedTick[y][x]; }
    public int resourceCount(Crop c) { return resources[c.ordinal()]; }
    public int pricesReadCount() { return pricesReadCount; }
    public int cropsReadCount() { return cropsReadCount; }
    public int moistureReadCount() { return moistureReadCount; }

    public boolean isRipe(int x, int y) {
        Crop c = crop[y][x];
        return c != Crop.NONE && (tick - plantedTick[y][x]) >= c.growTicks();
    }

    public void plantInternal(int x, int y, Crop c) {
        if (c == Crop.NONE) {
            throw new RuntimeException("Illegal plant: Crop.NONE is not a plantable crop.");
        }
        if (crop[y][x] != Crop.NONE) {
            throw new RuntimeException(
                "Illegal plant: tile (" + x + ", " + y + ") already has " + crop[y][x] + ".");
        }
        crop[y][x] = c;
        plantedTick[y][x] = tick;
    }

    public void setTileInternal(int x, int y, Crop c, int plantedAt, int moisture) {
        crop[y][x] = c;
        plantedTick[y][x] = c == Crop.NONE ? -1 : plantedAt;
        moistureGrid[y][x] = moisture;
    }

    /** Harvests the tile if ripe, returning the crop (or NONE). Tallies resources. */
    public Crop harvestInternal(int x, int y) {
        if (!isRipe(x, y)) return Crop.NONE;
        Crop c = crop[y][x];
        crop[y][x] = Crop.NONE;
        plantedTick[y][x] = -1;
        resources[c.ordinal()]++;
        return c;
    }

    public void advanceTick(int n) { tick += n; }
    public void setTick(int tick) { this.tick = tick; }

    public void setData(int[] prices, String[] cropNames, int[] moistureData) {
        this.prices = new int[prices.length];
        for (int i = 0; i < prices.length; i++) this.prices[i] = prices[i];
        this.cropNames = new String[cropNames.length];
        for (int i = 0; i < cropNames.length; i++) this.cropNames[i] = cropNames[i];
        this.moistureData = new int[moistureData.length];
        for (int i = 0; i < moistureData.length; i++) this.moistureData[i] = moistureData[i];
    }

    public void setResourceInternal(Crop crop, int count) {
        resources[crop.ordinal()] = count;
    }

    /** JSON object of non-zero harvested-resource counts, e.g. {"WHEAT":3}. */
    public String resourcesJson() {
        StringBuilder sb = new StringBuilder("{");
        boolean first = true;
        for (int i = 0; i < resources.length; i++) {
            if (resources[i] == 0) continue;
            if (!first) sb.append(",");
            sb.append(Json.str(Crop.values()[i].name())).append(":").append(resources[i]);
            first = false;
        }
        sb.append("}");
        return sb.toString();
    }

    public String tilesJson() {
        StringBuilder sb = new StringBuilder("[");
        boolean first = true;
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (!first) sb.append(",");
                first = false;
                sb.append("{\"x\":").append(x)
                    .append(",\"y\":").append(y)
                    .append(",\"crop\":").append(Json.str(crop[y][x].name()))
                    .append(",\"plantedTick\":").append(plantedTick[y][x])
                    .append(",\"ripe\":").append(isRipe(x, y))
                    .append(",\"moisture\":").append(moistureGrid[y][x])
                    .append("}");
            }
        }
        sb.append("]");
        return sb.toString();
    }

    public String resourcesStateString() {
        StringBuilder sb = new StringBuilder();
        boolean first = true;
        for (int i = 0; i < resources.length; i++) {
            if (!first) sb.append(",");
            first = false;
            sb.append(Crop.values()[i].name()).append(":").append(resources[i]);
        }
        return sb.toString();
    }
}
