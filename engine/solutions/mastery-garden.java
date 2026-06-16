public class Strategy {
    public void run(Drone drone, Farm farm) {
        int result = sumTo(10);
        drone.watch("recursiveSum", result);
    }

    int sumTo(int n) {
        if (n == 0) {
            return 0;
        }
        return n + sumTo(n - 1);
    }
}
