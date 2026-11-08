# Spider Ludo

Educational Ludo game for 2–4 players, written in Java (Swing). Group **spider**, Software Engineering course, University of Haifa.

## Steps in this version

1. **Project setup**: plain Java 17+ project, no external libraries. Data files in `data/`.
2. **Model**: classes from our SRS entities: `Game`, `Player`, `Piece`, `Board`, `Square`, `Dice`, `Question`, `QuestionBank`, `GameHistory`. Rules: leave the castle on a 6, extra roll on a 6, third 6 cancelled, captures, Double Block, exact roll to the Final Home, question and surprise squares, finishing order.
3. **MVC**: `model` (logic, no UI), `view` (Swing screens), `controller` (connects them). The Model notifies the controller through `GameListener` (Observer pattern).

## Structure

```
src/spider/ludo/
  Main.java
  model/       game logic and CSV files
  view/        MainFrame, MainMenuView, SetupView, GameView, BoardPanel, HistoryView
  controller/  AppController, SetupController, GameController, HistoryController
test/          JUnit 5 tests for the model
data/          questions.csv (30 questions), game_history.csv (course format)
```

## Run

Eclipse: File > Import > Existing folder as Java project (or new Java project pointing at this folder), mark `src` and `test` as source folders, add JUnit 5 to the build path, run `spider.ludo.Main`. The working directory must be the project folder so `data/` is found.

Command line:

```bash
javac -d out $(find src -name '*.java')
java -cp out spider.ludo.Main
```

## Next steps

Extra outer routes for Medium/Hard, arrow squares, the "opponent dice" surprise, and the admin screen for managing questions.
