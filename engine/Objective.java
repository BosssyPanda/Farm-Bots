/**
 * One curriculum objective. Objectives are data-first: the registry owns the
 * lesson, starter, data arrays, unlock, and concept tag; check logic is kept in
 * one switch so adding a new objective remains a single registry edit.
 */
public class Objective {
    public final String id;
    public final String title;
    public final String concept;
    public final String lesson;
    public final String workedExample;
    public final String[] hints;
    public final String starter;
    public final String unlock;
    public final int farmWidth;
    public final int farmHeight;
    public final String targetCrop;
    public final int targetPrice;
    public final int[] prices;
    public final String[] crops;
    public final int[] moisture;

    public Objective(String id, String title, String concept, String lesson, String workedExample,
                     String[] hints, String starter, String unlock, int farmWidth, int farmHeight,
                     String targetCrop, int targetPrice, int[] prices, String[] crops, int[] moisture) {
        this.id = id;
        this.title = title;
        this.concept = concept;
        this.lesson = lesson;
        this.workedExample = workedExample;
        this.hints = hints;
        this.starter = starter;
        this.unlock = unlock;
        this.farmWidth = farmWidth;
        this.farmHeight = farmHeight;
        this.targetCrop = targetCrop;
        this.targetPrice = targetPrice;
        this.prices = prices;
        this.crops = crops;
        this.moisture = moisture;
    }

    public void prepare(Farm farm) {
        farm.setData(prices, crops, moisture);
        if ("first-sprout".equals(id)) {
            for (int x = 1; x <= 3 && x < farm.width(); x++) {
                farm.setTileInternal(x, 0, Crop.NONE, -1, 0);
            }
        }
        if ("the-long-rows".equals(id)) {
            for (int x = 0; x < farm.width(); x++) {
                farm.setTileInternal(x, 0, Crop.NONE, -1, 0);
            }
        }
        if ("harvest-til-done".equals(id) && farm.resourceCount(Crop.WHEAT) == 0) {
            for (int x = 0; x < 4 && x < farm.width(); x++) {
                if (farm.cropAt(x, 1) == Crop.NONE) {
                    farm.setTileInternal(x, 1, Crop.WHEAT, farm.tick() - Crop.WHEAT.growTicks(), 0);
                }
            }
        }
    }

    public ObjectiveResult evaluate(Farm farm, Drone drone) {
        ObjectiveResult result = new ObjectiveResult();
        if ("first-sprout".equals(id)) {
            result.add("three-method-actions", "Call the drone methods to plant three target tiles", planted(farm, 1, 0) && planted(farm, 2, 0) && planted(farm, 3, 0));
            result.add("method-sequence", "Use at least three moves and three plant actions", drone.moveCount() >= 3 && drone.plantCount() >= 3);
        } else if ("the-long-rows".equals(id)) {
            int planted = 0;
            for (int x = 0; x < farm.width(); x++) if (planted(farm, x, 0)) planted++;
            result.add("row-planted", "Plant every tile in row 0 (" + planted + "/" + farm.width() + ")", planted == farm.width());
            result.add("loop-counter-watch", "Watch a loop counter named tiles with the row width", drone.watchInt("tiles", -1) >= farm.width());
        } else if ("stock-the-stall".equals(id)) {
            result.add("array-data-read", "Read both farm.prices() and farm.crops()", farm.pricesReadCount() > 0 && farm.cropsReadCount() > 0);
            result.add("array-total", "Use the prices array to watch total = 55", drone.watchInt("total", -1) == 55);
            result.add("array-min-max", "Watch minPrice = 4 and maxPrice = 13", drone.watchInt("minPrice", -1) == 4 && drone.watchInt("maxPrice", -1) == 13);
            result.add("parallel-count", "Watch count = 6 from the parallel crop array", drone.watchInt("count", -1) == 6);
        } else if ("harvest-til-done".equals(id)) {
            result.add("while-harvest", "Harvest four ripe wheat tiles", farm.resourceCount(Crop.WHEAT) >= 4);
            result.add("digit-decoder", "Decode irrigation code 314 and watch digitSum = 8", farm.moistureReadCount() > 0 && drone.watchInt("digitSum", -1) == 8);
        } else if ("find-the-crop".equals(id)) {
            result.add("sequential-probe", "Read crops and probe indexes 0, 1, then 2", farm.cropsReadCount() > 0 && drone.watchHistoryIntContainsSequence("i", new int[] { 0, 1, 2 }));
            result.add("sequential-index", "Sequentially find PUMPKIN at index 2", drone.watchInt("foundIndex", -1) == 2);
            result.add("comparison-count", "Watch exactly 3 comparisons for the left-to-right search", drone.watchInt("comparisons", 999) == 3);
        } else if ("fast-market".equals(id)) {
            result.add("binary-probe", "Read prices and watch mid indexes 3, 5, then 4", farm.pricesReadCount() > 0 && drone.watchHistoryIntContainsSequence("mid", new int[] { 3, 5, 4 }));
            result.add("binary-index", "Binary search sorted prices to find 21 at index 4", drone.watchInt("foundIndex", -1) == 4);
            result.add("binary-budget", "Use low/high/mid with exactly 3 comparisons", drone.watchInt("comparisons", 999) == 3);
        } else if ("tidy-the-stalls".equals(id)) {
            result.add("bubble-comparisons", "Read prices and compare adjacent stalls through the bubble passes", farm.pricesReadCount() > 0 && drone.watchCount("i") >= 10);
            result.add("bubble-sorted", "Bubble sort prices ascending and watch sortedPrices", "3,5,7,12,19".equals(drone.watchText("sortedPrices")));
            result.add("bubble-swaps", "Watch at least one swap", drone.watchInt("swaps", 0) > 0);
        } else if ("pick-the-best".equals(id)) {
            result.add("selection-comparisons", "Read prices/crops and scan candidates for each ranked slot", farm.pricesReadCount() > 0 && farm.cropsReadCount() > 0 && drone.watchCount("i") >= 10);
            result.add("selection-sorted", "Selection sort prices descending and watch rankedPrices", "30,22,14,9,6".equals(drone.watchText("rankedPrices")));
            result.add("best-crop", "Watch bestCrop = CORN", "CORN".equals(drone.watchText("bestCrop")));
        } else if ("mastery-garden".equals(id)) {
            result.add("recursive-sum", "Use recursion to watch recursiveSum = 55", drone.watchInt("recursiveSum", -1) == 55);
        }
        return result;
    }

    private boolean planted(Farm farm, int x, int y) {
        return farm.isInside(x, y) && farm.cropAt(x, y) != Crop.NONE;
    }

    public String metadataJson() {
        return "{"
            + "\"id\":" + Json.str(id)
            + ",\"title\":" + Json.str(title)
            + ",\"concept\":" + Json.str(concept)
            + ",\"lesson\":" + Json.str(lesson)
            + ",\"workedExample\":" + Json.str(workedExample)
            + ",\"hints\":" + Json.stringArray(hints)
            + ",\"starter\":" + Json.str(starter)
            + ",\"unlock\":" + Json.str(unlock)
            + ",\"farmWidth\":" + farmWidth
            + ",\"farmHeight\":" + farmHeight
            + ",\"targetCrop\":" + Json.str(targetCrop)
            + ",\"targetPrice\":" + targetPrice
            + ",\"prices\":" + Json.intArray(prices)
            + ",\"crops\":" + Json.stringArray(crops)
            + ",\"moisture\":" + Json.intArray(moisture)
            + "}";
    }

    public static class ObjectiveResult {
        private final StringBuilder checks = new StringBuilder("[");
        private boolean first = true;
        public boolean passed = true;

        public void add(String id, String label, boolean ok) {
            if (!first) checks.append(",");
            first = false;
            checks.append("{\"id\":").append(Json.str(id))
                .append(",\"label\":").append(Json.str(label))
                .append(",\"passed\":").append(ok)
                .append("}");
            if (!ok) passed = false;
        }

        public String checksJson() {
            return checks.toString() + "]";
        }
    }
}
