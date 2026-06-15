# Market Mayhem Agent Instructions

## Project Purpose

Market Mayhem is a terminal-based Java stock trading game built to help the student learn Java quickly through gameplay.

The current implementation phase is:

Market Mayhem: Java Trading Academy - Mixed Review Boss + Final Polish

This phase completes the core teaching version of Market Mayhem v0.2 by fully building the Mixed Review Boss, adding the Java Trading License reward, polishing the final score summary, and preserving every completed academy floor and trading unlock. The academy remains the main learning hub. Codex builds the Java program. The student plays the terminal game. The student must not edit Java source files to learn Java.

The game itself must teach Java through:

* in-game questions
* loop and array traces
* prediction prompts
* short bug-fix challenges
* search comparison counts
* immediate feedback
* mastery-gated trading unlocks

The game must remain playable as a trading game, but learning is no longer optional reading. The player learns by playing the terminal game and answering challenges through Scanner input.

## Non-Negotiable Learning Rules

* No passive XP is allowed.
* Reading text, opening the concept map, viewing a Java snippet, or watching an explanation must never award XP by itself.
* XP requires a correct answer or a completed challenge.
* Every lab, floor, quiz, trace, and challenge must ask the player before revealing the answer.
* Wrong answers must give short, useful explanations.
* Wrong answers must not award XP.
* The game must track mastery by topic.
* Trading advantages must unlock only after Java mastery.
* The student learns by playing the terminal game, not by editing MarketMayhem.java.
* Do not create StudentWork.java.
* In-game bug-fix challenges must be answered inside the game, such as by choosing a fixed line or typing a missing expression. They must not require the student to edit source files.

## Required Academy Gameplay Loop

Every academy interaction must follow this loop:

1. Briefing
2. Challenge
3. Player answer
4. Immediate feedback
5. Explanation
6. XP only if correct
7. Unlock trading ability after mastery

Do not print the final answer before the player answers. A Java snippet may be shown during the briefing, but the player must still make a prediction, trace a value, choose a fix, or answer a concept question before the answer is revealed.

## Phase 10 Scope

Phase 10 must preserve all v0.1 playable trading gameplay:

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

Phase 10 must preserve the Java Trading Academy shell:

* Learning Mode
* Java XP that is only earned through correct answers or completed challenges
* topic mastery tracking
* mastery-gated trading advantages
* Java Trading Academy hub
* academy floor cards
* floor status: OPEN, MASTERED, or LOCKED for the Mixed Review Boss
* unlock summary
* Mastery Report
* routing helpers
* askMultipleChoice
* askShortAnswer
* For Loop Floor
* While Loop Floor
* Arrays Floor
* Methods Floor
* Sequential Search Floor
* Binary Search Floor
* Mixed Review Boss
* Java Trading License
* Developer Tests

Phase 10 fully builds the Mixed Review Boss and Java Trading License final reward. For Loop Floor and Market Scanner from Phase 4 must keep working. Arrays Floor and Index Vision from Phase 5 must keep working. Methods Floor and Trade Calculator from Phase 6 must keep working. While Loop Floor and Signal Decoder from Phase 7 must keep working. Sequential Search Floor and Ticker Finder from Phase 8 must keep working. Binary Search Floor and Fast Broker from Phase 9 must keep working. Old passive labs must not be exposed as the main teaching path, and passive labs that award XP for reading are forbidden.

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

Phase 10 must preserve beginner Java constraints: arrays, for loops, while loops, static methods, Scanner input, manual sequential search, manual binary search, no ArrayList, no HashMap, no streams, no lambdas, no Arrays.sort, no Arrays.binarySearch, no Collections.sort, and no java.util.Random.

## Future Floor Challenge Types

Every academy floor will eventually contain these five challenge types:

1. Predict Output
2. Trace Variable
3. Fix Bug
4. Fill Blank
5. Stock Application

Phase 3 created the routing and shell structure for those challenge types. Phase 4 filled the For Loop Floor with full content. Phase 5 filled the Arrays Floor with full content. Phase 6 filled the Methods Floor with full content. Phase 7 filled the While Loop Floor with full content. Phase 8 filled the Sequential Search Floor with full content. Phase 9 filled the Binary Search Floor with full content. Phase 10 fills the Mixed Review Boss and completes the core teaching loop.

## Trading Unlocks

Java mastery unlocks trading advantages by topic:

* For Loops -> Market Scanner
* While Loops -> Signal Decoder
* Arrays -> Index Vision
* Methods -> Trade Calculator
* Sequential Search -> Ticker Finder
* Binary Search -> Fast Broker
* Mixed Review Boss -> Java Trading License

Selection sort, insertion sort, advanced digit puzzles, reverse/rotate/swap puzzles, duplicate detection, mode, file saving, graphics, and object-oriented extra classes are later expansion work.

## Required Academy Modules

### For Loop Floor

Must teach:

* indexed for loop structure
* loop initializer, condition, and update
* array traversal with i
* off-by-one error prevention
* why i < arr.length is safer than i <= arr.length

Challenge examples:

* predict the values of i printed by a loop
* choose the correct loop condition
* trace which stock index is visited next
* fix a loop that would access prices[prices.length]

### While Loop Floor

Must teach:

* while loop conditions
* repeated execution while the condition is true
* loop-control variables
* updating the loop-control variable
* infinite-loop risk
* using while loops when the number of repetitions is not fixed
* digit processing with % and /
* num % 10 for the last digit
* num / 10 for dropping the last digit of a positive integer

Challenge examples:

* predict the output of a countdown while loop
* trace a loop-control variable until the condition becomes false
* fix a loop that never changes its control variable
* fill the % expression that extracts the last digit
* decode a stock signal with repeated digits

### Arrays Floor

Must teach:

* 1D arrays
* array indexing
* array.length
* array traversal
* parallel arrays
* sum and average
* max/min traversal
* counting elements that meet criteria

Challenge examples:

* predict the value at prices[2]
* calculate an average from an int array
* choose which parallel-array fields belong to the same stock
* trace max or min as the loop visits each stock

### Methods Floor

Must teach:

* methods
* static methods
* method parameters
* arguments
* return values
* return statements
* void vs non-void methods
* method decomposition for game actions
* method signatures with public, static, return type, name, and parameter list

Challenge examples:

* choose the correct method call
* identify the return value of a method
* match parameters to arguments
* pick a method signature for a portfolio calculation
* predict the result of calculateTradeValue

### Sequential Search Floor

Must teach:

* manual sequential / linear search
* checking elements from first to last
* using a for loop to scan an array
* comparing each element with a target
* stopping early when the target is found
* returning the found index
* returning -1 when not found
* search comparison counts
* searching String arrays with equalsIgnoreCase
* searching int arrays with ==
* working even when data is unsorted

Challenge examples:

* trace checked indexes while looking for a ticker
* predict the found index
* choose -1 for a missing target
* fill the return value for a found index
* apply ticker search to the live stock market

### Binary Search Floor

Must teach:

* manual binary search
* binary search only works correctly on sorted data
* checking the middle element first
* low, high, and mid
* while (low <= high)
* updating low when the target is larger
* updating high when the target is smaller
* eliminating half of the remaining search range
* returning the found index
* returning -1 when not found
* counting comparisons
* why binary search is often faster than sequential search

Challenge examples:

* identify the sorted-data requirement
* compute mid from low and high
* choose whether low or high moves
* trace comparison counts
* fill the while-loop condition
* apply binary search to stock IDs

### Mixed Review Boss

Must combine:

* loops
* arrays
* methods
* sequential search
* binary search
* input validation
* off-by-one prevention

The Mixed Review Boss should require multiple correct answers before mastery. It should unlock a meaningful trading advantage only after the player demonstrates cross-topic understanding.

Phase 10 Mixed Review Boss requirements:

* locked until all six academy topics are mastered
* asks 10 active questions
* covers for loops, while loops, arrays, methods, sequential search, and binary search
* passing requires at least 8 / 10 correct
* XP requires correct answers
* no answer is revealed before player input
* passing unlocks Java Trading License
* Java Trading License gives a visible 20% final-score bonus

## Mastery Tracking Rule

The game must track mastery by topic. Because this is a beginner one-file Java project, use arrays and static variables, not HashMap or extra classes.

Acceptable implementation shape:

* String[] topicNames
* int[] topicCorrect
* int[] topicAttempts
* boolean[] topicMastered
* boolean[] abilityUnlocked
* int javaXp
* int questionsAnswered
* int questionsCorrect

The arrays must stay parallel: topicNames[i], topicCorrect[i], topicAttempts[i], topicMastered[i], and abilityUnlocked[i] must describe the same academy topic.

Mastery should be based on correct answers or completed challenges. Do not mark a topic mastered because the player opened a menu or read a briefing.

## Trading Unlock Rule

Core trading actions must remain available so the game is playable. Trading advantages are extra tools, hints, or analysis features that unlock after mastery.

Examples of acceptable unlocks:

* For Loop Floor mastery unlocks Market Scanner.
* While Loop Floor mastery unlocks Signal Decoder hints for Advance Day.
* Arrays Floor mastery unlocks Index Vision.
* Methods Floor mastery unlocks Trade Calculator previews for buy and sell calculations.
* Sequential Search Floor mastery unlocks Ticker Finder for Buy Stock.
* Binary Search Floor mastery unlocks Fast Broker for sorted stock ID lookup in Buy Stock.
* Passing Mixed Review Boss unlocks Java Trading License and a 20% final-score bonus.

Do not unlock an advantage from reading text. Do not unlock an advantage before mastery.

## Required Technical Constraints

* Use Java only.
* Use one main Java file: MarketMayhem.java.
* Use public class MarketMayhem.
* Use static variables and static methods at this beginner stage.
* Use Scanner for input.
* Use terminal input/output only.
* Use traditional indexed for loops when indexes matter.
* Use while loops for the main game loop, input validation, and binary search.
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
* java.util.Random
* object-oriented extra classes like Stock or Player
* StudentWork.java
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
* Keep challenge prompts short enough to read in a terminal.
* Print code snippets as beginner-readable Java, not pseudocode.
* Print feedback immediately after an answer.

## Parallel Array Rule

When the game uses parallel arrays, related values at the same index must stay together.

Example:
tickers[i], stockIds[i], prices[i], previousPrices[i], sharesOwned[i], and riskLevels[i] must describe the same stock.

If a sort, reverse, rotate, or swap changes one stock's position in a later phase, all parallel arrays must move together.

Academy mastery arrays must follow the same rule:
topicNames[i], topicCorrect[i], topicAttempts[i], topicMastered[i], and abilityUnlocked[i] must describe the same topic.

## Learning Feature Rule

Learning features must make Java visible to the player and must require interaction before rewards.

Use:

* printed Java snippets
* indexed loop traces
* prediction questions
* search comparison counts
* beginner concept checks
* short in-game bug-fix prompts
* short explanations of why the code works

Do not:

* award XP for reading
* reveal the answer before asking
* hide the learning by using advanced shortcuts
* require the student to edit MarketMayhem.java

## Build and Run Commands

Use:
javac MarketMayhem.java
java MarketMayhem

Every implementation phase must compile with javac. For documentation-only phases, do not edit MarketMayhem.java unless the user explicitly asks for implementation.

## Review Rules

For every review/autofix task, check:

* Does the code compile?
* Does the menu run?
* Are there off-by-one errors?
* Are invalid indexes handled?
* Are arrays kept the same length?
* Are parallel arrays preserved?
* Are academy mastery arrays kept parallel?
* Are any forbidden advanced features used?
* Are built-in search/sort methods avoided?
* Does each feature teach the intended Java concept?
* Does every lab or floor ask before revealing the answer?
* Is XP only awarded for correct answers or completed challenges?
* Do wrong answers give short explanations without XP?
* Are trading advantages gated behind mastery?
* Does Learning Mode, Java XP, Java Trading Academy, Mastery Report, academy floors, Mixed Review Boss, and Developer Tests still work?

## Done Means

A phase is only done when:

* the requested files/features exist
* the project still follows beginner Java constraints
* future phases are not accidentally implemented early
* the docs clearly say the student learns by playing, not by editing Java files
* passive XP is clearly banned
* the Java Trading Academy loop is preserved
* the instructions are clear enough for a new Codex session to continue correctly
* the Java game compiles and the requested smoke tests pass
