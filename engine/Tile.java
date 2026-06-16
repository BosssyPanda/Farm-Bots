/**
 * A read-only snapshot of a single field tile, returned to player code by
 * {@code drone.scan()} / {@code farm.tileAt(x, y)}.
 */
public class Tile {
    private final Crop crop;
    private final boolean ripe;
    private final int moisture;

    public Tile(Crop crop, boolean ripe, int moisture) {
        this.crop = crop;
        this.ripe = ripe;
        this.moisture = moisture;
    }

    public Crop crop() { return crop; }
    public boolean ripe() { return ripe; }
    public int moisture() { return moisture; }
}
