# Market Mayhem Agent Instructions

## Project Purpose

Market Mayhem is a terminal-based Java stock trading game built to help the student learn Java quickly through gameplay.

The project is now entering:

Market Mayhem v0.2 - Java Learning Edition

The game must still be playable, but it must not merely use Java internally. It must show Java code snippets, algorithm traces, and short concept checks to the player.

The game must teach:

* for loops
* while loops
* 1D arrays
* array traversal
* array indexing
* array.length
* methods
* static methods
* method parameters and return values
* Math.random
* Integer.MIN_VALUE and Integer.MAX_VALUE
* sequential / linear search
* binary search
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

## Current Version Scope

The current version, v0.2 - Java Learning Edition, must include:

* all v0.1 playable trading gameplay
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
* Learning Mode
* Java XP
* Java Concept Map
* Loops + Arrays Lab
* Methods Lab
* Search Lab
* Developer Tests

Selection sort, insertion sort, digit puzzles, reverse/rotate/swap puzzles, file saving, graphics, and object-oriented extra classes are later expansion work.

## Required Technical Constraints

* Use Java only.
* Use one main Java file: MarketMayhem.java.
* Use public class MarketMayhem.
* Use static variables and static methods at this beginner stage.
* Use Scanner for input.
* Use terminal input/output only.
* Use traditional indexed for loops when indexes matter.
* Use while loops for the main game loop and binary search.
* Use arrays, not ArrayList.
* Use manually written sequential search.
* Use manually written binary search.
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
* Keep learning notes concise and useful.

## Parallel Array Rule

When the game uses parallel arrays, related values at the same index must stay together.

Example:
tickers[i], stockIds[i], prices[i], previousPrices[i], sharesOwned[i], and riskLevels[i] must describe the same stock.

If a sort, reverse, rotate, or swap changes one stock's position in a later phase, all parallel arrays must move together.

## Learning Feature Rule

Learning features must make Java visible to the player.

Use:

* printed Java snippets
* indexed loop traces
* search comparison counts
* beginner concept checks
* short explanations of why the code works

Do not hide the learning by using advanced shortcuts.

## Build and Run Commands

Use:
javac MarketMayhem.java
java MarketMayhem

Every implementation phase must compile with javac.

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
* Do Learning Mode, Java XP, labs, and Developer Tests still work?

## Done Means

A phase is only done when:

* the requested files/features exist
* the project still follows beginner Java constraints
* future phases are not accidentally implemented early
* the instructions are clear enough for a new Codex session to continue correctly
* the Java game compiles and the requested smoke tests pass
