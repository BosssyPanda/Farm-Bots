public class Strategy {
    public void run(Drone drone, Farm farm) {
        for (int x = 0; x < farm.width(); x++) {
            drone.plant(Crop.WHEAT);
            drone.watch("tiles", x + 1);
            if (x < farm.width() - 1) {
                drone.moveEast();
            }
        }
    }
}
