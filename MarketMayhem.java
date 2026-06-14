import java.util.Scanner;

public class MarketMayhem {
    static String[] tickers;
    static int[] stockIds;
    static int[] prices;
    static int[] previousPrices;
    static int[] sharesOwned;
    static int[] riskLevels;

    static int day = 1;
    static final int MAX_DAYS = 15;
    static final int MIN_STOCK_PRICE = 1;
    static final int STARTING_CASH = 1000;
    static final int WIN_NET_WORTH = 1500;
    static final int LEGENDARY_NET_WORTH = 2200;
    static final int BANKRUPT_NET_WORTH = 200;
    static int cash;
    static boolean gameOver = false;
    static String gameOverReason = "";

    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);
        boolean running = true;

        printWelcome();
        initializeMarket();
        initializePlayer();

        while (running && !gameOver) {
            printMenu();
            int choice = getMenuChoice(input);
            running = processMenuChoice(choice, input);
            pauseBriefly();
        }

        input.close();

        if (!gameOver) {
            System.out.println("Goodbye from Market Mayhem!");
        }
    }

    public static void printWelcome() {
        System.out.println("Welcome to Market Mayhem!");
        System.out.println("v0.1 is a terminal stock trading game for learning Java.");
        System.out.println("Reach $" + WIN_NET_WORTH + " net worth by Day " + MAX_DAYS + " to win.");
        System.out.println("$" + LEGENDARY_NET_WORTH + " earns a legendary rating.");
        System.out.println("Falling below $" + BANKRUPT_NET_WORTH + " net worth ends the game early.");
        System.out.println();
    }

    public static void initializeMarket() {
        tickers = new String[] {"APEX", "NOVA", "BYTE", "IRON", "FUEL", "VRTX", "ZETA", "OMNI"};
        stockIds = new int[] {101, 102, 103, 104, 105, 106, 107, 108};
        prices = new int[] {45, 80, 62, 38, 55, 97, 24, 70};
        previousPrices = new int[prices.length];

        for (int i = 0; i < prices.length; i++) {
            previousPrices[i] = prices[i];
        }

        sharesOwned = new int[prices.length];
        riskLevels = new int[] {4, 6, 5, 7, 8, 9, 3, 5};
    }

    public static void initializePlayer() {
        cash = STARTING_CASH;
    }

    public static void printMenu() {
        System.out.println("=============================");
        System.out.println("MARKET MAYHEM");
        System.out.println("=============");
        System.out.println("Day: " + day + " / " + MAX_DAYS);
        System.out.println("Cash: $" + cash);
        System.out.println("Portfolio: $" + getPortfolioValue());
        System.out.println("Net Worth: $" + getNetWorth());
        System.out.println("Win Target: $" + WIN_NET_WORTH);
        System.out.println("Legendary Target: $" + LEGENDARY_NET_WORTH);
        System.out.println();
        System.out.println("0. Exit");
        System.out.println("1. View Market");
        System.out.println("2. Advance Day");
        System.out.println("3. View Portfolio");
        System.out.println("4. Buy Stock");
        System.out.println("5. Sell Stock");
        System.out.println();
        System.out.print("Choose an option: ");
    }

    public static int getMenuChoice(Scanner input) {
        if (input.hasNextInt()) {
            int choice = input.nextInt();
            input.nextLine();
            return choice;
        }

        input.nextLine();
        return -1;
    }

    public static boolean processMenuChoice(int choice, Scanner input) {
        if (gameOver) {
            System.out.println("The game is already over.");
            return false;
        }

        if (choice == 0) {
            return false;
        } else if (choice == 1) {
            printMarket();
        } else if (choice == 2) {
            advanceDay();
        } else if (choice == 3) {
            printPortfolio();
        } else if (choice == 4) {
            buyStock(input);
        } else if (choice == 5) {
            sellStock(input);
        } else {
            System.out.println("Invalid option. Please choose a number from 0 to 5.");
        }

        return true;
    }

    public static boolean marketDataIsValid() {
        if (tickers == null || stockIds == null || prices == null) {
            return false;
        }

        if (previousPrices == null || sharesOwned == null || riskLevels == null) {
            return false;
        }

        if (prices.length == 0) {
            return false;
        }

        if (tickers.length != prices.length) {
            return false;
        }

        if (stockIds.length != prices.length) {
            return false;
        }

        if (previousPrices.length != prices.length) {
            return false;
        }

        if (sharesOwned.length != prices.length) {
            return false;
        }

        if (riskLevels.length != prices.length) {
            return false;
        }

        return true;
    }

    public static int getDailyChange(int index) {
        return prices[index] - previousPrices[index];
    }

    public static int getPortfolioValue() {
        if (!marketDataIsValid()) {
            return 0;
        }

        int total = 0;

        for (int i = 0; i < prices.length; i++) {
            total = total + (prices[i] * sharesOwned[i]);
        }

        return total;
    }

    public static int getNetWorth() {
        return cash + getPortfolioValue();
    }

    public static int getTotalSharesOwned() {
        if (!marketDataIsValid()) {
            return 0;
        }

        int total = 0;

        for (int i = 0; i < prices.length; i++) {
            total = total + sharesOwned[i];
        }

        return total;
    }

    public static boolean ownsAnyShares() {
        return getTotalSharesOwned() > 0;
    }

    public static boolean hasWon() {
        return getNetWorth() >= WIN_NET_WORTH;
    }

    public static boolean isLegendaryTrader() {
        return getNetWorth() >= LEGENDARY_NET_WORTH;
    }

    public static boolean isBankrupt() {
        return getNetWorth() < BANKRUPT_NET_WORTH;
    }

    public static boolean isFinalTradingDay() {
        return day >= MAX_DAYS;
    }

    public static int indexOfMostValuableOwnedStock() {
        if (!marketDataIsValid()) {
            return -1;
        }

        if (!ownsAnyShares()) {
            return -1;
        }

        int bestIndex = -1;
        int bestValue = Integer.MIN_VALUE;

        for (int i = 0; i < prices.length; i++) {
            if (sharesOwned[i] > 0) {
                int positionValue = prices[i] * sharesOwned[i];

                if (positionValue > bestValue) {
                    bestValue = positionValue;
                    bestIndex = i;
                }
            }
        }

        return bestIndex;
    }

    public static String getFinalRating() {
        if (isLegendaryTrader()) {
            return "Legendary Trader";
        } else if (hasWon()) {
            return "Profitable Trader";
        } else if (isBankrupt()) {
            return "Bankrupt Trader";
        }

        return "Rookie Trader";
    }

    public static void printFinalSummary() {
        System.out.println("=============================");
        System.out.println("FINAL SUMMARY");
        System.out.println("=============");
        System.out.println("Reason: " + gameOverReason);
        System.out.println("Final Day: " + day + " / " + MAX_DAYS);
        System.out.println("Cash: $" + cash);
        System.out.println("Portfolio Value: $" + getPortfolioValue());
        System.out.println("Net Worth: $" + getNetWorth());
        System.out.println("Total Shares Owned: " + getTotalSharesOwned());
        System.out.println("Final Rating: " + getFinalRating());
        System.out.println("Win Target: $" + WIN_NET_WORTH);
        System.out.println("Legendary Target: $" + LEGENDARY_NET_WORTH);
        System.out.println("Bankrupt Threshold: $" + BANKRUPT_NET_WORTH);
        System.out.println();

        int bestIndex = indexOfMostValuableOwnedStock();

        if (bestIndex == -1) {
            System.out.println("Most valuable owned stock: None");
        } else {
            int positionValue = prices[bestIndex] * sharesOwned[bestIndex];
            System.out.println("Most valuable owned stock: "
                    + tickers[bestIndex]
                    + " | Shares: " + sharesOwned[bestIndex]
                    + " | Price: $" + prices[bestIndex]
                    + " | Value: $" + positionValue);
        }
    }

    public static void endGame(String reason) {
        if (gameOver) {
            return;
        }

        gameOver = true;
        gameOverReason = reason;
        printFinalSummary();
    }

    public static void checkGameOverAfterMarketMove() {
        if (isBankrupt()) {
            endGame("Your net worth fell below the bankruptcy threshold.");
        } else if (isFinalTradingDay()) {
            System.out.println("You have reached the final trading day. Make your final trades, then choose Advance Day to finish.");
        }
    }

    public static boolean isValidStockIndex(int index) {
        return marketDataIsValid() && index >= 0 && index < prices.length;
    }

    public static int readIntOrCancel(Scanner input, String prompt) {
        System.out.print(prompt);

        if (input.hasNextInt()) {
            return input.nextInt();
        }

        String badInput = input.next();
        System.out.println("Invalid input: " + badInput + ". Trade cancelled.");
        return Integer.MIN_VALUE;
    }

    public static int calculateTradeValue(int index, int shares) {
        return prices[index] * shares;
    }

    public static boolean canAffordTrade(int tradeValue) {
        return cash >= tradeValue;
    }

    public static void printStockLine(int index) {
        if (isValidStockIndex(index)) {
            System.out.println("Index: " + index
                    + " | Ticker: " + tickers[index]
                    + " | ID: " + stockIds[index]
                    + " | Price: $" + prices[index]
                    + " | Owned: " + sharesOwned[index]
                    + " | Risk: " + riskLevels[index]);
        }
    }

    public static void printTradeResult(String action, int index, int shares, int tradeValue) {
        System.out.println(action + " " + shares + " shares of " + tickers[index] + " for $" + tradeValue + ".");
        System.out.println("Cash: $" + cash);
        System.out.println("Portfolio Value: $" + getPortfolioValue());
        System.out.println("Net Worth: $" + getNetWorth());
    }

    public static int randomIntInRange(int min, int max) {
        return (int)(Math.random() * (max - min + 1)) + min;
    }

    public static int getPriceChangeForRisk(int riskLevel) {
        int maxChange = riskLevel + 2;
        return randomIntInRange(-maxChange, maxChange);
    }

    public static void copyCurrentPricesToPreviousPrices() {
        if (!marketDataIsValid()) {
            return;
        }

        for (int i = 0; i < prices.length; i++) {
            previousPrices[i] = prices[i];
        }
    }

    public static void updatePricesForNewDay() {
        if (!marketDataIsValid()) {
            return;
        }

        for (int i = 0; i < prices.length; i++) {
            int change = getPriceChangeForRisk(riskLevels[i]);
            prices[i] = prices[i] + change;

            if (prices[i] < MIN_STOCK_PRICE) {
                prices[i] = MIN_STOCK_PRICE;
            }
        }
    }

    public static void printDailyMarketSummary() {
        System.out.println("Daily Market Summary");
        System.out.println("Day: " + day + " / " + MAX_DAYS);
        System.out.println();
        System.out.printf("%-7s %-7s %-7s %-8s%n", "Ticker", "Prev", "Price", "Change");

        for (int i = 0; i < prices.length; i++) {
            int change = getDailyChange(i);
            String changeText = "";

            if (change >= 0) {
                changeText = "+" + change;
            } else {
                changeText = "" + change;
            }

            System.out.printf("%-7s $%-6d $%-6d %-8s%n",
                    tickers[i],
                    previousPrices[i],
                    prices[i],
                    changeText);
        }
    }

    public static void advanceDay() {
        if (gameOver) {
            System.out.println("The game is already over.");
            return;
        }

        if (!marketDataIsValid()) {
            System.out.println("Market data is not ready. Please restart the game.");
            return;
        }

        if (day >= MAX_DAYS) {
            if (hasWon()) {
                endGame("The final day ended and you reached the win target.");
            } else {
                endGame("The final day ended before you reached the win target.");
            }

            return;
        }

        copyCurrentPricesToPreviousPrices();
        updatePricesForNewDay();
        day = day + 1;

        System.out.println("The market moves into Day " + day + ".");
        printDailyMarketSummary();
        checkGameOverAfterMarketMove();
    }

    public static void buyStock(Scanner input) {
        if (!marketDataIsValid()) {
            System.out.println("Market data is not ready. Please restart the game.");
            return;
        }

        System.out.println("=============================");
        System.out.println("BUY STOCK");
        System.out.println("=========");
        printMarket();

        int index = readIntOrCancel(input, "Enter stock index to buy: ");

        if (index == Integer.MIN_VALUE) {
            return;
        }

        if (!isValidStockIndex(index)) {
            System.out.println("Invalid stock index. Trade cancelled.");
            return;
        }

        printStockLine(index);

        int shares = readIntOrCancel(input, "Enter number of shares to buy: ");

        if (shares == Integer.MIN_VALUE) {
            return;
        }

        if (shares <= 0) {
            System.out.println("Shares must be greater than 0. Trade cancelled.");
            return;
        }

        int tradeValue = calculateTradeValue(index, shares);

        if (!canAffordTrade(tradeValue)) {
            System.out.println("Not enough cash.");
            System.out.println("Required: $" + tradeValue);
            System.out.println("Cash: $" + cash);
            return;
        }

        cash = cash - tradeValue;
        sharesOwned[index] = sharesOwned[index] + shares;
        printTradeResult("Bought", index, shares, tradeValue);
    }

    public static void sellStock(Scanner input) {
        if (!marketDataIsValid()) {
            System.out.println("Market data is not ready. Please restart the game.");
            return;
        }

        System.out.println("=============================");
        System.out.println("SELL STOCK");
        System.out.println("==========");

        if (!ownsAnyShares()) {
            System.out.println("You do not own any shares to sell.");
            return;
        }

        printPortfolio();

        int index = readIntOrCancel(input, "Enter stock index to sell: ");

        if (index == Integer.MIN_VALUE) {
            return;
        }

        if (!isValidStockIndex(index)) {
            System.out.println("Invalid stock index. Trade cancelled.");
            return;
        }

        if (sharesOwned[index] == 0) {
            System.out.println("You do not own shares of this stock.");
            return;
        }

        printStockLine(index);

        int shares = readIntOrCancel(input, "Enter number of shares to sell: ");

        if (shares == Integer.MIN_VALUE) {
            return;
        }

        if (shares <= 0) {
            System.out.println("Shares must be greater than 0. Trade cancelled.");
            return;
        }

        if (shares > sharesOwned[index]) {
            System.out.println("You do not own that many shares. Trade cancelled.");
            return;
        }

        int tradeValue = calculateTradeValue(index, shares);
        cash = cash + tradeValue;
        sharesOwned[index] = sharesOwned[index] - shares;
        printTradeResult("Sold", index, shares, tradeValue);
    }

    public static void printPortfolio() {
        if (!marketDataIsValid()) {
            System.out.println("Market data is not ready. Please restart the game.");
            return;
        }

        System.out.println("=============================");
        System.out.println("PORTFOLIO");
        System.out.println("=========");
        System.out.println("Day: " + day + " / " + MAX_DAYS);
        System.out.println("Cash: $" + cash);
        System.out.println("Portfolio Value: $" + getPortfolioValue());
        System.out.println("Net Worth: $" + getNetWorth());
        System.out.println("Total Shares Owned: " + getTotalSharesOwned());
        System.out.println();

        if (!ownsAnyShares()) {
            System.out.println("You do not own any shares yet. Use Buy Stock to purchase shares.");
            return;
        }

        System.out.printf("%-7s %-7s %-7s %-8s%n", "Ticker", "Shares", "Price", "Value");

        for (int i = 0; i < prices.length; i++) {
            if (sharesOwned[i] > 0) {
                int positionValue = sharesOwned[i] * prices[i];
                System.out.printf("%-7s %-7d $%-6d $%-7d%n",
                        tickers[i],
                        sharesOwned[i],
                        prices[i],
                        positionValue);
            }
        }
    }

    public static void printMarket() {
        if (!marketDataIsValid()) {
            System.out.println("Market data is not ready. Please restart the game.");
            return;
        }

        System.out.println("=============================");
        System.out.println("MARKET");
        System.out.println("======");
        System.out.println("Day: " + day + " / " + MAX_DAYS);
        System.out.println();
        System.out.printf("%-6s %-7s %-5s %-7s %-7s %-8s %-7s %-5s%n",
                "Index", "Ticker", "ID", "Price", "Prev", "Change", "Owned", "Risk");

        for (int i = 0; i < prices.length; i++) {
            int change = getDailyChange(i);
            String changeText = "";

            if (change >= 0) {
                changeText = "+" + change;
            } else {
                changeText = "" + change;
            }

            System.out.printf("%-6d %-7s %-5d $%-6d $%-6d %-8s %-7d %-5d%n",
                    i,
                    tickers[i],
                    stockIds[i],
                    prices[i],
                    previousPrices[i],
                    changeText,
                    sharesOwned[i],
                    riskLevels[i]);
        }
    }

    public static void pauseBriefly() {
        System.out.println();
    }
}
