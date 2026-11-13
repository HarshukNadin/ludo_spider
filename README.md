# Spider Ludo

Educational Ludo game for 2–4 players on one computer, written in Java (Swing). Questions on the board are about Agile, MVC, design patterns, testing and software architecture. Group **Spider**, Software Engineering course, University of Haifa.

## Run

Java 17 or newer. Keep the `data/` folder next to the jar.

```bash
java -jar spider_ludo.jar
```

Double clicking the jar also works; the game finds `data/` next to it. A different data folder can be passed as the first argument.

Build the jar from the source:

```bash
./build.sh        # macOS / Linux
build.bat         # Windows
```

Eclipse: import the folder as a Java project, mark `src` and `test` as source folders, add JUnit 5 to the build path and run `Main` with the project folder as working directory.

## Iteration 1 (submitted 15/11/2026)

Main menu, game setup with per-field messages, the board for all three difficulties, turns, the Normal die, leaving the castle on a 6, moving pieces, and turns with no legal move (US-01 to US-04). Rules from later iterations that are already in the Model (Boost dice, capture, Double Block, questions, surprises, game end and history) work and are unit tested.

Documents for this iteration are in `docs/iteration1/`: system report, acceptance test, class diagram and the architecture questions.

## Structure

```
src/
  Main.java
  Model/       game rules and CSV files, no Swing
  View/        Swing screens: menu, how to play, setup, game, board, question dialog, history
  Controller/  AppController, SetupController, GameController, HistoryController
test/          JUnit 5 tests (57)
data/          questions.csv (42 questions), game_history.csv (course format)
docs/          iteration documents
```

The packages follow MVC as the course requires. The Model tells the controller about every change through `GameListener` (Observer).

## Team

Nadin Harshuk, David Koliev, Bothyna Hakroush, Mahmoud Mokary. Every task has its own branch and pull request; another member approves it and the Scrum Master merges.
