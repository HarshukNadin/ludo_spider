# Iteration 2 plan

Iteration 2 runs from 16/11/2026 to 06/12/2026 (Exercise 5).

## Roles

Roles rotate every iteration (SDP 5.2.1). Everyone still writes code.

| Role | Iteration 1 | Iteration 2 |
|---|---|---|
| Scrum Master | Nadin Harshuk | Bothyna Hakroush |
| Product Owner | David Koliev | Mahmoud Mokary |

Hand-over (risk R-06): Nadin passes the scrum report, the review levels and the merge routine to Bothyna. David passes the SRS change log and the acceptance checklist to Mahmoud.

## Goal

All rules on all levels, questions, game end and history, plus JUnit tests (US-05 to US-09).
Exercise 5 also asks for four design patterns, one per student. The SDP put them in Iteration 3 (US-14), so we move them forward to this iteration. The PO records this as a change in the SRS change log (T-0.5).

## Where we start

A lot of the game logic was written early, in Iteration 1. It is not accepted yet, so in this iteration we check it against the SRS, fix what is wrong and test it.

- Already in the code:
  - Boost and UltraBoost dice
  - the six rule
  - captures
  - Double Blocks
  - exact home
  - Question squares
  - the history CSV and history screen
  - random special squares on Medium and Hard
- Missing:
  - the 64- and 72-square outer routes of Medium and Hard (FR-2.03, FR-2.04)
  - the end screen with ranking, difficulty and game time (FR-5.03)
  - tests for most of the rules

## Tasks

| ID | Story | Task | SRS | Owner | Review |
|---|---|---|---|---|---|
| T-5.1 | US-05 | Route and OuterBoard model: outer routes joined by the Arrow squares | FR-2.03, 2.04, 2.05, 3.05 | David Koliev | L2 |
| T-5.2 | US-05 | **Factory:** BoardFactory builds the Easy, Medium or Hard board | FR-2.01–2.04 | David Koliev | L1 |
| T-5.3 | US-05 | Draw the outer routes and special squares on Medium and Hard | FR-1.08, EI-UI-2 | Nadin Harshuk | L1 |
| T-6.1 | US-06 | Check and finish the move rules: multiplier only on shared routes, six rule, capture, Double Block, exact home, backward stop at Start | FR-3.03, 3.06, 3.08–3.13 | Mahmoud Mokary | L2 |
| T-6.2 | US-06 | **Strategy:** DiceStrategy with NormalDice, BoostDice and UltraBoostDice | FR-3.03 | Mahmoud Mokary | L1 |
| T-7.1 | US-07 | Question squares: answer effects follow the move rules, no chained effects | FR-4.01–4.07 | Nadin Harshuk | L1 |
| T-7.2 | US-07 | **Observer:** split GameListener into clear game events (move, capture, question, game over) that the game screen and the history saver listen to | DC-2 | Nadin Harshuk | L1 |
| T-8.1 | US-08 | Game end: last player ranked, end screen with ranking, difficulty and game time | FR-5.01–5.03 | Bothyna Hakroush | L1 |
| T-8.2 | US-08 | History: check the CSV against the course template, error when it cannot be written or read | FR-5.04–5.09 | Bothyna Hakroush | L2 |
| T-8.3 | US-08 | **State:** one class per game phase instead of the GamePhase checks | DC-2 | Bothyna Hakroush | L2 |
| T-9.1 | US-09 | JUnit tests for your own tasks; 70% Model coverage | SDP 7.2 | Each owner | L1 |
| T-0.3 | – | Scrum meetings, scrum report, merges | – | Bothyna Hakroush (SM) | – |
| T-0.4 | – | Acceptance check for US-05 to US-09, coverage report | – | Mahmoud Mokary (PO) | L1 |
| T-0.5 | – | SRS change: design patterns move to Iteration 2 | – | Mahmoud Mokary (PO) | L1 |
| T-0.6 | – | Design patterns file (iteration2_dp_Spider): each owner writes their own pattern | – | All, put together by Bothyna | L1 |
| T-0.7 | – | System report: add the Iteration 2 features, inputs, results and tests | – | Nadin Harshuk | L1 |
| T-0.8 | – | Run the JAR on two computers (R-03) | – | Mahmoud Mokary | – |

### Notes on the patterns

- Singleton and MVC do not count (Exercise 5). GameRules was planned as a Singleton, so it is not one of the four.
- Observer already exists in a simple form (one `gameChanged` method). T-7.2 rebuilds it, so the pattern file has to show the new events and which classes listen to them.
- If someone picks a pattern that was not taught in the tutorial, they send the TA its name, a description and a source by 30/11 and wait for approval.

## Dates

| Date | Milestone |
|---|---|
| 22/11 | Planning meeting, hand-over, every task has a branch |
| 27/11 | Model tasks merged (T-5.1, T-5.2, T-6.1, T-6.2, T-8.3) |
| 30/11 | Last day to ask the TA about a pattern that was not taught |
| 02/12 | View tasks merged (T-5.3, T-7.2, T-8.1) |
| 04/12 | Code freeze; tests pass |
| 05/12 | Acceptance check, JAR on two computers, reports ready |
| 06/12 | Submit before 23:59 |

## Way of working

Same as Iteration 1:
- One branch per task in HarshukNadin/ludo_spider.
- One PR per task, with the task ID in the title.
- Another member approves the PR. L2 tasks need two reviewers.
- The Scrum Master merges.
- Definition of Done: SDP 5.3.4, including Javadoc on public methods.
