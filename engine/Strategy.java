/**
 * Sample/reference solution so the engine runs standalone (`java -cp build/engine Runner`).
 * In the real game this file is REPLACED by the player's code from the browser editor;
 * the player only ever writes the body of run(...).
 */
public class Strategy {
    public void run(Drone drone, Farm farm) {
        for (int i = 0; i < 3; i++) {
            drone.moveEast();
            drone.plant(Crop.WHEAT);
            drone.watch("planted", i + 1);
        }
    }
}
