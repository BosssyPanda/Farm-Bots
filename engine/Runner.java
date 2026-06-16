import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Engine entrypoint. In run mode it prints one JSON line with frames, objective,
 * progress, unlocked abilities, persistent farmState, ticks, runtimeError, and
 * captured player stdout. Catalog mode prints objective metadata from the same
 * registry used by checks.
 */
public class Runner {
    static final int TICK_BUDGET = 5000;
    static final int STEP_CAP = 1_000_000;

    public static void main(String[] args) {
        try {
            if (hasArg(args, "--catalog")) {
                System.out.println(Objectives.catalogJson());
                return;
            }
            run(args);
        } catch (Throwable t) {
            String msg = t.getMessage() == null ? "" : t.getMessage();
            System.out.println("{\"frames\":[],\"objective\":{\"id\":\"\",\"concept\":\"\",\"checks\":[],\"passed\":false},"
                + "\"concepts\":{},\"unlocked\":[],\"farmState\":" + defaultFarmStateJson() + ",\"ticks\":0,\"tickLimit\":" + TICK_BUDGET
                + ",\"runtimeError\":" + Json.str(t.getClass().getSimpleName() + ": " + msg)
                + ",\"stdout\":\"\"}");
        }
    }

    private static void run(String[] args) throws Exception {
        EngineState state = EngineState.load(argValue(args, "--state"));
        String objectiveOverride = argValue(args, "--objective");
        String interruptedObjectiveId = state.currentObjectiveId;
        boolean recapRun = false;
        if (objectiveOverride != null && objectiveOverride.length() > 0) {
            state.currentObjectiveId = objectiveOverride;
            interruptedObjectiveId = objectiveOverride;
        } else {
            String recapConcept = state.progress.firstRecapDue();
            if (recapConcept.length() > 0) {
                state.currentObjectiveId = Objectives.byConcept(recapConcept).id;
                recapRun = true;
            }
        }

        Objective objective = Objectives.byId(state.currentObjectiveId);
        Farm farm = state.toFarm(objective.farmWidth, objective.farmHeight);
        objective.prepare(farm);
        Drone drone = new Drone(farm, 0, 0, TICK_BUDGET, STEP_CAP);

        String runtimeError = "";
        PrintStream realOut = System.out;
        ByteArrayOutputStream captured = new ByteArrayOutputStream();
        System.setOut(new PrintStream(captured));
        try {
            new Strategy().run(drone, farm);
        } catch (BudgetReachedException e) {
            // Clean stop: continuous programs are expected to reach a budget.
        } catch (Throwable t) {
            String msg = t.getMessage() == null ? "" : t.getMessage();
            runtimeError = t.getClass().getSimpleName() + ": " + msg;
        } finally {
            System.setOut(realOut);
        }
        drone.emitInspectorFrame("final");

        Objective.ObjectiveResult result = objective.evaluate(farm, drone);
        boolean passed = result.passed && runtimeError.length() == 0;
        state.progress.record(objective.concept, passed);

        String newlyUnlocked = "";
        if (passed && !recapRun && objective.unlock.length() > 0 && !state.unlocked.contains(objective.unlock)) {
            state.unlocked.add(objective.unlock);
            newlyUnlocked = objective.unlock;
        }
        if (passed) {
            if (recapRun) {
                state.currentObjectiveId = interruptedObjectiveId;
            } else if (state.progress.status(objective.concept).mastered || "recursion-puzzles".equals(objective.concept)) {
                state.currentObjectiveId = Objectives.nextAfter(objective.id, state.progress).id;
            } else {
                state.currentObjectiveId = objective.id;
            }
        } else if (state.progress.status(objective.concept).recapDue) {
            state.currentObjectiveId = Objectives.byConcept(objective.concept).id;
        }

        StringBuilder json = new StringBuilder();
        json.append("{");
        json.append("\"frames\":[");
        List<String> frames = drone.frames();
        for (int i = 0; i < frames.size(); i++) {
            if (i > 0) json.append(",");
            json.append(frames.get(i));
        }
        json.append("],");
        json.append("\"objective\":{")
            .append("\"id\":").append(Json.str(objective.id))
            .append(",\"concept\":").append(Json.str(objective.concept))
            .append(",\"checks\":").append(result.checksJson())
            .append(",\"passed\":").append(passed)
            .append("},");
        json.append("\"concepts\":").append(state.progress.conceptsJson()).append(",");
        json.append("\"unlocked\":[");
        boolean firstUnlock = true;
        for (String unlock : state.unlocked) {
            if (!firstUnlock) json.append(",");
            firstUnlock = false;
            json.append(Json.str(unlock));
        }
        json.append("],");
        json.append("\"newlyUnlocked\":").append(newlyUnlocked.length() == 0 ? "[]" : "[" + Json.str(newlyUnlocked) + "]").append(",");
        json.append("\"farmState\":").append(state.farmStateJson(farm)).append(",");
        json.append("\"ticks\":").append(drone.ticksUsed()).append(",");
        json.append("\"tickLimit\":").append(drone.tickBudget()).append(",");
        json.append("\"runtimeError\":").append(Json.str(runtimeError)).append(",");
        json.append("\"stdout\":").append(Json.str(limit(captured.toString(), 12000)));
        json.append("}");
        realOut.println(json.toString());
    }

    private static boolean hasArg(String[] args, String flag) {
        for (int i = 0; i < args.length; i++) if (flag.equals(args[i])) return true;
        return false;
    }

    private static String argValue(String[] args, String flag) {
        for (int i = 0; i < args.length - 1; i++) {
            if (flag.equals(args[i])) return args[i + 1];
        }
        return null;
    }

    private static String limit(String s, int max) {
        if (s == null) return "";
        return s.length() <= max ? s : s.substring(0, max) + "\n... output truncated ...";
    }

    private static String defaultFarmStateJson() {
        return "{\"version\":1"
            + ",\"currentObjectiveId\":\"first-sprout\""
            + ",\"width\":6"
            + ",\"height\":4"
            + ",\"tick\":0"
            + ",\"tiles\":[]"
            + ",\"resources\":{}"
            + ",\"unlocked\":[]"
            + ",\"concepts\":{}"
            + ",\"stateCodec\":\"\""
            + "}";
    }

    private static class EngineState {
        String currentObjectiveId = Objectives.first().id;
        int width = Objectives.first().farmWidth;
        int height = Objectives.first().farmHeight;
        int tick = 0;
        Crop[][] crops = new Crop[height][width];
        int[][] planted = new int[height][width];
        int[][] moisture = new int[height][width];
        int[] resources = new int[Crop.values().length];
        Set<String> unlocked = new LinkedHashSet<String>();
        Progress progress = new Progress();

        EngineState() {
            fillEmpty();
        }

        static EngineState load(String path) throws Exception {
            EngineState state = new EngineState();
            if (path == null || path.length() == 0) return state;
            File file = new File(path);
            if (!file.exists()) return state;
            List<String> lines = Files.readAllLines(file.toPath(), StandardCharsets.UTF_8);
            Map<String, String> values = new LinkedHashMap<String, String>();
            for (String line : lines) {
                int eq = line.indexOf('=');
                if (eq <= 0) continue;
                values.put(line.substring(0, eq), line.substring(eq + 1));
            }
            String objectiveId = value(values, "currentObjectiveId", state.currentObjectiveId);
            state.currentObjectiveId = Objectives.hasId(objectiveId) ? objectiveId : Objectives.first().id;
            state.width = clamp(parseInt(value(values, "width", Integer.toString(state.width)), state.width), 1, 12);
            state.height = clamp(parseInt(value(values, "height", Integer.toString(state.height)), state.height), 1, 12);
            state.tick = clamp(parseInt(value(values, "tick", "0"), 0), 0, 1_000_000);
            state.crops = new Crop[state.height][state.width];
            state.planted = new int[state.height][state.width];
            state.moisture = new int[state.height][state.width];
            state.fillEmpty();
            state.loadTiles(value(values, "tiles", ""));
            state.loadResources(value(values, "resources", ""));
            state.loadUnlocked(value(values, "unlocked", ""));
            state.progress.loadStateString(value(values, "concepts", ""));
            return state;
        }

        Farm toFarm(int minWidth, int minHeight) {
            int w = Math.max(width, minWidth);
            int h = Math.max(height, minHeight);
            Farm farm = new Farm(w, h);
            farm.setTick(tick);
            for (int y = 0; y < h; y++) {
                for (int x = 0; x < w; x++) {
                    Crop c = (y < height && x < width) ? crops[y][x] : Crop.NONE;
                    int plantedAt = (y < height && x < width) ? planted[y][x] : -1;
                    int m = (y < height && x < width) ? moisture[y][x] : 0;
                    farm.setTileInternal(x, y, c, plantedAt, m);
                }
            }
            for (int i = 0; i < resources.length; i++) {
                farm.setResourceInternal(Crop.values()[i], resources[i]);
            }
            return farm;
        }

        String farmStateJson(Farm farm) {
            return "{"
                + "\"version\":1"
                + ",\"currentObjectiveId\":" + Json.str(currentObjectiveId)
                + ",\"width\":" + farm.width()
                + ",\"height\":" + farm.height()
                + ",\"tick\":" + farm.tick()
                + ",\"tiles\":" + farm.tilesJson()
                + ",\"resources\":" + farm.resourcesJson()
                + ",\"unlocked\":" + unlockedJson()
                + ",\"concepts\":" + progress.conceptsJson()
                + ",\"stateCodec\":" + Json.str(stateCodecString(farm))
                + "}";
        }

        private String stateCodecString(Farm farm) {
            return "version=1\n"
                + "currentObjectiveId=" + currentObjectiveId + "\n"
                + "width=" + farm.width() + "\n"
                + "height=" + farm.height() + "\n"
                + "tick=" + farm.tick() + "\n"
                + "tiles=" + tilesStateString(farm) + "\n"
                + "resources=" + farm.resourcesStateString() + "\n"
                + "unlocked=" + joinUnlocked() + "\n"
                + "concepts=" + progress.stateString() + "\n";
        }

        private String tilesStateString(Farm farm) {
            StringBuilder sb = new StringBuilder();
            for (int y = 0; y < farm.height(); y++) {
                if (y > 0) sb.append("|");
                for (int x = 0; x < farm.width(); x++) {
                    if (x > 0) sb.append(",");
                    sb.append(farm.cropAt(x, y).name()).append(":")
                        .append(farm.plantedTickAt(x, y)).append(":")
                        .append(farm.tileAt(x, y).moisture());
                }
            }
            return sb.toString();
        }

        private String unlockedJson() {
            StringBuilder sb = new StringBuilder("[");
            boolean first = true;
            for (String unlock : unlocked) {
                if (!first) sb.append(",");
                first = false;
                sb.append(Json.str(unlock));
            }
            sb.append("]");
            return sb.toString();
        }

        private String joinUnlocked() {
            StringBuilder sb = new StringBuilder();
            boolean first = true;
            for (String unlock : unlocked) {
                if (!first) sb.append(",");
                first = false;
                sb.append(unlock);
            }
            return sb.toString();
        }

        private void fillEmpty() {
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    crops[y][x] = Crop.NONE;
                    planted[y][x] = -1;
                    moisture[y][x] = 0;
                }
            }
        }

        private void loadTiles(String raw) {
            if (raw == null || raw.length() == 0) return;
            String[] rows = raw.split("\\|");
            for (int y = 0; y < rows.length && y < height; y++) {
                String[] cells = rows[y].split(",");
                for (int x = 0; x < cells.length && x < width; x++) {
                    String[] parts = cells[x].split(":");
                    if (parts.length < 3) continue;
                    crops[y][x] = parseCrop(parts[0]);
                    planted[y][x] = clamp(parseInt(parts[1], -1), -1, tick);
                    moisture[y][x] = clamp(parseInt(parts[2], 0), 0, 100);
                }
            }
        }

        private void loadResources(String raw) {
            if (raw == null || raw.length() == 0) return;
            String[] parts = raw.split(",");
            for (int i = 0; i < parts.length; i++) {
                String[] pair = parts[i].split(":");
                if (pair.length != 2) continue;
                resources[parseCrop(pair[0]).ordinal()] = clamp(parseInt(pair[1], 0), 0, 999_999);
            }
        }

        private void loadUnlocked(String raw) {
            if (raw == null || raw.length() == 0) return;
            String[] parts = raw.split(",");
            for (int i = 0; i < parts.length; i++) {
                if (Objectives.hasUnlock(parts[i])) unlocked.add(parts[i]);
            }
        }

        private static String value(Map<String, String> values, String key, String fallback) {
            String got = values.get(key);
            return got == null ? fallback : got;
        }

        private static int parseInt(String raw, int fallback) {
            try {
                return Integer.parseInt(raw);
            } catch (RuntimeException e) {
                return fallback;
            }
        }

        private static Crop parseCrop(String raw) {
            try {
                return Crop.valueOf(raw);
            } catch (RuntimeException e) {
                return Crop.NONE;
            }
        }

        private static int clamp(int value, int min, int max) {
            if (value < min) return min;
            if (value > max) return max;
            return value;
        }
    }
}
