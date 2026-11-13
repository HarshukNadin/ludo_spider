# Iteration 1 acceptance test (T-4.5)

Product owner: David Koliev. Goal of Iteration 1 (SDP): a runnable MVC skeleton where players
set up a game and move pieces on Easy. This list checks US-01 to US-04 against the SRS.

How we run it: the real application (`AppController`) in a 1280 x 720 window, with a fresh copy
of `data/questions.csv` and no history file. Each check clicks the real buttons on the screens.
Where a check needs a fixed dice value (for example "roll a 6"), the roll is given to the
Model directly, so the result does not depend on luck.

| ID | Check | SRS | Result |
|---|---|---|---|
| US-01.1 | The menu shows New game, Game history, Question management and Exit | EI-UI-1, FR-1.01 | Pass |
| US-01.2 | The menu also offers How to play | CR-13 | Pass |
| US-01.3 | How to play opens the rules screen | CR-13 | Pass |
| US-01.4 | Game history opens the history screen | EI-UI-1 | Pass |
| US-02.1 | New game asks for the number of players and the difficulty | FR-1.01 | Pass |
| US-02.2 | Start game with nothing chosen shows what to fix | FR-1.07, EI-UI-4 | Pass |
| US-02.3 | The game does not start while something is missing | FR-1.07 | Pass |
| US-02.4 | 4 players shows 4 name rows | FR-1.02 | Pass |
| US-02.5 | Changing to 3 players shows 3 rows | FR-1.02 | Pass |
| US-02.6 | Easy, Medium and Hard can be chosen | FR-1.03 | Pass |
| US-02.7 | A color picked by one player is disabled for the others | FR-1.05 | Pass |
| US-02.8 | A name over 15 characters is rejected, with its length | FR-1.04 | Pass |
| US-02.9 | A repeated name is rejected, upper and lower case count as the same | FR-1.04 | Pass |
| US-02.10 | A player without a color is asked to pick one | FR-1.05, FR-1.07 | Pass |
| US-02.11 | Valid input stays after an error | EI-UI-4 | Pass |
| US-02.12 | Fixing a field removes only its own message | EI-UI-4 | Pass |
| US-02.13 | A valid setup opens the game screen | FR-1.08 | Pass |
| US-02.14 | Every player gets four pieces in the castle | FR-1.06 | Pass |
| US-03.1 | The Easy board has 52 shared squares | FR-2.02 | Pass |
| US-03.2 | 4 start, 4 arrow, 4 protection squares, no question or surprise squares | FR-2.02 | Pass |
| US-03.3 | Start and arrow squares are in their fixed places | FR-2.06 | Pass |
| US-03.4 | The whole board fits in the 1280 x 720 window without scrolling | EI-UI-2 | Pass |
| US-03.5 | The active player's name is shown on the turn panel in their color | EI-UI-2 | Pass |
| US-04.1 | All pieces in the castle and a roll of 1-5: no move, the reason is shown | FR-3.07 | Pass |
| US-04.2 | Next player passes the turn to the next player in order | FR-3.01 | Pass |
| US-04.3 | After the last player the first player plays again | FR-3.01 | Pass |
| US-04.4 | A 6 lets a piece leave the castle | FR-3.04 | Pass |
| US-04.5 | Another player's piece cannot be moved | FR-3.01 | Pass |
| US-04.6 | The chosen piece is on its start square and the board shows it | FR-3.05, FR-3.11 | Pass |
| US-04.7 | A 6 gives the same player another roll | FR-3.06 | Pass |
| US-04.8 | A roll of 4 moves the piece 4 squares | FR-3.02, FR-3.05 | Pass |
| US-04.9 | The turn passes after a roll that is not a 6 | FR-3.01 | Pass |
| US-04.10 | The Normal die gives 1 to 6, about equally often (6000 rolls) | FR-3.02 | Pass |

## Result (13/11/2026)

36 of 36 checks passed. All four user stories of Iteration 1 are accepted.

Details worth keeping from the run:

- US-02.8: the message was "This name has 18 characters. Use 15 or fewer." and the counter showed 18/15 in red.
- US-03.4: the board area was 896 x 668 pixels inside the 1280 x 720 window, so nothing is cut off.
- US-04.10: 6000 rolls gave 1 = 992, 2 = 1010, 3 = 987, 4 = 993, 5 = 986, 6 = 1032.

Other checks done for this iteration:

- 57 JUnit tests pass (Model rules, setup validation, board layout, the setup screen).
- 60 complete games were played by a script (20 per difficulty, 4 players, random choices, real
  question file). Every game reached Game over; 620 questions were answered on the way, so no
  phase can get stuck.

Found during the run and fixed:

- The Game history screen still had the old grey look and the game log had a light scroll bar.
  Bothyna fixed both in PR #15 (look only, no rule change).

Done after the run:

- The jar from the submission zip ran on a second computer (risk R-03, Mahmoud).

Still open for the team:

- EI-UI-5 (5 first-time users finish setup in 2 minutes) is planned for the usability test in Iteration 3 (US-15).
