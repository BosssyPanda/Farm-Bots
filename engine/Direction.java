/**
 * Compass directions the drone can move. Coordinates: x = column (left -> right),
 * y = row (top -> bottom), both 0-based. NORTH decreases y; SOUTH increases y.
 */
public enum Direction {
    NORTH, SOUTH, EAST, WEST;

    public int dx() {
        switch (this) {
            case EAST: return 1;
            case WEST: return -1;
            default:   return 0;
        }
    }

    public int dy() {
        switch (this) {
            case SOUTH: return 1;
            case NORTH: return -1;
            default:    return 0;
        }
    }
}
