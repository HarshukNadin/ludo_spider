# Iteration 2 acceptance test (T-0.4)

Product owner: Mahmoud Mokary. Goal of Iteration 2 (SDP): all rules on all levels, questions,
game end and history, with unit tests. This list checks US-05 to US-08 against the SRS.
US-09 (unit tests) is checked in `test_coverage.md`.

How we run it: the real application (`AppController`) in a 1280 x 720 window, with a fresh copy
of `data/questions.csv` and no history file. Games are set up through the New game screen, and
the full game for US-08 is played only with the Roll, Piece and Next player buttons. Where a check
needs a fixed position or roll (for example "a Double Block in front of the piece"), it is set
in the Model directly, so the result does not depend on luck.

| ID | Check | SRS | Result |
|---|---|---|---|
| US-05.1 | Medium has the 52-square track and a 64-square outer route | FR-2.03 | Pass |
| US-05.2 | Medium: 8 protection, 8 arrow, 6 question, 2 surprise and 4 start squares | FR-2.03 | Pass |
| US-05.3 | The start squares are the outer corners next to each castle | FR-2.06 | Pass |
| US-05.4 | The bigger Medium board still fits in the 1280 x 720 window | EI-UI-2 | Pass |
| US-05.5 | Hard has the track and the 64 and 72-square outer routes | FR-2.04 | Pass |
| US-05.6 | Hard: 12 protection, 12 arrow, 9 question, 3 surprise and 4 start squares | FR-2.04 | Pass |
| US-05.7 | Special squares are in different places in two new games | FR-2.05 | Pass |
| US-05.8 | Start and arrow squares never hold a special square | FR-2.05 | Pass |
| US-06.1 | Medium rolls a Boost die (1-3), Hard an UltraBoost die (1-6) | FR-3.03 | Pass |
| US-06.2 | On a shared route a piece moves Normal x Boost; on the home path the Normal die only | FR-3.03, CR-01 | Pass |
| US-06.3 | A 6 gives another roll; the third 6 in a row is cancelled | FR-3.06 | Pass |
| US-06.4 | Landing on one opponent piece on the outer route sends it to its castle | FR-3.08 | Pass |
| US-06.5 | A Double Block stops the opponent on the square before it | FR-3.09, CR-02 | Pass |
| US-06.6 | Final Home needs the exact roll; the reason is shown | FR-3.10 | Pass |
| US-06.7 | At its arrow a piece moves from the outer route to the inner one | FR-3.05 | Pass |
| US-07.1 | Medium has 2 easy, 2 medium and 2 hard question squares | FR-2.03 | Pass |
| US-07.2 | Each question square asks a question of its own level, with four answers | FR-4.03 | Pass |
| US-07.3 | A right answer moves 1, 2 or 3 forward, a wrong one the same number back | FR-4.05 | Pass |
| US-07.4 | After a wrong answer the right answer is shown | FR-4.04 | Pass |
| US-08.1 | The game ends when all but one player finished; the last one gets the last place | FR-5.01, FR-5.02 | Pass |
| US-08.2 | The end screen shows the winner, the ranking, the level and the game time | FR-5.03 | Pass |
| US-08.3 | The end screen says the game was saved | FR-5.07 | Pass |
| US-08.4 | A new history file starts with the course header | FR-5.04, FR-5.07 | Pass |
| US-08.5 | The record has id 1, the level, the date and time, the names in order, an empty 4th place and the duration | FR-5.04 to FR-5.06 | Pass |
| US-08.6 | Game history from the end screen lists the saved game | FR-5.08 | Pass |
| US-08.7 | A damaged history file shows an error, not a partial list | FR-5.09 | Pass |

## Result (20/11/2026)

26 of 26 checks passed. US-05 to US-08 are accepted; US-09 is accepted in `test_coverage.md`.

Details worth keeping from the run:

- US-05.4: the Medium board area was 896 x 668 pixels in the 1280 x 720 window, so nothing is cut off.
- US-08.5: the saved line was `1,hard,20/11/2026;23:29:34,Nadin,David,Bothyna,,00:00:49`, a 3-player game
  played with the buttons only.
- US-08.7: the history screen said "The history file could not be read: Line 2 of the history file is damaged".

Other checks done for this iteration:

- 134 JUnit tests pass, and the Model has 96% line coverage (`test_coverage.md`).
- 120 complete games were played by a script (40 per level, 4 players, random choices, real
  question file). Every game reached Game over, so no phase can get stuck on the outer routes.

Found while checking the rules against the SRS, and fixed before this run:

- Question squares did not have their own level, and steps followed the game's level (FR-4.03, FR-4.05). Nadin, PR #36.
- A wrong answer did not show the right answer (FR-4.04), and going 3 steps back did not capture (FR-4.06). Nadin, PR #36.
- The UltraBoost surprise gave the extra roll to the next player (CR-07). Mahmoud, PR #37.
- A history file without a final line break, or with a damaged line, broke saving (FR-5.07). Bothyna, PR #38.

Still open for the team:

- Surprise squares (US-10) work, but the "opponent's dice" outcome has no effect yet; it is planned for Iteration 3 with the rest of US-10.
