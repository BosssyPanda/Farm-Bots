# Market Mayhem v0.2 - Java Learning Edition

## Game Concept

Market Mayhem is a terminal-based beginner Java stock trading game. The player starts with limited cash and tries to grow net worth by buying and selling fake stocks over several market days.

The game is designed to teach Java fundamentals through real gameplay. In v0.2, the player also sees Java code snippets, algorithm traces, and concept checks while playing.

## Core Game Loop

Each turn/day:

1. Player views market.
2. Player analyzes stock prices.
3. Player buys or sells shares.
4. Player advances the day.
5. Prices change randomly.
6. Net worth updates.
7. Game checks win/loss conditions.
8. Player can open learning labs to practise Java concepts.

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

v0.2 also includes:

* Learning Mode
* Java XP
* Java Concept Map
* Loops + Arrays Lab
* Methods Lab
* Search Lab
* Developer Tests

## v0.2 Does Not Include Yet

Later expansion phases may add:

* selection sort
* insertion sort
* duplicate detection
* mode
* reverse/rotate/swap puzzles
* digit-code puzzles
* file saving
* graphics
* extra object-oriented classes

## Data Model

Use parallel arrays:

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

## Target v0.2 Menu

0. Exit
1. View Market
2. Advance Day
3. View Portfolio
4. Buy Stock
5. Sell Stock
6. Java Concept Map
7. Loops + Arrays Lab
8. Methods Lab
9. Search Lab
10. Toggle Learning Mode
99. Run Developer Tests

## Learning Philosophy

Every feature must teach a Java concept.

Examples:

* View Market teaches indexed for loops and array traversal.
* Portfolio teaches accumulator variables.
* Buy/Sell teaches index validation and array mutation.
* Advance Day teaches Math.random, array updates, and preserving previous values.
* Loops + Arrays Lab teaches for loops, while loops, indexes, and array.length.
* Methods Lab teaches method signatures, parameters, return values, and static methods.
* Search Lab teaches sequential search and binary search.
* Binary search must explain that data must be sorted before binary search works.
* Developer Tests verify correctness.

## Anti-Cheating Rule

Do not hide the learning by using advanced shortcuts. The student should see the actual loops, array indexing, searching logic, and binary search logic.

Do not use Arrays.binarySearch, Arrays.sort, Collections.sort, ArrayList, HashMap, streams, lambdas, or external dependencies.
