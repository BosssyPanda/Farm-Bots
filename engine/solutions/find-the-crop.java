public class Strategy {
    public void run(Drone drone, Farm farm) {
        String[] crops = farm.crops();
        int foundIndex = -1;
        int comparisons = 0;
        for (int i = 0; i < crops.length; i++) {
            comparisons++;
            drone.watch("i", i);
            if (crops[i].equals("PUMPKIN")) {
                foundIndex = i;
                break;
            }
        }
        drone.watch("foundIndex", foundIndex);
        drone.watch("comparisons", comparisons);
    }
}
