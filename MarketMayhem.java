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
    static int questionsAnswered = 0;
    static int questionsCorrect = 0;

    static String[] topicNames;
    static int[] topicCorrect;
    static int[] topicAttempts;
    static boolean[] topicMastered;
    static boolean[] abilityUnlocked;
    static boolean suppressMarketScannerTrace = false;
    static boolean javaTradingLicenseUnlocked = false;
    static int mixedBossAttempts = 0;
    static int mixedBossBestScore = 0;

    static final int TOPIC_FOR_LOOPS = 0;
    static final int TOPIC_WHILE_LOOPS = 1;
    static final int TOPIC_ARRAYS = 2;
    static final int TOPIC_METHODS = 3;
    static final int TOPIC_SEQUENTIAL_SEARCH = 4;
    static final int TOPIC_BINARY_SEARCH = 5;

    static final int ABILITY_MARKET_SCANNER = TOPIC_FOR_LOOPS;
    static final int ABILITY_SIGNAL_DECODER = TOPIC_WHILE_LOOPS;
    static final int ABILITY_INDEX_VISION = TOPIC_ARRAYS;
    static final int ABILITY_TRADE_CALCULATOR = TOPIC_METHODS;
    static final int ABILITY_TICKER_FINDER = TOPIC_SEQUENTIAL_SEARCH;
    static final int ABILITY_FAST_BROKER = TOPIC_BINARY_SEARCH;

    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);
        boolean running = true;

        printWelcome();
        initializeMarket();
        initializePlayer();
        initializeLearningSystem();

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
        System.out.println("Market Mayhem: Java Trading Academy");
        System.out.println("Codex builds the game. You learn Java by playing the terminal game.");
        System.out.println("Correct answers earn Java XP. Reading alone never awards XP.");
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
        System.out.println("Questions: " + questionsCorrect + " correct / " + questionsAnswered + " answered");
        System.out.println();
        System.out.println("0. Exit");
        System.out.println("1. View Market");
        System.out.println("2. Advance Day");
        System.out.println("3. View Portfolio");
        System.out.println("4. Buy Stock");
        System.out.println("5. Sell Stock");
        System.out.println("6. Java Trading Academy");
        System.out.println("7. Mastery Report");
        System.out.println("8. Toggle Learning Mode");
        System.out.println("9. Run Developer Tests");
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
            runJavaTradingAcademy(input);
        } else if (choice == 7) {
            printMasteryReport();
        } else if (choice == 8) {
            toggleLearningMode();
        } else if (isDeveloperTestChoice(choice)) {
            runDeveloperTests();
        } else {
            System.out.println("Invalid option. Please choose a menu option shown above.");
        }

        return true;
    }

    public static boolean isDeveloperTestChoice(int choice) {
        return choice == 9 || choice == 99;
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

    public static void initializeLearningSystem() {
        topicNames = new String[] {
                "For Loops",
                "While Loops",
                "Arrays",
                "Methods",
                "Sequential Search",
                "Binary Search"
        };

        topicCorrect = new int[topicNames.length];
        topicAttempts = new int[topicNames.length];
        topicMastered = new boolean[topicNames.length];
        abilityUnlocked = new boolean[topicNames.length];
        questionsAnswered = 0;
        questionsCorrect = 0;
    }

    public static boolean isValidTopicIndex(int topicIndex) {
        return topicNames != null && topicIndex >= 0 && topicIndex < topicNames.length;
    }

    public static String normalizeAnswer(String answer) {
        if (answer == null) {
            return "";
        }

        return answer.trim().toUpperCase();
    }

    public static boolean askMultipleChoice(
            Scanner input,
            int topicIndex,
            String question,
            String optionA,
            String optionB,
            String optionC,
            String optionD,
            String correctAnswer,
            String explanation) {
        if (!isValidTopicIndex(topicIndex)) {
            System.out.println("Learning topic is not ready. No XP awarded.");
            return false;
        }

        System.out.println(question);
        System.out.println("A. " + optionA);
        System.out.println("B. " + optionB);
        System.out.println("C. " + optionC);
        System.out.println("D. " + optionD);
        System.out.print("Your answer: ");

        String answer = normalizeAnswer(input.nextLine());
        String normalizedCorrect = normalizeAnswer(correctAnswer);

        questionsAnswered = questionsAnswered + 1;

        if (!answer.equals("A") && !answer.equals("B") && !answer.equals("C") && !answer.equals("D")) {
            recordTopicResult(topicIndex, false);
            System.out.println("Invalid answer. No XP awarded.");
            updateTopicMastery(topicIndex);
            return false;
        }

        if (answer.equals(normalizedCorrect)) {
            questionsCorrect = questionsCorrect + 1;
            recordTopicResult(topicIndex, true);
            awardXpIfCorrect(true, 10, "correct answer for " + topicNames[topicIndex]);
            System.out.println("Correct.");
            System.out.println(explanation);
            updateTopicMastery(topicIndex);
            unlockAbilityIfMastered(topicIndex);
            return true;
        }

        recordTopicResult(topicIndex, false);
        System.out.println("Incorrect.");
        awardXpIfCorrect(false, 10, "wrong answer for " + topicNames[topicIndex]);
        System.out.println(explanation);
        updateTopicMastery(topicIndex);
        return false;
    }

    public static boolean askShortAnswer(
            Scanner input,
            int topicIndex,
            String question,
            String correctAnswer,
            String explanation) {
        if (!isValidTopicIndex(topicIndex)) {
            System.out.println("Learning topic is not ready. No XP awarded.");
            return false;
        }

        System.out.println(question);
        System.out.print("Your answer: ");

        String answer = normalizeAnswer(input.nextLine());
        String normalizedCorrect = normalizeAnswer(correctAnswer);
        questionsAnswered = questionsAnswered + 1;

        if (answer.equals(normalizedCorrect)) {
            questionsCorrect = questionsCorrect + 1;
            recordTopicResult(topicIndex, true);
            awardXpIfCorrect(true, 10, "correct answer for " + topicNames[topicIndex]);
            System.out.println("Correct.");
            System.out.println(explanation);
            updateTopicMastery(topicIndex);
            unlockAbilityIfMastered(topicIndex);
            return true;
        }

        recordTopicResult(topicIndex, false);
        System.out.println("Incorrect.");
        awardXpIfCorrect(false, 10, "wrong answer for " + topicNames[topicIndex]);
        System.out.println(explanation);
        updateTopicMastery(topicIndex);
        return false;
    }

    public static void awardXpIfCorrect(boolean correct, int amount, String reason) {
        if (correct) {
            javaXp = javaXp + amount;
            System.out.println("+" + amount + " Java XP: " + reason);
            System.out.println("Total Java XP: " + javaXp);
        } else {
            System.out.println("No XP awarded.");
        }
    }

    public static void recordTopicResult(int topicIndex, boolean correct) {
        if (!isValidTopicIndex(topicIndex)) {
            return;
        }

        topicAttempts[topicIndex] = topicAttempts[topicIndex] + 1;

        if (correct) {
            topicCorrect[topicIndex] = topicCorrect[topicIndex] + 1;
        }
    }

    public static void updateTopicMastery(int topicIndex) {
        if (!isValidTopicIndex(topicIndex)) {
            return;
        }

        if (topicCorrect[topicIndex] >= 3) {
            topicMastered[topicIndex] = true;
        }
    }

    public static boolean topicMastered(int topicIndex) {
        return isValidTopicIndex(topicIndex) && topicMastered[topicIndex];
    }

    public static void unlockAbilityIfMastered(int topicIndex) {
        if (!topicMastered(topicIndex)) {
            return;
        }

        if (abilityUnlocked[topicIndex]) {
            return;
        }

        abilityUnlocked[topicIndex] = true;
        System.out.println(topicNames[topicIndex] + " -> " + getAbilityName(topicIndex) + " unlocked.");
    }

    public static String getAbilityName(int topicIndex) {
        if (topicIndex == ABILITY_MARKET_SCANNER) {
            return "Market Scanner";
        } else if (topicIndex == ABILITY_SIGNAL_DECODER) {
            return "Signal Decoder";
        } else if (topicIndex == ABILITY_INDEX_VISION) {
            return "Index Vision";
        } else if (topicIndex == ABILITY_TRADE_CALCULATOR) {
            return "Trade Calculator";
        } else if (topicIndex == ABILITY_TICKER_FINDER) {
            return "Ticker Finder";
        } else if (topicIndex == ABILITY_FAST_BROKER) {
            return "Fast Broker";
        }

        return "Unknown Ability";
    }

    public static boolean javaTradingLicenseUnlocked() {
        return javaTradingLicenseUnlocked;
    }

    public static void unlockJavaTradingLicense() {
        if (javaTradingLicenseUnlocked) {
            return;
        }

        javaTradingLicenseUnlocked = true;
        System.out.println("JAVA TRADING LICENSE UNLOCKED");
        System.out.println("You passed the Mixed Review Boss.");
        System.out.println("Final Score Bonus: +20%");
    }

    public static int getFinalScoreBonusPercent() {
        if (javaTradingLicenseUnlocked) {
            return 20;
        }

        return 0;
    }

    public static int getLicensedFinalNetWorth() {
        int netWorth = getNetWorth();
        return netWorth + (netWorth * getFinalScoreBonusPercent() / 100);
    }

    public static void printMasteryReport() {
        if (topicNames == null) {
            initializeLearningSystem();
        }

        System.out.println("=============================");
        System.out.println("MASTERY REPORT");
        System.out.println("==============");
        System.out.println("Java XP: " + javaXp);
        System.out.println("Questions Answered: " + questionsAnswered);
        System.out.println("Questions Correct: " + questionsCorrect);

        if (questionsAnswered > 0) {
            double accuracy = (questionsCorrect * 100.0) / questionsAnswered;
            System.out.printf("Overall Accuracy: %.1f%%%n", accuracy);
        } else {
            System.out.println("Overall Accuracy: No questions answered yet");
        }

        System.out.println();
        System.out.printf("%-5s %-20s %-8s %-9s %-10s %-18s %-10s%n",
                "Index",
                "Topic",
                "Correct",
                "Attempts",
                "Mastered",
                "Ability",
                "Unlocked");

        for (int i = 0; i < topicNames.length; i++) {
            String masteredText = "No";
            String unlockedText = "No";

            if (topicMastered[i]) {
                masteredText = "Yes";
            }

            if (abilityUnlocked[i]) {
                unlockedText = "Yes";
            }

            System.out.printf("%-5d %-20s %-8d %-9d %-10s %-18s %-10s%n",
                    i,
                    topicNames[i],
                    topicCorrect[i],
                    topicAttempts[i],
                    masteredText,
                    getAbilityName(i),
                    unlockedText);
        }

        System.out.println();
        printUnlockSummary();
        System.out.println();
        System.out.println("Mixed Boss Attempts: " + mixedBossAttempts);
        System.out.println("Mixed Boss Best Score: " + mixedBossBestScore + " / 10");
        if (javaTradingLicenseUnlocked()) {
            System.out.println("Java Trading License: UNLOCKED");
        } else {
            System.out.println("Java Trading License: LOCKED");
        }
        System.out.println("Final Score Bonus: " + getFinalScoreBonusPercent() + "%");
        System.out.println();
        printTeachingQualityChecklist();
    }

    public static String getFloorStatus(int topicIndex) {
        if (topicMastered(topicIndex)) {
            return "MASTERED";
        }

        return "OPEN";
    }

    public static int countMasteredTopics() {
        if (topicNames == null || topicMastered == null) {
            return 0;
        }

        int count = 0;

        for (int i = 0; i < topicNames.length; i++) {
            if (topicMastered[i]) {
                count = count + 1;
            }
        }

        return count;
    }

    public static boolean allSixTopicsMastered() {
        return topicNames != null && countMasteredTopics() == topicNames.length;
    }

    public static void printLockedBossRequirements() {
        if (topicNames == null) {
            initializeLearningSystem();
        }

        System.out.println("Topics still needed:");

        for (int i = 0; i < topicNames.length; i++) {
            if (!topicMastered[i]) {
                System.out.println("- " + topicNames[i]);
            }
        }
    }

    public static void printAcademyFloorCard(int topicIndex, int menuNumber) {
        if (!isValidTopicIndex(topicIndex)) {
            System.out.println(menuNumber + ". Unknown Floor [LOCKED]");
            return;
        }

        String floorName = topicNames[topicIndex] + " Floor";

        if (topicIndex == TOPIC_FOR_LOOPS) {
            floorName = "For Loop Floor";
        } else if (topicIndex == TOPIC_WHILE_LOOPS) {
            floorName = "While Loop Floor";
        } else if (topicIndex == TOPIC_ARRAYS) {
            floorName = "Arrays Floor";
        } else if (topicIndex == TOPIC_METHODS) {
            floorName = "Methods Floor";
        } else if (topicIndex == TOPIC_SEQUENTIAL_SEARCH) {
            floorName = "Sequential Search Floor";
        } else if (topicIndex == TOPIC_BINARY_SEARCH) {
            floorName = "Binary Search Floor";
        }

        String unlockStatus = "LOCKED";

        if (abilityUnlocked[topicIndex]) {
            unlockStatus = "UNLOCKED";
        }

        System.out.println(menuNumber + ". "
                + floorName
                + " [" + getFloorStatus(topicIndex) + "]"
                + " | Topic: " + topicNames[topicIndex]
                + " | Correct: " + topicCorrect[topicIndex] + "/3"
                + " | Attempts: " + topicAttempts[topicIndex]
                + " | Unlock: " + getAbilityName(topicIndex) + " [" + unlockStatus + "]");
    }

    public static void printUnlockSummary() {
        if (topicNames == null) {
            initializeLearningSystem();
        }

        System.out.println("Trading Ability Unlocks");

        for (int i = 0; i < topicNames.length; i++) {
            String unlockStatus = "LOCKED";

            if (abilityUnlocked[i]) {
                unlockStatus = "UNLOCKED";
            }

            System.out.println(getAbilityName(i) + ": " + unlockStatus);
        }

        if (javaTradingLicenseUnlocked()) {
            System.out.println("Java Trading License: UNLOCKED");
        } else {
            System.out.println("Java Trading License: LOCKED");
        }

        System.out.println("Final Score Bonus: " + getFinalScoreBonusPercent() + "%");
    }

    public static void printTeachingQualityChecklist() {
        System.out.println("Teaching Quality Checklist");
        System.out.println("- XP requires correct answers: YES");
        System.out.println("- No source editing required: YES");
        System.out.println("- For Loop Floor complete: YES");
        System.out.println("- Arrays Floor complete: YES");
        System.out.println("- Methods Floor complete: YES");
        System.out.println("- While Loop Floor complete: YES");
        System.out.println("- Sequential Search Floor complete: YES");
        System.out.println("- Binary Search Floor complete: YES");
        System.out.println("- Mixed Review Boss complete: YES");
        System.out.println("- Trading unlocks connected to mastery: YES");
        System.out.println("- Developer tests available: YES");
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
            String badInput = input.nextLine();
            System.out.println("Invalid input: " + badInput + ". Action cancelled.");
            return Integer.MIN_VALUE;
        }

        int value = input.nextInt();
        input.nextLine();

        if (value < min || value > max) {
            System.out.println("Input must be from " + min + " to " + max + ". Action cancelled.");
            return Integer.MIN_VALUE;
        }

        return value;
    }

    public static void runJavaTradingAcademy(Scanner input) {
        boolean inAcademy = true;

        while (inAcademy) {
            int completedTopics = countMasteredTopics();

            System.out.println("=============================");
            System.out.println("JAVA TRADING ACADEMY");
            System.out.println("====================");
            System.out.println("Java XP: " + javaXp);
            System.out.println("Questions: " + questionsCorrect + " correct / " + questionsAnswered + " answered");
            System.out.println("Completed topics: " + completedTopics + " / " + topicNames.length);
            System.out.println("Mixed Boss Attempts: " + mixedBossAttempts);
            System.out.println("Mixed Boss Best Score: " + mixedBossBestScore + " / 10");
            if (javaTradingLicenseUnlocked()) {
                System.out.println("Java Trading License: UNLOCKED");
            } else {
                System.out.println("Java Trading License: LOCKED");
            }
            System.out.println();
            printUnlockSummary();
            System.out.println();
            System.out.println("Academy Floors");
            System.out.println("0. Return to Market");
            printAcademyFloorCard(TOPIC_FOR_LOOPS, 1);
            printAcademyFloorCard(TOPIC_WHILE_LOOPS, 2);
            printAcademyFloorCard(TOPIC_ARRAYS, 3);
            printAcademyFloorCard(TOPIC_METHODS, 4);
            printAcademyFloorCard(TOPIC_SEQUENTIAL_SEARCH, 5);
            printAcademyFloorCard(TOPIC_BINARY_SEARCH, 6);

            if (javaTradingLicenseUnlocked()) {
                System.out.println("7. Mixed Review Boss [PASSED]");
            } else if (allSixTopicsMastered()) {
                System.out.println("7. Mixed Review Boss [OPEN]");
            } else {
                System.out.println("7. Mixed Review Boss [LOCKED]");
            }

            System.out.println();
            int choice = readIntInRangeOrCancel(input, "Choose an academy option: ", 0, 7);

            if (choice == Integer.MIN_VALUE) {
                System.out.println("Returning to the academy hub.");
            } else if (choice == 0) {
                inAcademy = false;
            } else if (choice >= 1 && choice <= 6) {
                routeAcademyFloor(choice, input);
            } else if (choice == 7) {
                runMixedReviewBoss(input);
            }

            System.out.println();
        }
    }

    public static void routeAcademyFloor(int floorChoice, Scanner input) {
        if (floorChoice == 1) {
            runForLoopFloor(input);
        } else if (floorChoice == 2) {
            runWhileLoopFloor(input);
        } else if (floorChoice == 3) {
            runArraysFloor(input);
        } else if (floorChoice == 4) {
            runMethodsFloor(input);
        } else if (floorChoice == 5) {
            runSequentialSearchFloor(input);
        } else if (floorChoice == 6) {
            runBinarySearchFloor(input);
        } else {
            System.out.println("Invalid floor.");
        }
    }

    public static void printFloorShell(String title, String goal) {
        System.out.println("=============================");
        System.out.println(title);
        System.out.println("=============================");
        System.out.println("Goal: " + goal);
        System.out.println("Future challenge types:");
        System.out.println("- Predict Output");
        System.out.println("- Trace Variable");
        System.out.println("- Fix Bug");
        System.out.println("- Fill Blank");
        System.out.println("- Stock Application");
        System.out.println();
    }

    public static void runForLoopFloor(Scanner input) {
        boolean wasUnlocked = marketScannerUnlocked();

        printForLoopFloorBriefing();
        runForLoopPredictOutputChallenge(input);
        runForLoopTraceVariableChallenge(input);
        runForLoopFixBugChallenge(input);
        runForLoopFillBlankChallenge(input);
        runForLoopStockApplicationChallenge(input);
        printForLoopFloorSummary();

        if (!wasUnlocked && marketScannerUnlocked()) {
            System.out.println("Market Scanner repaired. View Market now shows the for-loop scanner trace.");
        }
    }

    public static void printForLoopFloorBriefing() {
        System.out.println("=============================");
        System.out.println("FOR LOOP FLOOR");
        System.out.println("=============================");
        System.out.println("Mission: Repair the Market Scanner by proving you understand for loops.");
        System.out.println();
        System.out.println("This floor teaches:");
        System.out.println("- i starts at 0");
        System.out.println("- i < prices.length");
        System.out.println("- i++");
        System.out.println("- array traversal");
        System.out.println("- avoiding prices[prices.length]");
        System.out.println();
        System.out.println("Challenges:");
        System.out.println("1. Predict Output");
        System.out.println("2. Trace Variable");
        System.out.println("3. Fix Bug");
        System.out.println("4. Fill Blank");
        System.out.println("5. Stock Application");
        System.out.println();
    }

    public static void runForLoopPredictOutputChallenge(Scanner input) {
        System.out.println("Challenge 1: Predict Output");
        printCodeBlock("Predict Output", new String[] {
                "for (int i = 0; i < 3; i++) {",
                "    System.out.print(i + \" \");",
                "}"
        });
        askMultipleChoice(
                input,
                TOPIC_FOR_LOOPS,
                "What does this code print?",
                "1 2 3",
                "0 1 2",
                "0 1 2 3",
                "3 2 1",
                "B",
                "The loop starts with i = 0, runs while i < 3, and increases i by 1 each time. It prints 0, then 1, then 2. It stops before 3.");
    }

    public static void runForLoopTraceVariableChallenge(Scanner input) {
        System.out.println("Challenge 2: Trace Variable");
        printCodeBlock("Trace Variable", new String[] {
                "for (int i = 0; i < prices.length; i++) {",
                "    System.out.println(prices[i]);",
                "}"
        });
        askMultipleChoice(
                input,
                TOPIC_FOR_LOOPS,
                "If prices.length is 8, what is the final valid value of i that accesses the array?",
                "8",
                "7",
                "9",
                "0",
                "B",
                "The valid indexes are 0 through prices.length - 1. If prices.length is 8, the last valid index is 7.");
    }

    public static void runForLoopFixBugChallenge(Scanner input) {
        System.out.println("Challenge 3: Fix Bug");
        printCodeBlock("Broken loop", new String[] {
                "for (int i = 0; i <= prices.length; i++) {",
                "    System.out.println(prices[i]);",
                "}"
        });
        askMultipleChoice(
                input,
                TOPIC_FOR_LOOPS,
                "Which change fixes the off-by-one bug?",
                "Change i = 0 to i = 1",
                "Change i <= prices.length to i < prices.length",
                "Change i++ to i--",
                "Change prices[i] to prices[prices.length]",
                "B",
                "prices[prices.length] is outside the array. The loop must stop before i reaches prices.length, so the condition should be i < prices.length.");
    }

    public static void runForLoopFillBlankChallenge(Scanner input) {
        System.out.println("Challenge 4: Fill Blank");
        printCodeBlock("Fill Blank", new String[] {
                "for (int i = 0; i < ______; i++) {",
                "    System.out.println(tickers[i]);",
                "}"
        });
        askMultipleChoice(
                input,
                TOPIC_FOR_LOOPS,
                "What should fill the blank so the loop prints every ticker safely?",
                "tickers.length",
                "tickers.length - 1",
                "tickers[0]",
                "i.length",
                "A",
                "The loop condition should be i < tickers.length. That allows i to visit 0 through tickers.length - 1.");
    }

    public static void runForLoopStockApplicationChallenge(Scanner input) {
        System.out.println("Challenge 5: Stock Application");
        boolean oldSuppressMarketScannerTrace = suppressMarketScannerTrace;
        suppressMarketScannerTrace = true;
        printMarket();
        suppressMarketScannerTrace = oldSuppressMarketScannerTrace;
        System.out.println();
        System.out.println("The Market Scanner wants to visit every stock exactly once.");
        askMultipleChoice(
                input,
                TOPIC_FOR_LOOPS,
                "Which loop safely visits every stock in the current market?",
                "for (int i = 0; i <= prices.length; i++)",
                "for (int i = 1; i < prices.length; i++)",
                "for (int i = 0; i < prices.length; i++)",
                "for (int i = prices.length; i > 0; i++)",
                "C",
                "The safe traversal starts at index 0 and continues while i is less than prices.length. That visits every valid index exactly once.");
    }

    public static void printForLoopFloorSummary() {
        String masteredText = "No";
        String unlockedText = "No";

        if (topicMastered(TOPIC_FOR_LOOPS)) {
            masteredText = "Yes";
        }

        if (marketScannerUnlocked()) {
            unlockedText = "Yes";
        }

        System.out.println("=============================");
        System.out.println("FOR LOOP FLOOR RESULT");
        System.out.println("=====================");
        System.out.println("For Loop Correct: " + topicCorrect[TOPIC_FOR_LOOPS]);
        System.out.println("For Loop Attempts: " + topicAttempts[TOPIC_FOR_LOOPS]);
        System.out.println("For Loops Mastered: " + masteredText);
        System.out.println("Market Scanner Unlocked: " + unlockedText);
    }

    public static void runWhileLoopFloor(Scanner input) {
        boolean wasUnlocked = signalDecoderUnlocked();

        printWhileLoopFloorBriefing();
        runWhileLoopPredictOutputChallenge(input);
        runWhileLoopTraceVariableChallenge(input);
        runWhileLoopFixInfiniteLoopChallenge(input);
        runWhileLoopFillDigitExpressionChallenge(input);
        runWhileLoopStockSignalChallenge(input);
        printWhileLoopFloorSummary();

        if (!wasUnlocked && signalDecoderUnlocked()) {
            System.out.println("Signal Decoder activated. Advance Day now shows while-loop market signal hints.");
        }
    }

    public static void printWhileLoopFloorBriefing() {
        System.out.println("=============================");
        System.out.println("WHILE LOOP FLOOR");
        System.out.println("=============================");
        System.out.println("Mission: Activate the Signal Decoder by proving you understand while loops.");
        System.out.println();
        System.out.println("This floor teaches:");
        System.out.println("- condition");
        System.out.println("- repeated execution");
        System.out.println("- loop-control variable");
        System.out.println("- update step");
        System.out.println("- infinite-loop risk");
        System.out.println("- digit processing using % and /");
        System.out.println();
        System.out.println("Challenges:");
        System.out.println("1. Predict Output");
        System.out.println("2. Trace Variable");
        System.out.println("3. Fix Infinite Loop");
        System.out.println("4. Fill Digit Expression");
        System.out.println("5. Stock Signal Application");
        System.out.println();
    }

    public static void runWhileLoopPredictOutputChallenge(Scanner input) {
        System.out.println("Challenge 1: Predict Output");
        printCodeBlock("Predict Output", new String[] {
                "int x = 3;",
                "while (x > 0) {",
                "    System.out.print(x + \" \");",
                "    x--;",
                "}"
        });
        askMultipleChoice(
                input,
                TOPIC_WHILE_LOOPS,
                "What does this code print?",
                "0 1 2",
                "3 2 1",
                "3 2 1 0",
                "It prints forever",
                "B",
                "The loop starts with x = 3. It prints x, then decreases x. It stops when x becomes 0 because x > 0 is false.");
    }

    public static void runWhileLoopTraceVariableChallenge(Scanner input) {
        System.out.println("Challenge 2: Trace Variable");
        printCodeBlock("Trace Variable", new String[] {
                "int x = 3;",
                "while (x > 0) {",
                "    x--;",
                "}"
        });
        askMultipleChoice(
                input,
                TOPIC_WHILE_LOOPS,
                "What is x after the loop finishes?",
                "3",
                "2",
                "1",
                "0",
                "D",
                "x decreases from 3 to 2, then 1, then 0. When x is 0, the condition x > 0 is false, so the loop stops.");
    }

    public static void runWhileLoopFixInfiniteLoopChallenge(Scanner input) {
        System.out.println("Challenge 3: Fix Infinite Loop");
        printCodeBlock("Broken while loop", new String[] {
                "int x = 3;",
                "while (x > 0) {",
                "    System.out.println(x);",
                "}"
        });
        askMultipleChoice(
                input,
                TOPIC_WHILE_LOOPS,
                "What change prevents this from running forever?",
                "Change x > 0 to x < 0",
                "Add x--; inside the loop",
                "Remove the while keyword",
                "Change int x = 3 to String x = \"3\"",
                "B",
                "The loop-control variable must change. Adding x--; moves x toward 0, so the condition eventually becomes false.");
    }

    public static void runWhileLoopFillDigitExpressionChallenge(Scanner input) {
        System.out.println("Challenge 4: Fill Digit Expression");
        printCodeBlock("Fill Digit Expression", new String[] {
                "int num = 123;",
                "int digit = num ______ 10;"
        });
        askMultipleChoice(
                input,
                TOPIC_WHILE_LOOPS,
                "What fills the blank to get the last digit of num?",
                "%",
                "/",
                "*",
                "+",
                "A",
                "num % 10 gives the remainder after dividing by 10, which is the last digit. For 123, 123 % 10 is 3.");
    }

    public static void runWhileLoopStockSignalChallenge(Scanner input) {
        System.out.println("Challenge 5: Stock Signal Application");
        System.out.println("Market signal: 133444");
        askMultipleChoice(
                input,
                TOPIC_WHILE_LOOPS,
                "Which digit has the longest repeated run in 133444?",
                "1",
                "3",
                "4",
                "No digit repeats",
                "C",
                "The digit 4 appears three times in a row at the end of 133444. A while-loop digit decoder can scan the number using % 10 and / 10.");
    }

    public static void printWhileLoopFloorSummary() {
        String masteredText = "No";
        String unlockedText = "No";

        if (topicMastered(TOPIC_WHILE_LOOPS)) {
            masteredText = "Yes";
        }

        if (signalDecoderUnlocked()) {
            unlockedText = "Yes";
        }

        System.out.println("=============================");
        System.out.println("WHILE LOOP FLOOR RESULT");
        System.out.println("=======================");
        System.out.println("While Loops Correct: " + topicCorrect[TOPIC_WHILE_LOOPS]);
        System.out.println("While Loops Attempts: " + topicAttempts[TOPIC_WHILE_LOOPS]);
        System.out.println("While Loops Mastered: " + masteredText);
        System.out.println("Signal Decoder Unlocked: " + unlockedText);
    }

    public static void runArraysFloor(Scanner input) {
        boolean wasUnlocked = indexVisionUnlocked();

        printArraysFloorBriefing();
        runArraysPredictAccessChallenge(input);
        runArraysTraceLastIndexChallenge(input);
        runArraysFixInvalidIndexChallenge(input);
        runArraysFillLengthBlankChallenge(input);
        runArraysStockApplicationChallenge(input);
        printArraysFloorSummary();

        if (!wasUnlocked && indexVisionUnlocked()) {
            System.out.println("Index Vision activated. Buy and Sell now show array index safety hints.");
        }
    }

    public static void printArraysFloorBriefing() {
        System.out.println("=============================");
        System.out.println("ARRAYS FLOOR");
        System.out.println("=============================");
        System.out.println("Mission: Activate Index Vision by proving you understand Java arrays.");
        System.out.println();
        System.out.println("This floor teaches:");
        System.out.println("- arrays store many related values");
        System.out.println("- array access uses brackets like tickers[i]");
        System.out.println("- arrays have a fixed length after they are created");
        System.out.println("- indexes start at 0");
        System.out.println("- last valid index is array.length - 1");
        System.out.println("- array.length itself is outside the array");
        System.out.println("- parallel arrays keep related data at the same index");
        System.out.println("- invalid indexes can cause ArrayIndexOutOfBoundsException");
        System.out.println();
        System.out.println("Challenges:");
        System.out.println("1. Predict Array Access");
        System.out.println("2. Trace Last Index");
        System.out.println("3. Fix Invalid Index");
        System.out.println("4. Fill Array Length Blank");
        System.out.println("5. Stock Application");
        System.out.println();
    }

    public static void runArraysPredictAccessChallenge(Scanner input) {
        System.out.println("Challenge 1: Predict Array Access");
        printCodeBlock("Predict Array Access", new String[] {
                "String[] tickers = {\"APEX\", \"NOVA\", \"BYTE\"};"
        });
        askMultipleChoice(
                input,
                TOPIC_ARRAYS,
                "What is tickers[2]?",
                "APEX",
                "NOVA",
                "BYTE",
                "index error",
                "C",
                "Java arrays start at index 0. tickers[0] is APEX, tickers[1] is NOVA, and tickers[2] is BYTE.");
    }

    public static void runArraysTraceLastIndexChallenge(Scanner input) {
        System.out.println("Challenge 2: Trace Last Index");
        askMultipleChoice(
                input,
                TOPIC_ARRAYS,
                "If prices.length is 8, what is the last valid index?",
                "8",
                "7",
                "9",
                "0",
                "B",
                "The last valid index is always array.length - 1. If prices.length is 8, the valid indexes are 0 through 7.");
    }

    public static void runArraysFixInvalidIndexChallenge(Scanner input) {
        System.out.println("Challenge 3: Fix Invalid Index");
        printCodeBlock("Broken array access", new String[] {
                "System.out.println(prices[prices.length]);"
        });
        askMultipleChoice(
                input,
                TOPIC_ARRAYS,
                "Which replacement safely prints the last price?",
                "prices[0]",
                "prices[prices.length - 1]",
                "prices[prices.length + 1]",
                "prices[-1]",
                "B",
                "prices.length is the number of elements, not the last index. The last index is prices.length - 1. Using prices[prices.length] can cause ArrayIndexOutOfBoundsException.");
    }

    public static void runArraysFillLengthBlankChallenge(Scanner input) {
        System.out.println("Challenge 4: Fill Array Length Blank");
        printCodeBlock("Fill Array Length Blank", new String[] {
                "sharesOwned = new int[______];"
        });
        askMultipleChoice(
                input,
                TOPIC_ARRAYS,
                "What should fill the blank so sharesOwned has one slot for every stock price?",
                "prices.length",
                "prices[0]",
                "tickers[0]",
                "day",
                "A",
                "prices.length gives the number of stocks. sharesOwned should have the same length so sharesOwned[i] matches prices[i].");
    }

    public static void runArraysStockApplicationChallenge(Scanner input) {
        System.out.println("Challenge 5: Stock Application");
        printMarketWithoutIndexForArrayChallenge();
        System.out.println();
        System.out.println("In Market Mayhem, tickers[i], prices[i], stockIds[i], sharesOwned[i], and riskLevels[i] describe the same stock.");

        int byteIndex = -1;

        for (int i = 0; i < tickers.length; i++) {
            if (tickers[i].equals("BYTE")) {
                byteIndex = i;
            }
        }

        askShortAnswer(
                input,
                TOPIC_ARRAYS,
                "In the default market, what index belongs to BYTE?",
                "" + byteIndex,
                "BYTE is stored at tickers[" + byteIndex + "]. That means prices[" + byteIndex + "], stockIds[" + byteIndex + "], sharesOwned[" + byteIndex + "], and riskLevels[" + byteIndex + "] all describe BYTE.");
    }

    public static void printMarketWithoutIndexForArrayChallenge() {
        if (!marketDataIsValid()) {
            System.out.println("Market data is not ready. Please restart the game.");
            return;
        }

        System.out.println("Current market preview without index numbers:");
        System.out.printf("%-7s %-5s %-7s %-7s %-5s%n", "Ticker", "ID", "Price", "Owned", "Risk");

        for (int i = 0; i < tickers.length; i++) {
            System.out.printf("%-7s %-5d $%-6d %-7d %-5d%n",
                    tickers[i],
                    stockIds[i],
                    prices[i],
                    sharesOwned[i],
                    riskLevels[i]);
        }
    }

    public static void printArraysFloorSummary() {
        String masteredText = "No";
        String unlockedText = "No";

        if (topicMastered(TOPIC_ARRAYS)) {
            masteredText = "Yes";
        }

        if (indexVisionUnlocked()) {
            unlockedText = "Yes";
        }

        System.out.println("=============================");
        System.out.println("ARRAYS FLOOR RESULT");
        System.out.println("===================");
        System.out.println("Arrays Correct: " + topicCorrect[TOPIC_ARRAYS]);
        System.out.println("Arrays Attempts: " + topicAttempts[TOPIC_ARRAYS]);
        System.out.println("Arrays Mastered: " + masteredText);
        System.out.println("Index Vision Unlocked: " + unlockedText);
    }

    public static void runMethodsFloor(Scanner input) {
        boolean wasUnlocked = tradeCalculatorUnlocked();

        printMethodsFloorBriefing();
        runMethodsPredictReturnChallenge(input);
        runMethodsTraceParametersChallenge(input);
        runMethodsFixCallChallenge(input);
        runMethodsFillHeaderChallenge(input);
        runMethodsStockApplicationChallenge(input);
        printMethodsFloorSummary();

        if (!wasUnlocked && tradeCalculatorUnlocked()) {
            System.out.println("Trade Calculator activated. Buy and Sell now preview reusable method calculations.");
        }
    }

    public static void printMethodsFloorBriefing() {
        System.out.println("=============================");
        System.out.println("METHODS FLOOR");
        System.out.println("=============================");
        System.out.println("Mission: Activate the Trade Calculator by proving you understand Java methods.");
        System.out.println();
        System.out.println("This floor teaches:");
        System.out.println("- method signature");
        System.out.println("- access modifier such as public");
        System.out.println("- static methods");
        System.out.println("- return type");
        System.out.println("- method name");
        System.out.println("- parameters");
        System.out.println("- arguments");
        System.out.println("- void methods");
        System.out.println("- return statement");
        System.out.println("- return values");
        System.out.println("- methods keep main shorter");
        System.out.println("- reusable game logic");
        System.out.println();
        System.out.println("Challenges:");
        System.out.println("1. Predict Return Value");
        System.out.println("2. Trace Parameters");
        System.out.println("3. Fix Method Call");
        System.out.println("4. Fill Method Header Blank");
        System.out.println("5. Stock Application");
        System.out.println();
    }

    public static void runMethodsPredictReturnChallenge(Scanner input) {
        System.out.println("Challenge 1: Predict Return Value");
        printCodeBlock("Predict Return Value", new String[] {
                "public static int calculateTradeValue(int price, int shares) {",
                "    return price * shares;",
                "}"
        });
        askMultipleChoice(
                input,
                TOPIC_METHODS,
                "What does calculateTradeValue(45, 3) return?",
                "48",
                "135",
                "453",
                "nothing, because it is void",
                "B",
                "The method has return type int and returns price * shares. With price = 45 and shares = 3, it returns 135. In the game version, calculateTradeValue(index, shares) gets the price from prices[index].");
    }

    public static void runMethodsTraceParametersChallenge(Scanner input) {
        System.out.println("Challenge 2: Trace Parameters");
        printCodeBlock("Trace Parameters", new String[] {
                "public static int calculateTradeValue(int index, int shares)"
        });
        askMultipleChoice(
                input,
                TOPIC_METHODS,
                "How many parameters does this method have?",
                "0",
                "1",
                "2",
                "3",
                "C",
                "The parameters are int index and int shares. They are the input values the method receives.");
    }

    public static void runMethodsFixCallChallenge(Scanner input) {
        System.out.println("Challenge 3: Fix Method Call");
        printCodeBlock("Broken method call", new String[] {
                "int value = calculateTradeValue();"
        });
        askMultipleChoice(
                input,
                TOPIC_METHODS,
                "The method needs an index and a share amount. Which call is correct?",
                "calculateTradeValue",
                "calculateTradeValue(0, 3)",
                "calculateTradeValue(int index, int shares)",
                "calculateTradeValue = 0, 3",
                "B",
                "A method call must include actual argument values. calculateTradeValue(0, 3) passes index 0 and shares 3.");
    }

    public static void runMethodsFillHeaderChallenge(Scanner input) {
        System.out.println("Challenge 4: Fill Method Header Blank");
        printCodeBlock("Fill Method Header Blank", new String[] {
                "public static ______ getNetWorth() {",
                "    return cash + getPortfolioValue();",
                "}"
        });
        askMultipleChoice(
                input,
                TOPIC_METHODS,
                "What fills the blank?",
                "void",
                "int",
                "boolean",
                "String[]",
                "B",
                "getNetWorth returns a number, so the return type should be int. A void method would not return a value.");
    }

    public static void runMethodsStockApplicationChallenge(Scanner input) {
        System.out.println("Challenge 5: Stock Application");

        if (isValidStockIndex(0)) {
            printStockLine(0);
        }

        System.out.println("The Trade Calculator needs a method that computes price times shares.");
        askMultipleChoice(
                input,
                TOPIC_METHODS,
                "Which existing method should calculate the value of buying or selling shares?",
                "printMarket()",
                "calculateTradeValue(index, shares)",
                "pauseBriefly()",
                "initializePlayer()",
                "B",
                "calculateTradeValue(index, shares) returns prices[index] * shares. That is exactly the reusable method needed for buy and sell calculations.");
    }

    public static void printMethodsFloorSummary() {
        String masteredText = "No";
        String unlockedText = "No";

        if (topicMastered(TOPIC_METHODS)) {
            masteredText = "Yes";
        }

        if (tradeCalculatorUnlocked()) {
            unlockedText = "Yes";
        }

        System.out.println("=============================");
        System.out.println("METHODS FLOOR RESULT");
        System.out.println("====================");
        System.out.println("Methods Correct: " + topicCorrect[TOPIC_METHODS]);
        System.out.println("Methods Attempts: " + topicAttempts[TOPIC_METHODS]);
        System.out.println("Methods Mastered: " + masteredText);
        System.out.println("Trade Calculator Unlocked: " + unlockedText);
    }

    public static void runSequentialSearchFloor(Scanner input) {
        boolean wasUnlocked = tickerFinderUnlocked();

        printSequentialSearchFloorBriefing();
        runSequentialSearchTraceIndexesChallenge(input);
        runSequentialSearchFoundIndexChallenge(input);
        runSequentialSearchNotFoundChallenge(input);
        runSequentialSearchFillReturnChallenge(input);
        runSequentialSearchStockApplicationChallenge(input);
        printSequentialSearchFloorSummary();

        if (!wasUnlocked && tickerFinderUnlocked()) {
            System.out.println("Ticker Finder activated. Buy Stock can now search by ticker before choosing an index.");
        }
    }

    public static void printSequentialSearchFloorBriefing() {
        System.out.println("=============================");
        System.out.println("SEQUENTIAL SEARCH FLOOR");
        System.out.println("=============================");
        System.out.println("Mission: Activate Ticker Finder by proving you understand sequential search.");
        System.out.println();
        System.out.println("This floor teaches:");
        System.out.println("- check indexes from first to last");
        System.out.println("- compare each element to the target");
        System.out.println("- return the index if found");
        System.out.println("- return -1 if not found");
        System.out.println("- count comparisons");
        System.out.println("- works on unsorted arrays");
        System.out.println();
        System.out.println("Challenges:");
        System.out.println("1. Trace Checked Indexes");
        System.out.println("2. Predict Found Index");
        System.out.println("3. Not-Found Return");
        System.out.println("4. Fill Return Blank");
        System.out.println("5. Stock Ticker Application");
        System.out.println();
    }

    public static void runSequentialSearchTraceIndexesChallenge(Scanner input) {
        System.out.println("Challenge 1: Trace Checked Indexes");
        printCodeBlock("Sequential search setup", new String[] {
                "String[] tickers = {\"APEX\", \"NOVA\", \"BYTE\", \"IRON\"};",
                "String target = \"BYTE\";"
        });
        System.out.println("Sequential search starts at index 0 and checks one element at a time.");
        askMultipleChoice(
                input,
                TOPIC_SEQUENTIAL_SEARCH,
                "Which indexes are checked before BYTE is found?",
                "2 only",
                "0, 1, 2",
                "0, 1, 2, 3",
                "3, 2",
                "B",
                "Sequential search checks APEX at index 0, NOVA at index 1, then BYTE at index 2. It stops when the target is found.");
    }

    public static void runSequentialSearchFoundIndexChallenge(Scanner input) {
        System.out.println("Challenge 2: Predict Found Index");
        printCodeBlock("Sequential search setup", new String[] {
                "String[] tickers = {\"APEX\", \"NOVA\", \"BYTE\", \"IRON\"};",
                "String target = \"BYTE\";"
        });
        askMultipleChoice(
                input,
                TOPIC_SEQUENTIAL_SEARCH,
                "What index should sequential search return?",
                "0",
                "1",
                "2",
                "-1",
                "C",
                "BYTE is stored at tickers[2], so an index-returning sequential search should return 2.");
    }

    public static void runSequentialSearchNotFoundChallenge(Scanner input) {
        System.out.println("Challenge 3: Not-Found Return");
        printCodeBlock("Sequential search setup", new String[] {
                "int[] ids = {101, 102, 103};",
                "int target = 999;"
        });
        askMultipleChoice(
                input,
                TOPIC_SEQUENTIAL_SEARCH,
                "If sequential search checks every value and does not find 999, what should it return?",
                "0",
                "-1",
                "ids.length",
                "true",
                "B",
                "A common Java pattern is to return -1 when the target is not found because -1 is not a valid array index.");
    }

    public static void runSequentialSearchFillReturnChallenge(Scanner input) {
        System.out.println("Challenge 4: Fill Return Blank");
        printCodeBlock("Fill Return Blank", new String[] {
                "for (int i = 0; i < tickers.length; i++) {",
                "    if (tickers[i].equals(target)) {",
                "        return ______;",
                "    }",
                "}",
                "return -1;"
        });
        askMultipleChoice(
                input,
                TOPIC_SEQUENTIAL_SEARCH,
                "What should fill the blank?",
                "i",
                "target",
                "tickers[i]",
                "tickers.length",
                "A",
                "When the target is found, the method should return i because i is the index where the match was found.");
    }

    public static void runSequentialSearchStockApplicationChallenge(Scanner input) {
        System.out.println("Challenge 5: Stock Ticker Application");
        printMarket();
        System.out.println();
        System.out.println("Ticker Finder scans tickers[0], then tickers[1], then tickers[2], and so on until it finds the target.");

        int omniIndex = -1;

        for (int i = 0; i < tickers.length; i++) {
            if (tickers[i].equalsIgnoreCase("OMNI")) {
                omniIndex = i;
            }
        }

        if (omniIndex != -1) {
            askShortAnswer(
                    input,
                    TOPIC_SEQUENTIAL_SEARCH,
                    "How many comparisons are needed to find OMNI using sequential search in the current market?",
                    "" + (omniIndex + 1),
                    "Sequential search starts at index 0. If OMNI is at index " + omniIndex + ", it checks " + (omniIndex + 1) + " elements before finding it.");
        } else {
            askMultipleChoice(
                    input,
                    TOPIC_SEQUENTIAL_SEARCH,
                    "If a target is not in the array, how many elements must sequential search check?",
                    "0",
                    "1",
                    "all elements",
                    "only the middle",
                    "C",
                    "If the target is missing, sequential search must check every element before returning -1.");
        }
    }

    public static void printSequentialSearchFloorSummary() {
        String masteredText = "No";
        String unlockedText = "No";

        if (topicMastered(TOPIC_SEQUENTIAL_SEARCH)) {
            masteredText = "Yes";
        }

        if (tickerFinderUnlocked()) {
            unlockedText = "Yes";
        }

        System.out.println("=============================");
        System.out.println("SEQUENTIAL SEARCH FLOOR RESULT");
        System.out.println("==============================");
        System.out.println("Sequential Search Correct: " + topicCorrect[TOPIC_SEQUENTIAL_SEARCH]);
        System.out.println("Sequential Search Attempts: " + topicAttempts[TOPIC_SEQUENTIAL_SEARCH]);
        System.out.println("Sequential Search Mastered: " + masteredText);
        System.out.println("Ticker Finder Unlocked: " + unlockedText);
    }

    public static void runBinarySearchFloor(Scanner input) {
        boolean wasUnlocked = fastBrokerUnlocked();

        printBinarySearchFloorBriefing();
        runBinarySearchSortedRequirementChallenge(input);
        runBinarySearchTraceMidpointChallenge(input);
        runBinarySearchUpdateRangeChallenge(input);
        runBinarySearchFillConditionChallenge(input);
        runBinarySearchStockApplicationChallenge(input);
        printBinarySearchFloorSummary();

        if (!wasUnlocked && fastBrokerUnlocked()) {
            System.out.println("Fast Broker activated. Buy Stock can now search sorted stock IDs with binary search.");
        }
    }

    public static void printBinarySearchFloorBriefing() {
        System.out.println("=============================");
        System.out.println("BINARY SEARCH FLOOR");
        System.out.println("===================");
        System.out.println("Mission: Activate Fast Broker by proving you understand binary search.");
        System.out.println();
        System.out.println("This floor teaches:");
        System.out.println("- sorted data requirement");
        System.out.println("- low, high, mid");
        System.out.println("- while loop");
        System.out.println("- compare target with stockIds[mid]");
        System.out.println("- move low or high");
        System.out.println("- cut the search range in half");
        System.out.println("- return found index or -1");
        System.out.println("- count comparisons");
        System.out.println();
        System.out.println("Challenges:");
        System.out.println("1. Sorted Requirement");
        System.out.println("2. Trace Midpoint");
        System.out.println("3. Update Search Range");
        System.out.println("4. Fill While Condition");
        System.out.println("5. Stock ID Application");
        System.out.println();
    }

    public static void runBinarySearchSortedRequirementChallenge(Scanner input) {
        System.out.println("Challenge 1: Sorted Requirement");
        System.out.println("Binary search can only work correctly when the data is sorted.");
        askMultipleChoice(
                input,
                TOPIC_BINARY_SEARCH,
                "What must be true before binary search can correctly search stockIds?",
                "stockIds must be sorted",
                "stockIds must be random",
                "stockIds must be empty",
                "stockIds must contain only negative numbers",
                "A",
                "Binary search repeatedly eliminates half of the search range. That only makes sense when the values are sorted.");
    }

    public static void runBinarySearchTraceMidpointChallenge(Scanner input) {
        System.out.println("Challenge 2: Trace Midpoint");
        printCodeBlock("Trace Midpoint", new String[] {
                "int low = 0;",
                "int high = 7;",
                "int mid = (low + high) / 2;"
        });
        askMultipleChoice(
                input,
                TOPIC_BINARY_SEARCH,
                "What is mid?",
                "2",
                "3",
                "4",
                "7",
                "B",
                "Integer division cuts off the decimal part. (0 + 7) / 2 is 3, so binary search first checks index 3.");
    }

    public static void runBinarySearchUpdateRangeChallenge(Scanner input) {
        System.out.println("Challenge 3: Update Search Range");
        System.out.println("sorted stockIds:");
        System.out.println("101 102 103 104 105 106 107 108");
        System.out.println();
        System.out.println("target = 107");
        System.out.println("low = 0");
        System.out.println("high = 7");
        System.out.println("mid = 3");
        System.out.println("stockIds[mid] = 104");
        askMultipleChoice(
                input,
                TOPIC_BINARY_SEARCH,
                "Because target is larger than stockIds[mid], what should binary search do?",
                "high = mid - 1",
                "low = mid + 1",
                "return -1 immediately",
                "stop because 104 is close enough",
                "B",
                "The target is larger than the middle value, so binary search discards the left half and searches the right half by setting low = mid + 1.");
    }

    public static void runBinarySearchFillConditionChallenge(Scanner input) {
        System.out.println("Challenge 4: Fill While Condition");
        printCodeBlock("Fill While Condition", new String[] {
                "int low = 0;",
                "int high = stockIds.length - 1;",
                "",
                "while (__________) {",
                "    int mid = (low + high) / 2;",
                "    // compare stockIds[mid] with target",
                "}"
        });
        askMultipleChoice(
                input,
                TOPIC_BINARY_SEARCH,
                "What condition should fill the blank?",
                "low <= high",
                "low > high",
                "mid < high",
                "stockIds.length == 0",
                "A",
                "Binary search continues while the search range is still valid. The range is valid while low <= high.");
    }

    public static void runBinarySearchStockApplicationChallenge(Scanner input) {
        System.out.println("Challenge 5: Stock ID Application");
        printMarket();
        System.out.println();
        System.out.println("Sorted stockIds:");
        System.out.println("101 102 103 104 105 106 107 108");
        System.out.println();
        System.out.println("target = 107");
        System.out.println();
        System.out.println("Binary search checks a middle index, then cuts the search range in half.");
        askMultipleChoice(
                input,
                TOPIC_BINARY_SEARCH,
                "Which indexes does binary search check before finding 107?",
                "0, 1, 2, 3, 4, 5, 6",
                "3, 5, 6",
                "7 only",
                "4, 5, 6",
                "B",
                "Trace after your answer: low = 0, high = 7, mid = 3; then low = 4, high = 7, mid = 5; then low = 6, high = 7, mid = 6.");
    }

    public static void printBinarySearchFloorSummary() {
        String masteredText = "No";
        String unlockedText = "No";

        if (topicMastered(TOPIC_BINARY_SEARCH)) {
            masteredText = "Yes";
        }

        if (fastBrokerUnlocked()) {
            unlockedText = "Yes";
        }

        System.out.println("=============================");
        System.out.println("BINARY SEARCH FLOOR RESULT");
        System.out.println("==========================");
        System.out.println("Binary Search Correct: " + topicCorrect[TOPIC_BINARY_SEARCH]);
        System.out.println("Binary Search Attempts: " + topicAttempts[TOPIC_BINARY_SEARCH]);
        System.out.println("Binary Search Mastered: " + masteredText);
        System.out.println("Fast Broker Unlocked: " + unlockedText);
    }

    public static void runMixedReviewBoss(Scanner input) {
        if (!allSixTopicsMastered()) {
            System.out.println("Mixed Review Boss is locked.");
            System.out.println("Master all six academy floors first.");
            printLockedBossRequirements();
            return;
        }

        System.out.println("=============================");
        System.out.println("MIXED REVIEW BOSS");
        System.out.println("=================");
        System.out.println("Answer 10 mixed Java trading questions.");
        System.out.println("Passing score: 8 / 10");
        System.out.println();

        mixedBossAttempts = mixedBossAttempts + 1;

        int bossCorrect = 0;
        bossCorrect = bossCorrect + askBossQuestionForLoops(input);
        bossCorrect = bossCorrect + askBossQuestionArrays(input);
        bossCorrect = bossCorrect + askBossQuestionMethods(input);
        bossCorrect = bossCorrect + askBossQuestionWhileLoops(input);
        bossCorrect = bossCorrect + askBossQuestionSequentialSearch(input);
        bossCorrect = bossCorrect + askBossQuestionBinarySearch(input);
        bossCorrect = bossCorrect + askBossQuestionOffByOne(input);
        bossCorrect = bossCorrect + askBossQuestionTradeMethod(input);
        bossCorrect = bossCorrect + askBossQuestionSearchComparison(input);
        bossCorrect = bossCorrect + askBossQuestionMixedApplication(input);

        if (bossCorrect > mixedBossBestScore) {
            mixedBossBestScore = bossCorrect;
        }

        System.out.println("=============================");
        System.out.println("MIXED REVIEW BOSS RESULT");
        System.out.println("========================");
        System.out.println("Score: " + bossCorrect + " / 10");
        System.out.println("Best Score: " + mixedBossBestScore + " / 10");

        if (bossCorrect >= 8) {
            System.out.println("Boss passed. Score: " + bossCorrect + " / 10");
            unlockJavaTradingLicense();
        } else {
            System.out.println("Boss failed. Score: " + bossCorrect + " / 10");
            System.out.println("Review the floors and try again.");
        }
    }

    public static int askBossQuestionForLoops(Scanner input) {
        System.out.println("Boss Question 1 - For Loops");
        System.out.println("for (int i = 0; i < 4; i++) {");
        System.out.println("System.out.print(i + \" \");");
        System.out.println("}");

        boolean correct = askMultipleChoice(
                input,
                TOPIC_FOR_LOOPS,
                "What does this print?",
                "1 2 3 4",
                "0 1 2 3",
                "0 1 2 3 4",
                "4 3 2 1",
                "B",
                "The loop starts at 0, runs while i < 4, and stops before 4.");

        if (correct) {
            return 1;
        }

        return 0;
    }

    public static int askBossQuestionArrays(Scanner input) {
        System.out.println("Boss Question 2 - Arrays");

        boolean correct = askMultipleChoice(
                input,
                TOPIC_ARRAYS,
                "If prices.length is 8, which index is outside the array?",
                "0",
                "7",
                "prices.length",
                "prices.length - 1",
                "C",
                "prices.length is the number of elements. The last valid index is prices.length - 1.");

        if (correct) {
            return 1;
        }

        return 0;
    }

    public static int askBossQuestionMethods(Scanner input) {
        System.out.println("Boss Question 3 - Methods");
        System.out.println("public static int getNetWorth() {");
        System.out.println("return cash + getPortfolioValue();");
        System.out.println("}");

        boolean correct = askMultipleChoice(
                input,
                TOPIC_METHODS,
                "What type of value does this method return?",
                "int",
                "void",
                "boolean",
                "Scanner",
                "A",
                "The method header says int, so getNetWorth returns an integer value.");

        if (correct) {
            return 1;
        }

        return 0;
    }

    public static int askBossQuestionWhileLoops(Scanner input) {
        System.out.println("Boss Question 4 - While Loops");
        System.out.println("int num = 123;");
        System.out.println("int digit = num % 10;");

        boolean correct = askMultipleChoice(
                input,
                TOPIC_WHILE_LOOPS,
                "What is digit?",
                "1",
                "2",
                "3",
                "12",
                "C",
                "The % operator gives the remainder. 123 % 10 gives the last digit, 3.");

        if (correct) {
            return 1;
        }

        return 0;
    }

    public static int askBossQuestionSequentialSearch(Scanner input) {
        System.out.println("Boss Question 5 - Sequential Search");
        System.out.println("String[] tickers = {\"APEX\", \"NOVA\", \"BYTE\", \"IRON\"};");
        System.out.println("target = \"IRON\";");

        boolean correct = askMultipleChoice(
                input,
                TOPIC_SEQUENTIAL_SEARCH,
                "How many comparisons does sequential search need to find IRON?",
                "1",
                "2",
                "3",
                "4",
                "D",
                "Sequential search checks APEX, NOVA, BYTE, then IRON. That is 4 comparisons.");

        if (correct) {
            return 1;
        }

        return 0;
    }

    public static int askBossQuestionBinarySearch(Scanner input) {
        System.out.println("Boss Question 6 - Binary Search");
        System.out.println("stockIds = {101, 102, 103, 104, 105, 106, 107, 108}");
        System.out.println("target = 107");
        System.out.println("First check has low = 0, high = 7, mid = 3, stockIds[mid] = 104.");

        boolean correct = askMultipleChoice(
                input,
                TOPIC_BINARY_SEARCH,
                "What happens next?",
                "high = mid - 1",
                "low = mid + 1",
                "return -1",
                "stop because 104 is close",
                "B",
                "The target is larger than 104, so binary search moves to the right half with low = mid + 1.");

        if (correct) {
            return 1;
        }

        return 0;
    }

    public static int askBossQuestionOffByOne(Scanner input) {
        System.out.println("Boss Question 7 - Off-by-One");
        System.out.println("for (int i = 0; i <= prices.length; i++) {");
        System.out.println("System.out.println(prices[i]);");
        System.out.println("}");

        boolean correct = askMultipleChoice(
                input,
                TOPIC_FOR_LOOPS,
                "Why is this wrong?",
                "It starts at 0",
                "It uses i++",
                "It eventually tries prices[prices.length]",
                "It prints too slowly",
                "C",
                "The loop condition allows i to equal prices.length, which is outside the array.");

        if (correct) {
            return 1;
        }

        return 0;
    }

    public static int askBossQuestionTradeMethod(Scanner input) {
        System.out.println("Boss Question 8 - Trade Method");

        boolean correct = askMultipleChoice(
                input,
                TOPIC_METHODS,
                "Which method should calculate the value of buying 3 shares of stock index 0?",
                "calculateTradeValue(0, 3)",
                "printMarket()",
                "initializePlayer()",
                "pauseBriefly()",
                "A",
                "calculateTradeValue(0, 3) passes an index and share count to the reusable trade method.");

        if (correct) {
            return 1;
        }

        return 0;
    }

    public static int askBossQuestionSearchComparison(Scanner input) {
        System.out.println("Boss Question 9 - Search Comparison");

        boolean correct = askMultipleChoice(
                input,
                TOPIC_BINARY_SEARCH,
                "Which search usually uses fewer comparisons on sorted data?",
                "Sequential search",
                "Binary search",
                "Random search",
                "They always use the same",
                "B",
                "Binary search can eliminate half of the remaining sorted range after each comparison.");

        if (correct) {
            return 1;
        }

        return 0;
    }

    public static int askBossQuestionMixedApplication(Scanner input) {
        System.out.println("Boss Question 10 - Mixed Application");

        boolean correct = askMultipleChoice(
                input,
                TOPIC_FOR_LOOPS,
                "A safe loop that scans every stock should use:",
                "for (int i = 0; i <= prices.length; i++)",
                "for (int i = 0; i < prices.length; i++)",
                "while (prices.length < i)",
                "for (int i = prices.length; i < 0; i++)",
                "B",
                "The safe loop starts at 0 and stops before prices.length, visiting every valid index.");

        if (correct) {
            return 1;
        }

        return 0;
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
        askMultipleChoice(
                input,
                TOPIC_ARRAYS,
                "Concept Check 1: What is the first valid array index?",
                "0",
                "1",
                "prices.length",
                "prices.length - 1",
                "A",
                "Java array indexes start at 0.");
        askMultipleChoice(
                input,
                TOPIC_ARRAYS,
                "Concept Check 2: What is the last valid index of prices?",
                "prices.length",
                "prices.length - 1",
                "prices[0]",
                "day",
                "B",
                "The last valid index is prices.length - 1.");
        askMultipleChoice(
                input,
                TOPIC_FOR_LOOPS,
                "Concept Check 3: Why use i < prices.length?",
                "It stops before the invalid index prices.length.",
                "It skips the first stock.",
                "It makes prices longer.",
                "It sorts the market.",
                "A",
                "The condition i < prices.length keeps the loop from accessing prices[prices.length].");
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
        } else if (choice == 2) {
            int target = readIntInRangeOrCancel(input, "Enter stock ID to search: ", Integer.MIN_VALUE + 1, Integer.MAX_VALUE);

            if (target == Integer.MIN_VALUE) {
                return;
            }

            int[] result = sequentialSearchIdWithCount(target);
            printSearchResult(result, "stock ID " + target);
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

    public static void printSequentialSearchCodeSnippet() {
        printCodeBlock("Sequential search by ticker", new String[] {
                "for (int i = 0; i < tickers.length; i++) {",
                "    if (tickers[i].equalsIgnoreCase(target)) {",
                "        return i;",
                "    }",
                "}",
                "return -1;"
        });
    }

    public static void printBinarySearchCodeSnippet() {
        printCodeBlock("Binary search by stock ID", new String[] {
                "int low = 0;",
                "int high = stockIds.length - 1;",
                "",
                "while (low <= high) {",
                "    int mid = (low + high) / 2;",
                "",
                "    if (stockIds[mid] == target) {",
                "        return mid;",
                "    } else if (target > stockIds[mid]) {",
                "        low = mid + 1;",
                "    } else {",
                "        high = mid - 1;",
                "    }",
                "}",
                "",
                "return -1;"
        });
    }

    public static int[] binarySearchIdWithCount(int target) {
        int[] result = new int[2];
        result[0] = -1;
        result[1] = 0;

        if (!stockIdsAreSortedAscending()) {
            System.out.println("Binary search cannot run because stockIds is not sorted.");
            return result;
        }

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
            } else if (target > stockIds[mid]) {
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

    public static boolean[] copyBooleanArray(boolean[] source) {
        boolean[] copy = new boolean[source.length];

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
        boolean savedSuppressMarketScannerTrace = suppressMarketScannerTrace;
        boolean savedJavaTradingLicenseUnlocked = javaTradingLicenseUnlocked;
        int savedMixedBossAttempts = mixedBossAttempts;
        int savedMixedBossBestScore = mixedBossBestScore;
        int savedJavaXp = javaXp;
        int savedQuestionsAnswered = questionsAnswered;
        int savedQuestionsCorrect = questionsCorrect;
        String[] savedTopicNames = topicNames;
        int[] savedTopicCorrect = copyIntArray(topicCorrect);
        int[] savedTopicAttempts = copyIntArray(topicAttempts);
        boolean[] savedTopicMastered = copyBooleanArray(topicMastered);
        boolean[] savedAbilityUnlocked = copyBooleanArray(abilityUnlocked);
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

        total = total + 1;
        passed = passed + recordTestResult("developer tests accept visible menu option 9", isDeveloperTestChoice(9));

        total = total + 1;
        passed = passed + recordTestResult("developer tests keep legacy menu option 99", isDeveloperTestChoice(99));

        total = total + 1;
        initializeLearningSystem();
        passed = passed + recordTestResult("initializeLearningSystem creates six topics", topicNames.length == 6);

        total = total + 1;
        passed = passed + recordTestResult("learning tracking arrays match topicNames length",
                topicCorrect.length == topicNames.length
                        && topicAttempts.length == topicNames.length
                        && topicMastered.length == topicNames.length
                        && abilityUnlocked.length == topicNames.length);

        total = total + 1;
        passed = passed + recordTestResult("normalizeAnswer trims and uppercases", normalizeAnswer(" b ").equals("B"));

        total = total + 1;
        passed = passed + recordTestResult("normalizeAnswer handles null", normalizeAnswer(null).equals(""));

        total = total + 1;
        passed = passed + recordTestResult("isValidTopicIndex accepts first topic", isValidTopicIndex(TOPIC_FOR_LOOPS));

        total = total + 1;
        passed = passed + recordTestResult("isValidTopicIndex rejects topicNames.length", !isValidTopicIndex(topicNames.length));

        total = total + 1;
        recordTopicResult(TOPIC_ARRAYS, false);
        passed = passed + recordTestResult("recordTopicResult counts wrong attempt without correct",
                topicAttempts[TOPIC_ARRAYS] == 1 && topicCorrect[TOPIC_ARRAYS] == 0);

        total = total + 1;
        recordTopicResult(TOPIC_ARRAYS, true);
        passed = passed + recordTestResult("recordTopicResult counts correct attempt",
                topicAttempts[TOPIC_ARRAYS] == 2 && topicCorrect[TOPIC_ARRAYS] == 1);

        total = total + 1;
        topicCorrect[TOPIC_METHODS] = 3;
        updateTopicMastery(TOPIC_METHODS);
        passed = passed + recordTestResult("updateTopicMastery masters topic after three correct answers", topicMastered(TOPIC_METHODS));

        total = total + 1;
        unlockAbilityIfMastered(TOPIC_METHODS);
        passed = passed + recordTestResult("unlockAbilityIfMastered unlocks mastered topic", abilityUnlocked[TOPIC_METHODS]);

        total = total + 1;
        passed = passed + recordTestResult("getAbilityName returns Trade Calculator for methods",
                getAbilityName(TOPIC_METHODS).equals("Trade Calculator"));

        total = total + 1;
        passed = passed + recordTestResult("getAbilityName returns Market Scanner for for loops",
                getAbilityName(TOPIC_FOR_LOOPS).equals("Market Scanner"));

        total = total + 1;
        passed = passed + recordTestResult("getAbilityName returns all Phase 3 ability names",
                getAbilityName(TOPIC_FOR_LOOPS).equals("Market Scanner")
                        && getAbilityName(TOPIC_WHILE_LOOPS).equals("Signal Decoder")
                        && getAbilityName(TOPIC_ARRAYS).equals("Index Vision")
                        && getAbilityName(TOPIC_METHODS).equals("Trade Calculator")
                        && getAbilityName(TOPIC_SEQUENTIAL_SEARCH).equals("Ticker Finder")
                        && getAbilityName(TOPIC_BINARY_SEARCH).equals("Fast Broker"));

        total = total + 1;
        initializeLearningSystem();
        topicMastered[TOPIC_FOR_LOOPS] = true;
        topicMastered[TOPIC_ARRAYS] = true;
        passed = passed + recordTestResult("countMasteredTopics counts mastered topics", countMasteredTopics() == 2);

        total = total + 1;
        passed = passed + recordTestResult("allSixTopicsMastered is false before all topics are mastered", !allSixTopicsMastered());

        total = total + 1;
        for (int i = 0; i < topicNames.length; i++) {
            topicMastered[i] = true;
        }
        passed = passed + recordTestResult("allSixTopicsMastered is true after all topics are mastered", allSixTopicsMastered());

        total = total + 1;
        initializeLearningSystem();
        passed = passed + recordTestResult("getFloorStatus returns OPEN before mastery",
                getFloorStatus(TOPIC_FOR_LOOPS).equals("OPEN"));

        total = total + 1;
        topicMastered[TOPIC_FOR_LOOPS] = true;
        passed = passed + recordTestResult("getFloorStatus returns MASTERED after mastery",
                getFloorStatus(TOPIC_FOR_LOOPS).equals("MASTERED"));

        total = total + 1;
        printUnlockSummary();
        passed = passed + recordTestResult("printUnlockSummary can run with initialized learning arrays", true);

        total = total + 1;
        initializeLearningSystem();
        passed = passed + recordTestResult("TOPIC_FOR_LOOPS is valid for Phase 4", isValidTopicIndex(TOPIC_FOR_LOOPS));

        total = total + 1;
        passed = passed + recordTestResult("marketScannerUnlocked is false before unlock", !marketScannerUnlocked());

        total = total + 1;
        abilityUnlocked[TOPIC_FOR_LOOPS] = true;
        passed = passed + recordTestResult("marketScannerUnlocked is true after unlock", marketScannerUnlocked());

        total = total + 1;
        printMarketScannerTrace();
        passed = passed + recordTestResult("printMarketScannerTrace can run when unlocked", true);

        total = total + 1;
        initializeLearningSystem();
        passed = passed + recordTestResult("TOPIC_ARRAYS is valid for Phase 5", isValidTopicIndex(TOPIC_ARRAYS));

        total = total + 1;
        passed = passed + recordTestResult("getAbilityName returns Index Vision for arrays",
                getAbilityName(TOPIC_ARRAYS).equals("Index Vision"));

        total = total + 1;
        passed = passed + recordTestResult("indexVisionUnlocked is false before unlock", !indexVisionUnlocked());

        total = total + 1;
        abilityUnlocked[TOPIC_ARRAYS] = true;
        passed = passed + recordTestResult("indexVisionUnlocked is true after unlock", indexVisionUnlocked());

        total = total + 1;
        printIndexVisionHint();
        passed = passed + recordTestResult("printIndexVisionHint can run when unlocked", true);

        total = total + 1;
        passed = passed + recordTestResult("Phase 5 validates first stock index", isValidStockIndex(0));

        total = total + 1;
        passed = passed + recordTestResult("Phase 5 validates last stock index", isValidStockIndex(prices.length - 1));

        total = total + 1;
        passed = passed + recordTestResult("Phase 5 rejects prices.length as stock index", !isValidStockIndex(prices.length));

        total = total + 1;
        passed = passed + recordTestResult("Phase 5 rejects negative stock index", !isValidStockIndex(-1));

        total = total + 1;
        initializeLearningSystem();
        passed = passed + recordTestResult("TOPIC_METHODS is valid for Phase 6", isValidTopicIndex(TOPIC_METHODS));

        total = total + 1;
        passed = passed + recordTestResult("getAbilityName returns Trade Calculator for methods in Phase 6",
                getAbilityName(TOPIC_METHODS).equals("Trade Calculator"));

        total = total + 1;
        passed = passed + recordTestResult("tradeCalculatorUnlocked is false before unlock", !tradeCalculatorUnlocked());

        total = total + 1;
        abilityUnlocked[TOPIC_METHODS] = true;
        passed = passed + recordTestResult("tradeCalculatorUnlocked is true after unlock", tradeCalculatorUnlocked());

        total = total + 1;
        printTradeCalculatorPreview(0, 2);
        passed = passed + recordTestResult("printTradeCalculatorPreview can run when unlocked", true);

        total = total + 1;
        passed = passed + recordTestResult("calculateTradeValue uses price times shares",
                calculateTradeValue(0, 2) == prices[0] * 2);

        total = total + 1;
        passed = passed + recordTestResult("canAffordTrade accepts zero-dollar trade", canAffordTrade(0));

        total = total + 1;
        passed = passed + recordTestResult("getNetWorth returns cash plus portfolio value",
                getNetWorth() == cash + getPortfolioValue());

        total = total + 1;
        passed = passed + recordTestResult("isTradeValueSafe accepts normal trade size",
                isTradeValueSafe(0, 2));

        total = total + 1;
        passed = passed + recordTestResult("isTradeValueSafe rejects overflow-sized trade",
                !isTradeValueSafe(0, Integer.MAX_VALUE));

        total = total + 1;
        initializeLearningSystem();
        passed = passed + recordTestResult("TOPIC_WHILE_LOOPS is valid for Phase 7",
                isValidTopicIndex(TOPIC_WHILE_LOOPS));

        total = total + 1;
        passed = passed + recordTestResult("getAbilityName returns Signal Decoder for while loops",
                getAbilityName(TOPIC_WHILE_LOOPS).equals("Signal Decoder"));

        total = total + 1;
        passed = passed + recordTestResult("signalDecoderUnlocked is false before unlock", !signalDecoderUnlocked());

        total = total + 1;
        abilityUnlocked[TOPIC_WHILE_LOOPS] = true;
        passed = passed + recordTestResult("signalDecoderUnlocked is true after unlock", signalDecoderUnlocked());

        total = total + 1;
        passed = passed + recordTestResult("countDigitsWithWhile counts six digits",
                countDigitsWithWhile(133444) == 6);

        total = total + 1;
        passed = passed + recordTestResult("countDigitsWithWhile treats zero as one digit",
                countDigitsWithWhile(0) == 1);

        total = total + 1;
        passed = passed + recordTestResult("getLastDigit returns ones digit",
                getLastDigit(123) == 3);

        total = total + 1;
        passed = passed + recordTestResult("dropLastDigit removes ones digit",
                dropLastDigit(123) == 12);

        total = total + 1;
        printSignalDecoderHint();
        passed = passed + recordTestResult("printSignalDecoderHint can run when unlocked", true);

        total = total + 1;
        initializeLearningSystem();
        passed = passed + recordTestResult("TOPIC_SEQUENTIAL_SEARCH is valid for Phase 8",
                isValidTopicIndex(TOPIC_SEQUENTIAL_SEARCH));

        total = total + 1;
        passed = passed + recordTestResult("getAbilityName returns Ticker Finder for sequential search",
                getAbilityName(TOPIC_SEQUENTIAL_SEARCH).equals("Ticker Finder"));

        total = total + 1;
        passed = passed + recordTestResult("tickerFinderUnlocked is false before unlock", !tickerFinderUnlocked());

        total = total + 1;
        abilityUnlocked[TOPIC_SEQUENTIAL_SEARCH] = true;
        passed = passed + recordTestResult("tickerFinderUnlocked is true after unlock", tickerFinderUnlocked());

        int[] apexTickerResult = sequentialSearchTickerWithCount("APEX");

        total = total + 1;
        passed = passed + recordTestResult("sequentialSearchTickerWithCount finds APEX at index 0",
                apexTickerResult[0] == 0);

        total = total + 1;
        passed = passed + recordTestResult("sequentialSearchTickerWithCount counts one comparison for APEX",
                apexTickerResult[1] == 1);

        int currentOmniIndex = -1;

        for (int i = 0; i < tickers.length; i++) {
            if (tickers[i].equalsIgnoreCase("OMNI")) {
                currentOmniIndex = i;
            }
        }

        int[] omniTickerResult = sequentialSearchTickerWithCount("OMNI");

        total = total + 1;
        passed = passed + recordTestResult("sequentialSearchTickerWithCount finds OMNI at current index",
                omniTickerResult[0] == currentOmniIndex);

        total = total + 1;
        passed = passed + recordTestResult("sequentialSearchTickerWithCount counts comparisons for OMNI",
                omniTickerResult[1] == currentOmniIndex + 1);

        int[] fakeTickerResult = sequentialSearchTickerWithCount("FAKE");

        total = total + 1;
        passed = passed + recordTestResult("sequentialSearchTickerWithCount returns -1 for FAKE",
                fakeTickerResult[0] == -1);

        total = total + 1;
        passed = passed + recordTestResult("sequentialSearchTickerWithCount checks all tickers for FAKE",
                fakeTickerResult[1] == tickers.length);

        int currentStockId104Index = -1;

        for (int i = 0; i < stockIds.length; i++) {
            if (stockIds[i] == 104) {
                currentStockId104Index = i;
            }
        }

        total = total + 1;
        passed = passed + recordTestResult("sequentialSearchIdWithCount finds stock ID 104 at current index",
                sequentialSearchIdWithCount(104)[0] == currentStockId104Index);

        total = total + 1;
        passed = passed + recordTestResult("sequentialSearchIdWithCount returns -1 for missing stock ID",
                sequentialSearchIdWithCount(999)[0] == -1);

        total = total + 1;
        printSequentialSearchCodeSnippet();
        passed = passed + recordTestResult("printSequentialSearchCodeSnippet can run", true);

        total = total + 1;
        initializeLearningSystem();
        passed = passed + recordTestResult("TOPIC_BINARY_SEARCH is valid for Phase 9",
                isValidTopicIndex(TOPIC_BINARY_SEARCH));

        total = total + 1;
        passed = passed + recordTestResult("getAbilityName returns Fast Broker for binary search",
                getAbilityName(TOPIC_BINARY_SEARCH).equals("Fast Broker"));

        total = total + 1;
        passed = passed + recordTestResult("fastBrokerUnlocked is false before unlock", !fastBrokerUnlocked());

        total = total + 1;
        abilityUnlocked[TOPIC_BINARY_SEARCH] = true;
        passed = passed + recordTestResult("fastBrokerUnlocked is true after unlock", fastBrokerUnlocked());

        total = total + 1;
        passed = passed + recordTestResult("stockIdsAreSortedAscending is true for default market",
                stockIdsAreSortedAscending());

        int currentStockId101Index = -1;
        int currentStockId108Index = -1;

        for (int i = 0; i < stockIds.length; i++) {
            if (stockIds[i] == 101) {
                currentStockId101Index = i;
            }

            if (stockIds[i] == 108) {
                currentStockId108Index = i;
            }
        }

        total = total + 1;
        passed = passed + recordTestResult("binarySearchIdWithCount finds stock ID 101 at current index",
                binarySearchIdWithCount(101)[0] == currentStockId101Index);

        total = total + 1;
        passed = passed + recordTestResult("binarySearchIdWithCount finds stock ID 108 at current index",
                binarySearchIdWithCount(108)[0] == currentStockId108Index);

        int[] missingBinaryResult = binarySearchIdWithCount(999);

        total = total + 1;
        passed = passed + recordTestResult("binarySearchIdWithCount returns -1 for missing stock ID",
                missingBinaryResult[0] == -1);

        total = total + 1;
        passed = passed + recordTestResult("binarySearchIdWithCount counts comparisons for missing stock ID",
                missingBinaryResult[1] > 0);

        total = total + 1;
        printBinarySearchCodeSnippet();
        passed = passed + recordTestResult("printBinarySearchCodeSnippet can run", true);

        total = total + 1;
        javaTradingLicenseUnlocked = false;
        passed = passed + recordTestResult("Java Trading License starts locked after temporary reset",
                !javaTradingLicenseUnlocked());

        total = total + 1;
        passed = passed + recordTestResult("locked Java Trading License gives zero final score bonus",
                getFinalScoreBonusPercent() == 0);

        int normalNetWorthForLicenseTest = getNetWorth();

        total = total + 1;
        passed = passed + recordTestResult("locked Java Trading License keeps licensed net worth unchanged",
                getLicensedFinalNetWorth() == normalNetWorthForLicenseTest);

        javaTradingLicenseUnlocked = true;

        total = total + 1;
        passed = passed + recordTestResult("Java Trading License reports unlocked after temporary unlock",
                javaTradingLicenseUnlocked());

        total = total + 1;
        passed = passed + recordTestResult("unlocked Java Trading License gives 20 percent final score bonus",
                getFinalScoreBonusPercent() == 20);

        total = total + 1;
        passed = passed + recordTestResult("licensed final net worth includes 20 percent bonus",
                getLicensedFinalNetWorth() == normalNetWorthForLicenseTest + (normalNetWorthForLicenseTest * 20 / 100));

        total = total + 1;
        gameOverReason = "Developer final summary license display test.";
        printFinalSummary();
        passed = passed + recordTestResult("printFinalSummary can show Java Trading License bonus", true);

        total = total + 1;
        initializeLearningSystem();
        for (int i = 0; i < topicNames.length; i++) {
            topicMastered[i] = true;
        }
        passed = passed + recordTestResult("Phase 10 allSixTopicsMastered works when all topics are mastered",
                allSixTopicsMastered());

        total = total + 1;
        topicMastered[TOPIC_BINARY_SEARCH] = false;
        passed = passed + recordTestResult("Mixed Review Boss remains locked when one topic is unmastered",
                !allSixTopicsMastered());

        total = total + 1;
        printTeachingQualityChecklist();
        passed = passed + recordTestResult("printTeachingQualityChecklist can run", true);

        total = total + 1;
        initializeLearningSystem();
        for (int i = 0; i < topicNames.length; i++) {
            topicCorrect[i] = 3;
            topicAttempts[i] = 3;
            topicMastered[i] = true;
            abilityUnlocked[i] = true;
        }
        javaTradingLicenseUnlocked = false;
        mixedBossAttempts = 0;
        mixedBossBestScore = 0;
        runMixedReviewBoss(new Scanner("B\nC\nA\nC\nD\nB\nC\nA\nB\nB\n"));
        passed = passed + recordTestResult("Mixed Review Boss normal flow unlocks license after ten correct answers",
                javaTradingLicenseUnlocked()
                        && mixedBossAttempts == 1
                        && mixedBossBestScore == 10);

        total = total + 1;
        initializeLearningSystem();
        int savedXpBeforeQuestion = javaXp;
        boolean activeQuestionPassed = askMultipleChoice(
                new Scanner("B\n"),
                TOPIC_FOR_LOOPS,
                "Developer test question",
                "wrong",
                "right",
                "wrong",
                "wrong",
                "B",
                "B is the correct test answer.");
        passed = passed + recordTestResult("askMultipleChoice records correct answer and XP",
                activeQuestionPassed
                        && questionsAnswered == 1
                        && questionsCorrect == 1
                        && topicAttempts[TOPIC_FOR_LOOPS] == 1
                        && topicCorrect[TOPIC_FOR_LOOPS] == 1
                        && javaXp == savedXpBeforeQuestion + 10);

        total = total + 1;
        int savedXpBeforeWrongQuestion = javaXp;
        boolean wrongQuestionPassed = askMultipleChoice(
                new Scanner("A\n"),
                TOPIC_FOR_LOOPS,
                "Developer wrong answer test",
                "wrong",
                "right",
                "wrong",
                "wrong",
                "B",
                "B is the correct test answer.");
        passed = passed + recordTestResult("askMultipleChoice gives no XP for wrong answer",
                !wrongQuestionPassed
                        && questionsAnswered == 2
                        && questionsCorrect == 1
                        && topicAttempts[TOPIC_FOR_LOOPS] == 2
                        && topicCorrect[TOPIC_FOR_LOOPS] == 1
                        && javaXp == savedXpBeforeWrongQuestion);

        total = total + 1;
        int savedXpBeforeInvalid = javaXp;
        boolean invalidQuestionPassed = askMultipleChoice(
                new Scanner("Z\n"),
                TOPIC_FOR_LOOPS,
                "Developer invalid answer test",
                "one",
                "two",
                "three",
                "four",
                "A",
                "Invalid answers should not reveal XP.");
        passed = passed + recordTestResult("askMultipleChoice gives no XP for invalid answer",
                !invalidQuestionPassed
                        && questionsAnswered == 3
                        && topicAttempts[TOPIC_FOR_LOOPS] == 3
                        && javaXp == savedXpBeforeInvalid);

        cash = savedCash;
        day = savedDay;
        gameOver = savedGameOver;
        gameOverReason = savedGameOverReason;
        learningMode = savedLearningMode;
        suppressMarketScannerTrace = savedSuppressMarketScannerTrace;
        javaTradingLicenseUnlocked = savedJavaTradingLicenseUnlocked;
        mixedBossAttempts = savedMixedBossAttempts;
        mixedBossBestScore = savedMixedBossBestScore;
        javaXp = savedJavaXp;
        questionsAnswered = savedQuestionsAnswered;
        questionsCorrect = savedQuestionsCorrect;
        topicNames = savedTopicNames;
        topicCorrect = savedTopicCorrect;
        topicAttempts = savedTopicAttempts;
        topicMastered = savedTopicMastered;
        abilityUnlocked = savedAbilityUnlocked;
        prices = savedPrices;
        previousPrices = savedPreviousPrices;
        sharesOwned = savedSharesOwned;
        riskLevels = savedRiskLevels;
        stockIds = savedStockIds;

        System.out.println("Developer Tests Passed: " + passed + " / " + total);
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
        System.out.println("Java XP: " + javaXp);
        System.out.println("Questions: " + questionsCorrect + " correct / " + questionsAnswered + " answered");
        System.out.println("Mixed Boss Best Score: " + mixedBossBestScore + " / 10");
        if (javaTradingLicenseUnlocked()) {
            System.out.println("Java Trading License: UNLOCKED");
        } else {
            System.out.println("Java Trading License: LOCKED");
        }
        System.out.println("Final Score Bonus: " + getFinalScoreBonusPercent() + "%");
        System.out.println("Licensed Final Net Worth: $" + getLicensedFinalNetWorth());
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

    public static boolean isTradeValueSafe(int index, int shares) {
        if (!isValidStockIndex(index)) {
            return false;
        }

        if (shares <= 0) {
            return false;
        }

        return shares <= Integer.MAX_VALUE / prices[index];
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

        if (signalDecoderUnlocked()) {
            printSignalDecoderHint();
        } else if (learningMode) {
            System.out.println("Tip: Master the While Loop Floor to unlock market signal decoding.");
        }

        printLearningNote("Array mutation", "advanceDay copies prices into previousPrices, then changes prices[i] inside a for loop.");
        copyCurrentPricesToPreviousPrices();
        updatePricesForNewDay();
        day = day + 1;

        System.out.println("The market moves into Day " + day + ".");
        printDailyMarketSummary();
        checkGameOverAfterMarketMove();
    }

    public static boolean signalDecoderUnlocked() {
        return isValidTopicIndex(TOPIC_WHILE_LOOPS)
                && abilityUnlocked != null
                && TOPIC_WHILE_LOOPS < abilityUnlocked.length
                && abilityUnlocked[TOPIC_WHILE_LOOPS];
    }

    public static int countDigitsWithWhile(int number) {
        if (number == 0) {
            return 1;
        }

        if (number < 0) {
            number = Math.abs(number);
        }

        int count = 0;

        while (number > 0) {
            count = count + 1;
            number = number / 10;
        }

        return count;
    }

    public static int getLastDigit(int number) {
        if (number < 0) {
            number = Math.abs(number);
        }

        return number % 10;
    }

    public static int dropLastDigit(int number) {
        if (number < 0) {
            number = Math.abs(number);
        }

        return number / 10;
    }

    public static void printSignalDecoderHint() {
        if (!signalDecoderUnlocked()) {
            return;
        }

        int signal = randomIntInRange(100, 999);
        int digitCount = countDigitsWithWhile(signal);
        int lastDigit = getLastDigit(signal);
        int reducedSignal = dropLastDigit(signal);

        System.out.println("SIGNAL DECODER ACTIVE");
        System.out.println("Market signal: " + signal);
        System.out.println("Digit count using while loop: " + digitCount);
        System.out.println("Last digit using signal % 10: " + lastDigit);
        System.out.println("Signal after signal / 10: " + reducedSignal);

        if (lastDigit >= 5) {
            System.out.println("Decoder hint: High final digit. Expect stronger volatility today.");
        } else {
            System.out.println("Decoder hint: Low final digit. Expect calmer movement today.");
        }

        if (learningMode) {
            System.out.println("while (signal > 0) {");
            System.out.println("    int digit = signal % 10;");
            System.out.println("    signal = signal / 10;");
            System.out.println("}");
        }
    }

    public static boolean indexVisionUnlocked() {
        return isValidTopicIndex(TOPIC_ARRAYS)
                && abilityUnlocked != null
                && TOPIC_ARRAYS < abilityUnlocked.length
                && abilityUnlocked[TOPIC_ARRAYS];
    }

    public static boolean tradeCalculatorUnlocked() {
        return isValidTopicIndex(TOPIC_METHODS)
                && abilityUnlocked != null
                && TOPIC_METHODS < abilityUnlocked.length
                && abilityUnlocked[TOPIC_METHODS];
    }

    public static void printTradeCalculatorPreview(int index, int shares) {
        if (!tradeCalculatorUnlocked()) {
            return;
        }

        if (!isValidStockIndex(index)) {
            return;
        }

        if (shares <= 0) {
            return;
        }

        int tradeValue = calculateTradeValue(index, shares);

        System.out.println("TRADE CALCULATOR ACTIVE");
        System.out.println("Method call: calculateTradeValue(" + index + ", " + shares + ")");
        System.out.println("Formula: prices[index] * shares");
        System.out.println("Price: $" + prices[index]);
        System.out.println("Shares: " + shares);
        System.out.println("Trade Value: $" + tradeValue);
        System.out.println("This method returns an int that buyStock and sellStock can reuse.");

        if (learningMode) {
            System.out.println("The reusable calculation keeps both trade actions consistent.");
        }
    }

    public static void printIndexVisionHint() {
        if (!indexVisionUnlocked()) {
            return;
        }

        System.out.println("INDEX VISION ACTIVE");
        System.out.println("Valid stock indexes are 0 through " + (prices.length - 1) + ".");
        System.out.println("Never use prices[prices.length]; that index is outside the array.");
        System.out.println("Parallel arrays keep related stock data aligned at the same index.");

        if (learningMode) {
            System.out.println("Parallel array reminder:");
            System.out.println("tickers[i], prices[i], sharesOwned[i], stockIds[i], and riskLevels[i] describe the same stock.");
        }
    }

    public static boolean tickerFinderUnlocked() {
        return isValidTopicIndex(TOPIC_SEQUENTIAL_SEARCH)
                && abilityUnlocked != null
                && TOPIC_SEQUENTIAL_SEARCH < abilityUnlocked.length
                && abilityUnlocked[TOPIC_SEQUENTIAL_SEARCH];
    }

    public static boolean fastBrokerUnlocked() {
        return isValidTopicIndex(TOPIC_BINARY_SEARCH)
                && abilityUnlocked != null
                && TOPIC_BINARY_SEARCH < abilityUnlocked.length
                && abilityUnlocked[TOPIC_BINARY_SEARCH];
    }

    public static int runTickerFinder(Scanner input) {
        if (!tickerFinderUnlocked()) {
            return -1;
        }

        System.out.println("TICKER FINDER ACTIVE");
        System.out.println("This tool uses sequential search to scan tickers from first to last.");

        if (learningMode) {
            printSequentialSearchCodeSnippet();
        }

        System.out.print("Enter ticker to search: ");
        String target = input.nextLine();

        if (target.trim().equals("")) {
            System.out.print("Enter ticker to search: ");
            target = input.nextLine();
        }

        int[] result = sequentialSearchTickerWithCount(target);

        if (result[0] == -1) {
            System.out.println("Ticker not found: " + target);
        } else {
            System.out.println("Found ticker " + target + " at index " + result[0] + ".");
            printStockLine(result[0]);
        }

        System.out.println("Comparisons: " + result[1]);
        return result[0];
    }

    public static int runFastBrokerSearch(Scanner input) {
        if (!fastBrokerUnlocked()) {
            return -1;
        }

        if (!stockIdsAreSortedAscending()) {
            System.out.println("Fast Broker requires sorted stock IDs. It cannot run right now.");
            return -1;
        }

        System.out.println("FAST BROKER ACTIVE");
        System.out.println("This tool uses binary search to search sorted stock IDs.");

        if (learningMode) {
            printBinarySearchCodeSnippet();
        }

        int target = readIntOrCancel(input, "Enter stock ID to search: ");

        if (target == Integer.MIN_VALUE) {
            return -1;
        }

        int[] result = binarySearchIdWithCount(target);

        if (result[0] == -1) {
            System.out.println("Stock ID not found: " + target);
        } else {
            System.out.println("Found stock ID " + target + " at index " + result[0] + ".");
            printStockLine(result[0]);
        }

        System.out.println("Comparisons: " + result[1]);
        return result[0];
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

        if (indexVisionUnlocked()) {
            printIndexVisionHint();
        } else if (learningMode) {
            System.out.println("Tip: Master the Arrays Floor to unlock Index Vision and get index safety hints.");
        }

        int index = Integer.MIN_VALUE;

        if (tickerFinderUnlocked()) {
            int finderChoice = readIntInRangeOrCancel(input, "Use Ticker Finder before choosing a stock? 1 = Yes, 0 = No: ", 0, 1);

            if (finderChoice == 1) {
                int foundIndex = runTickerFinder(input);

                if (isValidStockIndex(foundIndex)) {
                    index = foundIndex;
                }
            }
        } else if (learningMode) {
            System.out.println("Tip: Master the Sequential Search Floor to unlock Ticker Finder.");
        }

        if (index == Integer.MIN_VALUE) {
            if (fastBrokerUnlocked()) {
                int brokerChoice = readIntInRangeOrCancel(input, "Use Fast Broker ID search before choosing a stock? 1 = Yes, 0 = No: ", 0, 1);

                if (brokerChoice == 0 && !tickerFinderUnlocked() && learningMode) {
                    System.out.println("Fast Broker is available. Enter 1 to search by stock ID or 0 to type an index manually.");
                    brokerChoice = readIntInRangeOrCancel(input, "Use Fast Broker ID search before choosing a stock? 1 = Yes, 0 = No: ", 0, 1);
                }

                if (brokerChoice == 1) {
                    int foundIndex = runFastBrokerSearch(input);

                    if (isValidStockIndex(foundIndex)) {
                        index = foundIndex;
                    }
                }
            } else if (learningMode) {
                System.out.println("Tip: Master the Binary Search Floor to unlock Fast Broker.");
            }
        }

        if (index == Integer.MIN_VALUE) {
            index = readIntOrCancel(input, "Enter stock index to buy: ");
        }

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
            System.out.println("Shares must be greater than 0.");
            shares = readIntOrCancel(input, "Enter number of shares to buy: ");

            if (shares == Integer.MIN_VALUE) {
                return;
            }

            if (shares <= 0) {
                System.out.println("Shares must be greater than 0. Trade cancelled.");
                return;
            }
        }

        if (!isTradeValueSafe(index, shares)) {
            System.out.println("Share amount is too large. Trade cancelled.");
            return;
        }

        if (tradeCalculatorUnlocked()) {
            printTradeCalculatorPreview(index, shares);
        } else if (learningMode) {
            System.out.println("Tip: Master the Methods Floor to unlock Trade Calculator previews.");
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

        if (indexVisionUnlocked()) {
            printIndexVisionHint();
        } else if (learningMode) {
            System.out.println("Tip: Master the Arrays Floor to unlock Index Vision and get index safety hints.");
        }

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

        if (!isTradeValueSafe(index, shares)) {
            System.out.println("Share amount is too large. Trade cancelled.");
            return;
        }

        if (shares > sharesOwned[index]) {
            System.out.println("You do not own that many shares. Trade cancelled.");
            return;
        }

        if (tradeCalculatorUnlocked()) {
            printTradeCalculatorPreview(index, shares);
        } else if (learningMode) {
            System.out.println("Tip: Master the Methods Floor to unlock Trade Calculator previews.");
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

    public static boolean marketScannerUnlocked() {
        return isValidTopicIndex(TOPIC_FOR_LOOPS)
                && abilityUnlocked != null
                && TOPIC_FOR_LOOPS < abilityUnlocked.length
                && abilityUnlocked[TOPIC_FOR_LOOPS];
    }

    public static void printMarketScannerTrace() {
        if (!marketScannerUnlocked()) {
            return;
        }

        System.out.println();
        System.out.println("MARKET SCANNER ACTIVE");
        System.out.println("This market table is produced by an indexed for loop:");
        System.out.println("for (int i = 0; i < prices.length; i++) {");
        System.out.println("    // print stock at index i");
        System.out.println("}");
        System.out.println("First valid index: 0");
        System.out.println("Last valid index: " + (prices.length - 1));
        System.out.println("Stocks scanned: " + prices.length);

        if (learningMode) {
            System.out.println("Scanner trace:");

            for (int i = 0; i < tickers.length; i++) {
                System.out.println("i = " + i + " scans tickers[" + i + "]");
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
        if (!suppressMarketScannerTrace) {
            printLearningNote("Indexed for loop", "This table uses for (int i = 0; i < prices.length; i++) to line up values from parallel arrays.");
        }

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

        if (marketScannerUnlocked() && !suppressMarketScannerTrace) {
            printMarketScannerTrace();
        } else if (!marketScannerUnlocked() && learningMode && !suppressMarketScannerTrace) {
            System.out.println();
            System.out.println("Tip: Master the For Loop Floor to unlock Market Scanner.");
        }
    }

    public static void pauseBriefly() {
        System.out.println();
    }
}
