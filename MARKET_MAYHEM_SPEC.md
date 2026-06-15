# Market Mayhem: Java Trading Academy - Mixed Review Boss + Final Polish

## Game Concept

Market Mayhem is a terminal-based beginner Java trading academy. The player starts with limited cash and tries to grow net worth by buying and selling fake stocks over several market days.

The game is also the classroom. Codex builds the Java source code, and the student learns by playing the terminal game. The student should not edit Java source files to learn. Do not create StudentWork.java. All learning happens through in-game questions, tracing, predictions, bug-fix prompts, immediate feedback, mastery tracking, and trading unlocks.

v0.2 must not be a passive Java-notes stock game. Java concepts must be taught through interactive academy challenges that affect gameplay.

Phase 10 completes the core teaching version by fully building the Mixed Review Boss, adding the Java Trading License final reward, and polishing final score reporting. The academy remains the main learning hub with floor cards, topic status, mastery progress, unlock previews, active challenge routing, and a mastery-gated Mixed Review Boss.

## Core Trading Loop

The trading game remains playable:

1. Player views the market.
2. Player analyzes stock prices and unlocked academy tools.
3. Player buys or sells shares.
4. Player advances the day.
5. Prices change randomly.
6. Net worth updates.
7. Game checks win/loss conditions.
8. Player enters academy floors to master Java and unlock better trading tools.

## Required Academy Gameplay Loop

Every academy floor, lab, quiz, trace, and boss challenge must follow this loop:

1. Briefing
2. Challenge
3. Player answer
4. Immediate feedback
5. Explanation
6. XP only if correct
7. Unlock trading ability after mastery

Rules:

* The game may show a short Java snippet during the briefing.
* The game must ask a question before revealing the answer.
* Reading a briefing or explanation never gives XP.
* Correct answers or completed challenges give XP.
* Wrong answers give short explanations and no XP.
* Mastery is tracked by topic.
* Trading advantages unlock only after Java mastery.

## v0.2 Includes

v0.2 includes all v0.1 gameplay:

* menu loop
* view market
* advance day
* view portfolio
* buy stock
* sell stock
* cash
* portfolio value
* net worth
* day counter
* win/loss condition
* final summary

Phase 10 preserves the Java Trading Academy shell:

* Learning Mode
* Java XP
* topic mastery tracking
* mastery-gated trading advantages
* Java Trading Academy hub
* academy floor cards
* floor status: OPEN, MASTERED, or LOCKED for the Mixed Review Boss
* unlock summary
* askMultipleChoice
* askShortAnswer
* Mastery Report
* routing helpers
* For Loop Floor
* While Loop Floor
* Arrays Floor
* Methods Floor
* Sequential Search Floor
* Binary Search Floor
* Mixed Review Boss
* Java Trading License
* Developer Tests

Phase 10 fully builds the Mixed Review Boss and Java Trading License final reward. For Loop Floor and Market Scanner from Phase 4 must keep working. Arrays Floor and Index Vision from Phase 5 must keep working. Methods Floor and Trade Calculator from Phase 6 must keep working. While Loop Floor and Signal Decoder from Phase 7 must keep working. Sequential Search Floor and Ticker Finder from Phase 8 must keep working. Binary Search Floor and Fast Broker from Phase 9 must keep working. Old passive labs are not exposed as the main teaching path, and passive labs that award XP for reading are forbidden.

Mixed Review Boss tests mixed understanding across:

* for loops
* while loops
* arrays
* methods
* sequential search
* binary search

Mixed Review Boss rules:

* unlocks only after all six topics are mastered
* asks 10 active questions
* passing requires at least 8 / 10 correct
* XP requires correct answers
* no answer is revealed before player input
* passing unlocks Java Trading License
* Java Trading License gives a visible 20% final-score bonus
* the player learns by playing, not by editing source files

Phase 10 keeps beginner Java constraints: arrays, for loops, while loops, static methods, Scanner, manual sequential search, manual binary search, no ArrayList, no HashMap, no streams, no lambdas, no Arrays.sort, no Arrays.binarySearch, no Collections.sort, and no java.util.Random.

## Future Floor Challenge Types

Each academy floor eventually contains five challenge types:

1. Predict Output
2. Trace Variable
3. Fix Bug
4. Fill Blank
5. Stock Application

Phase 3 created the routing and shell structure for those challenge types. Phase 4 filled the For Loop Floor with the full five-question content. Phase 5 filled the Arrays Floor with the full five-question content. Phase 6 filled the Methods Floor with the full five-question content. Phase 7 filled the While Loop Floor with the full five-question content. Phase 8 filled the Sequential Search Floor with the full five-question content. Phase 9 filled the Binary Search Floor with the full five-question content. Phase 10 fills the Mixed Review Boss and completes the core teaching loop.

## Trading Unlocks

Java mastery unlocks trading advantages by topic:

* For Loops -> Market Scanner
* While Loops -> Signal Decoder
* Arrays -> Index Vision
* Methods -> Trade Calculator
* Sequential Search -> Ticker Finder
* Binary Search -> Fast Broker
* Mixed Review Boss -> Java Trading License

## v0.2 Does Not Include Yet

Later expansion phases may add:

* selection sort
* insertion sort
* duplicate detection
* mode
* reverse/rotate/swap puzzles
* advanced digit-code puzzles
* file saving
* graphics
* extra object-oriented classes

## Data Model

Use parallel arrays for stocks:

* String[] tickers
* int[] stockIds
* int[] prices
* int[] previousPrices
* int[] sharesOwned
* int[] riskLevels

Also use:

* int cash
* int day
* int maxDays
* int startingCash
* int winNetWorth
* boolean running
* boolean learningMode
* int javaXp
* int questionsAnswered
* int questionsCorrect

Use parallel arrays for academy mastery:

* String[] topicNames
* int[] topicCorrect
* int[] topicAttempts
* boolean[] topicMastered
* boolean[] abilityUnlocked

The same index must describe the same topic across all academy arrays. For example, topicNames[i], topicCorrect[i], topicAttempts[i], topicMastered[i], and abilityUnlocked[i] all describe one academy topic.

Do not use ArrayList, HashMap, or extra classes for mastery tracking in v0.2.

## Target v0.2 Menu

0. Exit
1. View Market
2. Advance Day
3. View Portfolio
4. Buy Stock
5. Sell Stock
6. Java Trading Academy
7. Mastery Report
8. Toggle Learning Mode
9. Run Developer Tests

Developer Tests should also accept 99 as a legacy shortcut so older smoke tests keep working.

## Academy Modules

| Module | Concepts | Challenge Types | Trading Unlock |
| --- | --- | --- | --- |
| For Loop Floor | indexed for loops, i, i < arr.length, traversal, off-by-one prevention | predict loop output, choose loop condition, trace visited indexes, fix arr[arr.length] bug | Market Scanner |
| While Loop Floor | while conditions, repeated execution, loop-control variables, update steps, infinite-loop risk, unknown repetition counts, digit processing with % and / | predict output, trace variable, fix infinite loop, fill digit expression, decode market signal | Signal Decoder |
| Arrays Floor | 1D arrays, indexing, array.length, parallel arrays, sum, average, max/min, counting | predict array values, calculate average, trace max/min, match parallel-array values | Index Vision |
| Methods Floor | methods, static methods, parameters, arguments, return values, void vs non-void, decomposition | predict return values, trace parameters, fix method calls, fill method headers, apply reusable methods to trading logic | Trade Calculator |
| Sequential Search Floor | manual linear search, first-to-last scanning, target comparisons, found index, -1 when missing, comparison counts, equalsIgnoreCase, == for int search, unsorted-data compatibility | trace checked indexes, predict found index, not-found return, fill return blank, stock ticker application | Ticker Finder |
| Binary Search Floor | sorted-data requirement, middle checks, low, high, mid, while (low <= high), moving bounds, halving the range, found index, -1 when missing, comparison counts | sorted requirement, trace midpoint, update search range, fill while condition, stock ID application | Fast Broker |
| Mixed Review Boss | for loops, while loops, arrays, methods, sequential search, binary search, validation, off-by-one prevention | 10 mixed active questions requiring 8 / 10 correct | Java Trading License |

## Learning Philosophy

Every learning feature must make Java visible and playable.

Examples:

* View Market can teach indexed for loops and array traversal by showing a trace challenge before revealing a loop result.
* Portfolio can teach accumulator variables by asking the player to predict a running total.
* Buy/Sell can teach index validation and array mutation through short prompts.
* Advance Day can teach Math.random, array updates, and preserving previous values.
* For Loop Floor teaches initializer, condition, update, i, traversal, and off-by-one prevention.
* While Loop Floor teaches conditions, repeated execution, control-variable updates, infinite-loop prevention, and digit processing with % and /.
* Arrays Floor teaches indexes, array.length, parallel arrays, sum, average, max/min, and counting.
* Methods Floor teaches method signatures, static methods, parameters, return values, and decomposition.
* Sequential Search Floor teaches first-to-last comparison, early stopping, not-found returns, and comparison counts.
* Binary Search Floor teaches sorted-data requirements, low/high/mid, while-loop bounds, range halving, found/not-found returns, and comparison counts.
* Mixed Review Boss proves the player can combine concepts before receiving Java Trading License and a 20% final-score bonus.
* Developer Tests verify correctness.

The player earns Java XP by answering correctly, not by reading. A concept explanation should help the next attempt, not function as a reward by itself.

## XP, Feedback, and Mastery

XP rules:

* Award XP only for a correct answer or completed challenge.
* Do not award XP for opening a floor.
* Do not award XP for opening the concept map.
* Do not award XP for reading a code snippet.
* Do not award XP for reading feedback or explanations.
* Do not award XP for a wrong answer.

Feedback rules:

* Correct answers should confirm the answer and briefly explain why it works.
* Wrong answers should briefly explain the mistake.
* Feedback should be short enough to fit naturally in the terminal.
* The answer must be revealed only after the player answers.

Mastery rules:

* Track attempts and correct answers by topic.
* Mark a topic mastered only after the player completes the required challenges for that topic.
* Use mastery to unlock trading advantages.
* Keep core trading playable even before unlocks.
* Unlocks should help the player analyze or navigate the market, not replace the need to make trading choices.

## In-Game Bug Fixes

Bug-fix challenges must happen inside the terminal game. The student should answer by selecting an option, typing a missing expression, or choosing a corrected line.

Example acceptable prompt:

```java
for (int i = 0; i <= prices.length; i++) {
    System.out.println(prices[i]);
}
```

Question:
Which condition prevents the off-by-one bug?

1. i < prices.length
2. i <= prices.length
3. i == prices.length

The student answers in the game. The student does not edit MarketMayhem.java.

## Anti-Cheating Rule

Do not hide the learning by using advanced shortcuts. The student should see the actual loops, array indexing, searching logic, and binary search logic.

Do not use Arrays.binarySearch, Arrays.sort, Collections.sort, ArrayList, HashMap, streams, lambdas, java.util.Random, or external dependencies.

## Beginner Java Constraints

Use:

* arrays
* for loops
* while loops
* static methods
* Scanner
* manual sequential search
* manual binary search
* terminal input/output
* one public class named MarketMayhem
* one main Java file named MarketMayhem.java

Do not use:

* ArrayList
* HashMap
* streams
* lambdas
* Arrays.sort
* Arrays.binarySearch
* Collections.sort
* java.util.Random
* extra object-oriented model classes
* StudentWork.java
* graphics
* file saving
* external dependencies

## Developer Tests

Developer Tests should verify:

* arrays have matching lengths
* buy and sell reject invalid indexes
* portfolio and net worth calculations are correct
* random price updates preserve previousPrices
* sequential search is manual and returns correct indexes
* binary search is manual and returns correct indexes
* academy XP is not awarded for passive reading
* wrong answers do not award XP
* correct answers award XP
* mastery updates only after correct challenge completion
* trading unlocks require mastery
* the Mixed Review Boss does not unlock rewards before mastery
* Java Trading License changes final score display only after a passing boss score

## Done Criteria

v0.2 is done only when:

* the trading game is still playable
* the Java Trading Academy is interactive
* the student learns by playing, not by editing Java files
* every academy floor asks before revealing answers
* no passive XP exists
* XP requires correct answers or completed challenges
* wrong answers give short explanations
* mastery is tracked by topic
* trading advantages unlock only after mastery
* beginner Java constraints are preserved
* Developer Tests cover the academy reward rules
* MarketMayhem.java compiles with javac
