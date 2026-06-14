# Market Mayhem Agent Instructions

## Project Purpose

Market Mayhem is a terminal-based Java stock trading game built to help the student learn Java quickly through gameplay.

The game must teach:

* for loops
* while loops
* 1D arrays
* array traversal
* methods
* static methods
* method parameters and return values
* Math.random
* Integer.MIN_VALUE and Integer.MAX_VALUE
* sequential / linear search
* binary search
* selection sort
* insertion sort
* max/min traversal
* sum and average
* duplicate detection
* mode
* counting elements that meet criteria
* reversing arrays
* swapping pairs
* rotating/shifting arrays
* digit processing with %, /, and while loops
* basic input validation
* off-by-one error prevention

## First Version Scope

The first playable version, v0.1, should include:

* terminal menu
* market display
* stock arrays
* random daily price changes
* cash
* portfolio value
* net worth
* buy stock
* sell stock
* basic win/loss condition

Search, sort, digit puzzles, developer tests, and learning mode will be added after v0.1.

## Required Technical Constraints

* Use Java only.
* Use one file at first: MarketMayhem.java.
* Use public class MarketMayhem.
* Use static variables and static methods at the beginner stage.
* Use Scanner for input.
* Use terminal input/output only.
* Use traditional indexed for loops when indexes matter.
* Use while loops for the main game loop, binary search, insertion sort, and digit-processing puzzles.
* Use arrays, not ArrayList, for the first full learning version.
* Use manually written searching and sorting algorithms.
* Use manually written sequential search, binary search, selection sort, and insertion sort when those later features are implemented.
* Use beginner-readable code.

## Forbidden Until Later

Do not use:

* ArrayList
* HashMap
* streams
* lambdas
* Collections.sort
* Arrays.sort
* Arrays.binarySearch
* object-oriented extra classes like Stock or Player
* JavaFX
* Swing
* libGDX
* Maven
* Gradle
* databases
* file saving
* graphics
* networking
* external libraries
* external dependencies

## Code Style

* Prefer clear method names.
* Keep main short.
* Move logic into methods.
* Every important game action should become a method.
* Prefer methods with parameters and return values when appropriate.
* Use constants where helpful.
* Validate all user indexes before accessing arrays.
* Never access arr[arr.length].
* Avoid hard-coding array length in loops.
* Use arr.length instead.

## Parallel Array Rule

When the game uses parallel arrays, related values at the same index must stay together.

Example:
tickers[i], stockIds[i], prices[i], previousPrices[i], sharesOwned[i], and riskLevels[i] must describe the same stock.

If a sort, reverse, rotate, or swap changes one stock’s position, all parallel arrays must move together.

## Build and Run Commands

Use:
javac MarketMayhem.java
java MarketMayhem

After MarketMayhem.java exists, every implementation phase must compile with javac.

## Review Rules

For every review/autofix task, check:

* Does the code compile?
* Does the menu run?
* Are there off-by-one errors?
* Are invalid indexes handled?
* Are arrays kept the same length?
* Are parallel arrays preserved?
* Are any forbidden advanced features used?
* Are built-in search/sort methods avoided?
* Does each feature teach the intended Java concept?

## Done Means

A phase is only done when:

* the requested files/features exist
* the project still follows beginner Java constraints
* future phases are not accidentally implemented early
* the instructions are clear enough for a new Codex session to continue correctly
