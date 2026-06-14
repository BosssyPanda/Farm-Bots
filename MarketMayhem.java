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
    static boolean learningMode = true;
    static int javaXp = 0;

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
        System.out.println("Market Mayhem v0.2 - Java Learning Edition");
        System.out.println("This terminal stock trading game teaches loops, arrays, methods, sequential search, and binary search.");
        System.out.println("Reach $" + WIN_NET_WORTH + " net worth by Day " + MAX_DAYS + " to win.");
        System.out.println("$" + LEGENDARY_NET_WORTH + " earns a legendary rating.");
        System.out.println("Falling below $" + BANKRUPT_NET_WORTH + " net worth ends the game early.");
        System.out.println("Learning Mode starts ON.");
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
        System.out.println("Java XP: " + javaXp);
        System.out.println("Learning Mode: " + getLearningModeText());
        System.out.println();
        System.out.println("0. Exit");
        System.out.println("1. View Market");
        System.out.println("2. Advance Day");
        System.out.println("3. View Portfolio");
        System.out.println("4. Buy Stock");
        System.out.println("5. Sell Stock");
        System.out.println("6. Java Concept Map");
        System.out.println("7. Loops + Arrays Lab");
        System.out.println("8. Methods Lab");
        System.out.println("9. Search Lab");
        System.out.println("10. Toggle Learning Mode");
        System.out.println("99. Run Developer Tests");
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
        } else if (choice == 6) {
            printJavaConceptMap();
        } else if (choice == 7) {
            runLoopsArraysLab(input);
        } else if (choice == 8) {
            runMethodsLab(input);
        } else if (choice == 9) {
            runSearchLab(input);
        } else if (choice == 10) {
            toggleLearningMode();
        } else if (choice == 99) {
            runDeveloperTests();
        } else {
            System.out.println("Invalid option. Please choose a menu option shown above.");
        }

        return true;
    }

    public static String getLearningModeText() {
        if (learningMode) {
            return "ON";
        }

        return "OFF";
    }

    public static void printCodeBlock(String title, String[] lines) {
        System.out.println("-----------------------------");
        System.out.println("JAVA CODE: " + title);
        System.out.println("-----------------------------");

        for (int i = 0; i < lines.length; i++) {
            System.out.println(lines[i]);
        }

        System.out.println("-----------------------------");
    }

    public static void awardJavaXp(int amount, String reason) {
        javaXp = javaXp + amount;
        System.out.println("+" + amount + " Java XP: " + reason);
        System.out.println("Total Java XP: " + javaXp);
    }

    public static void printLearningNote(String concept, String explanation) {
        if (learningMode) {
            System.out.println();
            System.out.println("Learning Note - " + concept);
            System.out.println(explanation);
            System.out.println();
        }
    }

    public static void toggleLearningMode() {
        learningMode = !learningMode;
        System.out.println("Learning Mode is now " + getLearningModeText() + ".");
    }

    public static int readIntInRangeOrCancel(Scanner input, String prompt, int min, int max) {
        System.out.print(prompt);

        if (!input.hasNextInt()) {
            String badInput = input.next();
            System.out.println("Invalid input: " + badInput + ". Action cancelled.");
            return Integer.MIN_VALUE;
        }

        int value = input.nextInt();

        if (value < min || value > max) {
            System.out.println("Input must be from " + min + " to " + max + ". Action cancelled.");
            return Integer.MIN_VALUE;
        }

        return value;
    }

    public static void printJavaConceptMap() {
        System.out.println("=============================");
        System.out.println("JAVA CONCEPT MAP");
        System.out.println("================");
        System.out.printf("%-22s %s%n", "Concept", "Where it appears in the game");
        System.out.printf("%-22s %s%n", "for loops", "printMarket, getPortfolioValue, sequential search");
        System.out.printf("%-22s %s%n", "while loops", "main game loop, binary search");
        System.out.printf("%-22s %s%n", "arrays", "tickers, stockIds, prices, sharesOwned");
        System.out.printf("%-22s %s%n", "methods", "buyStock, sellStock, advanceDay, printMarket");
        System.out.printf("%-22s %s%n", "parameters", "calculateTradeValue(index, shares)");
        System.out.printf("%-22s %s%n", "return values", "getPortfolioValue, getNetWorth, sequentialSearchTicker");
        System.out.printf("%-22s %s%n", "sequential search", "Search Lab");
        System.out.printf("%-22s %s%n", "binary search", "Search Lab");
        System.out.println();

        printCodeBlock("for loop using prices.length", new String[] {
                "for (int i = 0; i < prices.length; i++) {",
                "    System.out.println(tickers[i] + \" costs $\" + prices[i]);",
                "}"
        });

        printCodeBlock("method with parameter and return value", new String[] {
                "public static int calculateTradeValue(int index, int shares) {",
                "    return prices[index] * shares;",
                "}"
        });

        printCodeBlock("while loop from binary search", new String[] {
                "while (low <= high) {",
                "    int mid = (low + high) / 2;",
                "    if (stockIds[mid] == target) {",
                "        return mid;",
                "    }",
                "}"
        });

        awardJavaXp(10, "reviewed the Java Concept Map");
    }

    public static void runLoopsArraysLab(Scanner input) {
        System.out.println("=============================");
        System.out.println("LOOPS + ARRAYS LAB");
        System.out.println("===================");
        System.out.println("An array stores many values of the same type under one variable name.");
        System.out.println("Java indexes start at 0, so the first stock is prices[0].");
        System.out.println("The last valid index is prices.length - 1.");
        System.out.println("Loops should use i < prices.length because prices[prices.length] is past the end.");
        System.out.println();

        printCodeBlock("for loop traversal", new String[] {
                "for (int i = 0; i < prices.length; i++) {",
                "    System.out.println(tickers[i] + \" costs $\" + prices[i]);",
                "}"
        });

        printCodeBlock("while loop traversal", new String[] {
                "int i = 0;",
                "while (i < prices.length) {",
                "    System.out.println(tickers[i] + \" costs $\" + prices[i]);",
                "    i++;",
                "}"
        });

        printCodeBlock("off-by-one warning", new String[] {
                "for (int i = 0; i <= prices.length; i++) {",
                "    // WRONG: this tries to access prices[prices.length]",
                "}"
        });

        if (marketDataIsValid()) {
            System.out.println("Loop trace through the current market:");

            for (int i = 0; i < prices.length; i++) {
                System.out.println("i = " + i + " -> " + tickers[i] + " costs $" + prices[i]);
            }
        }

        System.out.println();
        System.out.println("Concept Check 1: What is the first valid array index?");
        System.out.println("Answer: 0");
        System.out.println("Concept Check 2: What is the last valid index of prices?");
        System.out.println("Answer: prices.length - 1");
        System.out.println("Concept Check 3: Why use i < prices.length?");
        System.out.println("Answer: It stops before the invalid index prices.length.");

        awardJavaXp(20, "completed the Loops + Arrays Lab");
    }

    public static void runMethodsLab(Scanner input) {
        System.out.println("=============================");
        System.out.println("METHODS LAB");
        System.out.println("===========");
        System.out.println("A method is a named block of code. Methods keep main shorter and make behavior reusable.");
        System.out.println("A parameter is input to a method. A return value is output from a method.");
        System.out.println("static means the method belongs to the class at this beginner stage.");
        System.out.println();

        printCodeBlock("actual game method examples", new String[] {
                "public static int getPortfolioValue()",
                "public static int calculateTradeValue(int index, int shares)",
                "public static boolean isValidStockIndex(int index)",
                "public static void printMarket()"
        });

        System.out.println("getPortfolioValue returns an int.");
        System.out.println("calculateTradeValue uses parameters named index and shares.");
        System.out.println("isValidStockIndex returns true or false.");
        System.out.println("printMarket is void because it displays output instead of returning a value.");
        System.out.println();

        int exampleIndex = 0;
        int exampleShares = 2;

        if (isValidStockIndex(exampleIndex)) {
            int exampleValue = calculateTradeValue(exampleIndex, exampleShares);
            System.out.println("Trace: calculateTradeValue(" + exampleIndex + ", " + exampleShares + ")");
            System.out.println("prices[" + exampleIndex + "] is $" + prices[exampleIndex]);
            System.out.println("return prices[index] * shares -> $" + exampleValue);
            System.out.println("No shares were bought or sold during this lab.");
        }

        awardJavaXp(20, "completed the Methods Lab");
    }

    public static void runSearchLab(Scanner input) {
        if (!marketDataIsValid()) {
            System.out.println("Market data is not ready. Please restart the game.");
            return;
        }

        System.out.println("=============================");
        System.out.println("SEARCH LAB");
        System.out.println("==========");
        System.out.println("0. Return");
        System.out.println("1. Sequential Search Demo by Ticker");
        System.out.println("2. Sequential Search Demo by Stock ID");
        System.out.println("3. Binary Search Demo by Stock ID");
        System.out.println("4. Compare Sequential vs Binary Search");

        int choice = readIntInRangeOrCancel(input, "Choose a search lab option: ", 0, 4);

        if (choice == Integer.MIN_VALUE || choice == 0) {
            return;
        }

        if (choice == 1) {
            System.out.print("Enter ticker to search: ");
            String target = input.next();
            int[] result = sequentialSearchTickerWithCount(target);
            printSearchResult(result, "ticker " + target);
            awardJavaXp(15, "ran sequential ticker search");
        } else if (choice == 2) {
            int target = readIntInRangeOrCancel(input, "Enter stock ID to search: ", Integer.MIN_VALUE + 1, Integer.MAX_VALUE);

            if (target == Integer.MIN_VALUE) {
                return;
            }

            int[] result = sequentialSearchIdWithCount(target);
            printSearchResult(result, "stock ID " + target);
            awardJavaXp(15, "ran sequential ID search");
        } else if (choice == 3) {
            int target = readIntInRangeOrCancel(input, "Enter stock ID to search: ", Integer.MIN_VALUE + 1, Integer.MAX_VALUE);

            if (target == Integer.MIN_VALUE) {
                return;
            }

            if (!stockIdsAreSortedAscending()) {
                System.out.println("Binary search requires sorted data. stockIds is not sorted right now.");
                return;
            }

            System.out.println("Binary search requires sorted data. stockIds is sorted ascending right now.");
            int[] result = binarySearchIdWithCount(target);
            printSearchResult(result, "stock ID " + target);
            awardJavaXp(20, "ran binary ID search");
        } else if (choice == 4) {
            int target = stockIds[stockIds.length - 1];
            int[] sequentialResult = sequentialSearchIdWithCount(target);

            if (!stockIdsAreSortedAscending()) {
                System.out.println("Binary search requires sorted data. stockIds is not sorted right now.");
                return;
            }

            System.out.println("Binary search requires sorted data. stockIds is sorted ascending right now.");
            int[] binaryResult = binarySearchIdWithCount(target);
            System.out.println("Target stock ID: " + target);
            System.out.println("Sequential search comparisons: " + sequentialResult[1]);
            System.out.println("Binary search comparisons: " + binaryResult[1]);
            System.out.println("Binary search usually uses fewer comparisons because it cuts the search range in half each loop.");
            awardJavaXp(25, "compared sequential search and binary search");
        }
    }

    public static void printSearchResult(int[] result, String label) {
        if (result[0] == -1) {
            System.out.println("Did not find " + label + ".");
        } else {
            System.out.println("Found " + label + " at index " + result[0] + ".");
            printStockLine(result[0]);
        }

        System.out.println("Comparisons: " + result[1]);
    }

    public static int[] sequentialSearchTickerWithCount(String target) {
        int[] result = new int[2];
        result[0] = -1;
        result[1] = 0;

        for (int i = 0; i < tickers.length; i++) {
            result[1] = result[1] + 1;

            if (learningMode) {
                System.out.println("Checking index " + i + ": " + tickers[i]);
            }

            if (tickers[i].equalsIgnoreCase(target)) {
                result[0] = i;
                return result;
            }
        }

        return result;
    }

    public static int[] sequentialSearchIdWithCount(int target) {
        int[] result = new int[2];
        result[0] = -1;
        result[1] = 0;

        for (int i = 0; i < stockIds.length; i++) {
            result[1] = result[1] + 1;

            if (learningMode) {
                System.out.println("Checking index " + i + ": " + stockIds[i]);
            }

            if (stockIds[i] == target) {
                result[0] = i;
                return result;
            }
        }

        return result;
    }

    public static int[] binarySearchIdWithCount(int target) {
        int[] result = new int[2];
        result[0] = -1;
        result[1] = 0;

        int low = 0;
        int high = stockIds.length - 1;

        while (low <= high) {
            int mid = (low + high) / 2;
            result[1] = result[1] + 1;

            if (learningMode) {
                System.out.println("low = " + low + ", high = " + high + ", mid = " + mid + ", stockIds[mid] = " + stockIds[mid]);
            }

            if (stockIds[mid] == target) {
                result[0] = mid;
                return result;
            } else if (stockIds[mid] < target) {
                low = mid + 1;
            } else {
                high = mid - 1;
            }
        }

        return result;
    }

    public static boolean stockIdsAreSortedAscending() {
        if (!marketDataIsValid()) {
            return false;
        }

        for (int i = 0; i < stockIds.length - 1; i++) {
            if (stockIds[i] > stockIds[i + 1]) {
                return false;
            }
        }

        return true;
    }

    public static int[] copyIntArray(int[] source) {
        int[] copy = new int[source.length];

        for (int i = 0; i < source.length; i++) {
            copy[i] = source[i];
        }

        return copy;
    }

    public static int recordTestResult(String testName, boolean passed) {
        if (passed) {
            System.out.println("PASS: " + testName);
            return 1;
        }

        System.out.println("FAIL: " + testName);
        return 0;
    }

    public static void runDeveloperTests() {
        if (!marketDataIsValid()) {
            System.out.println("Market data is not ready. Please restart the game.");
            return;
        }

        System.out.println("=============================");
        System.out.println("DEVELOPER TESTS");
        System.out.println("===============");

        int savedCash = cash;
        int savedDay = day;
        boolean savedGameOver = gameOver;
        String savedGameOverReason = gameOverReason;
        boolean savedLearningMode = learningMode;
        int[] savedPrices = copyIntArray(prices);
        int[] savedPreviousPrices = copyIntArray(previousPrices);
        int[] savedSharesOwned = copyIntArray(sharesOwned);
        int[] savedRiskLevels = copyIntArray(riskLevels);
        int[] savedStockIds = copyIntArray(stockIds);

        learningMode = false;

        int passed = 0;
        int total = 0;

        total = total + 1;
        passed = passed + recordTestResult("isValidStockIndex(-1) is false", !isValidStockIndex(-1));

        total = total + 1;
        passed = passed + recordTestResult("isValidStockIndex(0) is true", isValidStockIndex(0));

        total = total + 1;
        passed = passed + recordTestResult("isValidStockIndex(prices.length - 1) is true", isValidStockIndex(prices.length - 1));

        total = total + 1;
        passed = passed + recordTestResult("isValidStockIndex(prices.length) is false", !isValidStockIndex(prices.length));

        for (int i = 0; i < sharesOwned.length; i++) {
            sharesOwned[i] = 0;
        }

        total = total + 1;
        passed = passed + recordTestResult("getPortfolioValue() starts at 0 when no shares are owned", getPortfolioValue() == 0);

        total = total + 1;
        passed = passed + recordTestResult("sequentialSearchTickerWithCount(\"APEX\") finds index 0", sequentialSearchTickerWithCount("APEX")[0] == 0);

        total = total + 1;
        passed = passed + recordTestResult("sequentialSearchTickerWithCount(\"FAKE\") returns -1", sequentialSearchTickerWithCount("FAKE")[0] == -1);

        total = total + 1;
        passed = passed + recordTestResult("sequentialSearchIdWithCount(104) finds index 3", sequentialSearchIdWithCount(104)[0] == 3);

        total = total + 1;
        passed = passed + recordTestResult("binarySearchIdWithCount(104) finds index 3 when stockIds are sorted",
                stockIdsAreSortedAscending() && binarySearchIdWithCount(104)[0] == 3);

        total = total + 1;
        passed = passed + recordTestResult("binarySearchIdWithCount(999) returns -1", binarySearchIdWithCount(999)[0] == -1);

        cash = savedCash;
        day = savedDay;
        gameOver = savedGameOver;
        gameOverReason = savedGameOverReason;
        learningMode = savedLearningMode;
        prices = savedPrices;
        previousPrices = savedPreviousPrices;
        sharesOwned = savedSharesOwned;
        riskLevels = savedRiskLevels;
        stockIds = savedStockIds;

        System.out.println("Developer Tests Passed: " + passed + " / " + total);

        if (passed == total) {
            awardJavaXp(30, "all developer tests passed");
        }
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

        // total is an accumulator: each loop adds one stock position value.
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

        printLearningNote("Array mutation", "advanceDay copies prices into previousPrices, then changes prices[i] inside a for loop.");
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
        printLearningNote("Index validation", "isValidStockIndex checks the player index before prices[index] or sharesOwned[index] is used.");

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
        printLearningNote("Array mutation", "sellStock changes sharesOwned[index] only after the stock index and share amount are valid.");

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
        printLearningNote("Indexed for loop", "This table uses for (int i = 0; i < prices.length; i++) to line up values from parallel arrays.");
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
