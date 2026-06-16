/**
 * Crops the drone can plant and harvest. {@code growTicks} is how many world ticks
 * after planting until the crop is ripe (harvestable).
 */
public enum Crop {
    NONE(0),
    WHEAT(4),
    CORN(6),
    PUMPKIN(10),
    CARROT(5);

    private final int growTicks;

    Crop(int growTicks) { this.growTicks = growTicks; }

    public int growTicks() { return growTicks; }
}
