public class Strategy {
    public void run(Drone drone, Farm farm) {
        int[] prices = farm.prices();
        String[] crops = farm.crops();

        for (int slot = 0; slot < prices.length - 1; slot++) {
            int best = slot;
            for (int i = slot + 1; i < prices.length; i++) {
                if (prices[i] > prices[best]) {
                    best = i;
                }
                drone.watch("i", i);
            }
            int priceTemp = prices[slot];
            prices[slot] = prices[best];
            prices[best] = priceTemp;

            String cropTemp = crops[slot];
            crops[slot] = crops[best];
            crops[best] = cropTemp;
        }

        drone.watch("rankedPrices", join(prices));
        drone.watch("bestCrop", crops[0]);
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
