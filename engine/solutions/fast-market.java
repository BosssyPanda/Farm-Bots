public class Strategy {
    public void run(Drone drone, Farm farm) {
        int[] prices = farm.prices();
        int target = 21;
        int low = 0;
        int high = prices.length - 1;
        int foundIndex = -1;
        int comparisons = 0;

        while (low <= high) {
            int mid = (low + high) / 2;
            comparisons++;
            drone.watch("low", low);
            drone.watch("high", high);
            drone.watch("mid", mid);
            if (prices[mid] == target) {
                foundIndex = mid;
                break;
            } else if (prices[mid] < target) {
                low = mid + 1;
            } else {
                high = mid - 1;
            }
        }

        drone.watch("foundIndex", foundIndex);
        drone.watch("comparisons", comparisons);
    }
}
