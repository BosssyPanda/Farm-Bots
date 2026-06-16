public class Strategy {
    public void run(Drone drone, Farm farm) {
        int[] prices = farm.prices();
        int swaps = 0;
        for (int pass = 0; pass < prices.length - 1; pass++) {
            for (int i = 0; i < prices.length - 1 - pass; i++) {
                if (prices[i] > prices[i + 1]) {
                    int temp = prices[i];
                    prices[i] = prices[i + 1];
                    prices[i + 1] = temp;
                    swaps++;
                }
                drone.watch("i", i);
            }
        }
        drone.watch("swaps", swaps);
        drone.watch("sortedPrices", join(prices));
    }

    String join(int[] values) {
        String text = "";
        for (int i = 0; i < values.length; i++) {
            if (i > 0) {
                text += ",";
            }
            text += values[i];
        }
        return text;
    }
}
