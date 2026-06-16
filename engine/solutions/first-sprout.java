public class Strategy {
    public void run(Drone drone, Farm farm) {
        for (int i = 0; i < 3; i++) {
            drone.moveEast();
            drone.plant(Crop.WHEAT);
            drone.watch("planted", i + 1);
        }
    }
}
