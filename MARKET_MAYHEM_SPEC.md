# Market Mayhem Game Spec

## Game Concept

Market Mayhem is a terminal-based beginner Java stock trading game. The player starts with limited cash and tries to grow net worth by buying and selling fake stocks over several market days.

The game is designed to teach Java fundamentals through real gameplay.

## Core Game Loop

Each turn/day:

1. Player views market.
2. Player analyzes stock prices.
3. Player buys or sells shares.
4. Player advances the day.
5. Prices change randomly.
6. Net worth updates.
7. Game checks win/loss conditions.

## First Playable Version v0.1

v0.1 includes:

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

v0.1 does not include:

* sequential search
* binary search
* selection sort
* insertion sort
* duplicate detection
* mode
* reverse/rotate/swap puzzles
* digit-code puzzles
* developer test mode
* learning mode

Those are later learning expansion phases and must not be implemented in v0.1.

## Data Model for v0.1

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

## Target v0.1 Menu

0. Exit
1. View Market
2. Advance Day
3. View Portfolio
4. Buy Stock
5. Sell Stock

## Later Learning Expansion Features

After v0.1, add:

* market analysis
* highest/lowest price
* biggest gainer/loser
* sum and average
* count above threshold
* sequential search by ticker
* sequential search by stock ID
* binary search by sorted stock ID
* selection sort by price
* selection sort by ID
* insertion sort by daily change
* duplicate price detection
* mode price
* reverse market order
* swap pairs
* rotate market
* research cost table
* digit-code puzzle
* developer test mode
* learning/debug mode

## Learning Philosophy

Every feature must teach a Java concept.

Examples:

* View Market teaches indexed for loops.
* Advance Day teaches Math.random and array updates.
* Buy/Sell teaches index validation and array mutation.
* Portfolio Value teaches accumulator variables.
* Market Analysis teaches max/min traversal.
* Sequential Search teaches linear scanning.
* Binary Search teaches low/high/mid and while loops.
* Sorting teaches nested loops and shifting.
* Digit Code teaches %, /, and while loops.

## Anti-Cheating Rule

Do not hide the learning by using advanced shortcuts. The student should see the actual loops, array indexing, searching logic, and sorting logic.
