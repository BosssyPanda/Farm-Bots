public class Strategy {
    public void run(Drone drone, Farm farm) {
        drone.moveSouth();
        int harvested = 0;
        while (drone.x() < 4) {
            Crop got = drone.harvest();
            if (got != Crop.NONE) {
                harvested++;
            }
            drone.watch("harvested", harvested);
            if (drone.x() < 3) {
                drone.moveEast();
            } else {
                break;
            }
        }

        int code = farm.moisture()[0];
        int digitSum = 0;
        while (code > 0) {
            int digit = code % 10;
            digitSum += digit;
            code = code / 10;
            drone.watch("digitSum", digitSum);
        }
    }
}
