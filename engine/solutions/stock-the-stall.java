public class Strategy {
    public void run(Drone drone, Farm farm) {
        int[] prices = farm.prices();
        String[] crops = farm.crops();
        int total = 0;
        int minPrice = prices[0];
        int maxPrice = prices[0];
        for (int i = 0; i < prices.length; i++) {
            total += prices[i];
            if (prices[i] < minPrice) {
                minPrice = prices[i];
            }
            if (prices[i] > maxPrice) {
                maxPrice = prices[i];
            }
            drone.watch("i", i);
        }
        drone.watch("total", total);
        drone.watch("minPrice", minPrice);
        drone.watch("maxPrice", maxPrice);
        drone.watch("count", crops.length);
    }
}
