# Unit tests and coverage (T-9.1, US-09)

Owner: Mahmoud Mokary. Target from the SDP (7.2): JUnit unit tests for the Model with at least
70% coverage.

How we measured: EclEmma, the coverage tool in the SDP (6.2), which runs JaCoCo 0.8.11. All
134 JUnit 5 tests were run with the coverage agent on 20/11/2026, on main after PR #38.

## Result

| Package | Line coverage | Branch coverage |
|---|---|---|
| **Model** | **96% (591 of 614 lines)** | **85%** |
| View | 37% | 29% |
| Controller | 0% | 0% |

The Model is above the 70% target, so US-09 is accepted. The View and the Controller are Swing
screens; they are checked by the acceptance test, which clicks the real buttons
(`acceptance_test.md`), and by a few View tests for the board drawing, the setup screen and the
end screen.

## Model classes

| Class | Lines covered | Line % | Branch % |
|---|---|---|---|
| Board | 46 / 46 | 100 | 100 |
| OuterBoard | 23 / 23 | 100 | 100 |
| Route | 13 / 15 | 87 | 83 |
| BoardFactory | 3 / 4 | 75 | 67 |
| Square | 13 / 14 | 93 | 100 |
| Game | 192 / 199 | 96 | 85 |
| GameState and its 5 states | 24 / 25 | 96 | - |
| GameEvent, HistoryRecorder | 18 / 18 | 100 | 75 |
| Dice, NormalDice, BoostDice, UltraBoostDice | 17 / 17 | 100 | 100 |
| Player | 19 / 20 | 95 | 92 |
| Piece | 21 / 22 | 95 | 72 |
| SetupValidation | 35 / 36 | 97 | 88 |
| Question | 19 / 19 | 100 | 55 |
| QuestionBank | 33 / 37 | 89 | 63 |
| GameHistory | 41 / 43 | 95 | 96 |
| CsvUtil | 20 / 20 | 100 | 81 |
| Surprise | 16 / 18 | 89 | 75 |

The lines that are not covered are mostly error branches that cannot happen in a game, for
example a route number that does not exist or the default branch of a switch over all levels.

## Test classes

| Test class | Tests | What it checks |
|---|---|---|
| OuterBoardTest | 13 | Routes of Medium and Hard, square counts, paths, capture on the rings |
| BoardFactoryTest | 6 | The factory gives each level the right board |
| DiceStrategyTest | 6 | Each die strategy and its faces |
| GameStateTest | 6 | Each game state allows only its own action |
| GameEventTest | 6 | Game events and saving at game over |
| QuestionSquareTest | 9 | Question levels, steps, feedback, no repeats |
| RulesTest | 13 | Move rules on Medium and Hard |
| HistoryFileTest | 11 | The history file against the course template |
| EndViewTest | 5 | The end screen |
| Other test classes | 59 | Setup, turns, moves, no move, board, CSV files, board drawing with the rings |
