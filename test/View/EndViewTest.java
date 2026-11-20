package View;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Random;
import org.junit.jupiter.api.Test;

import Model.DiceResult;
import Model.Difficulty;
import Model.Game;
import Model.GamePhase;
import Model.GameRecord;
import Model.Player;
import Model.PlayerColor;

/** The end screen shows the ranking, the level, the game time and the save result (FR-5.03, FR-5.07). */
class EndViewTest {

    /** Plays a whole game with seeded dice; the View cannot place pieces by hand. */
    private static Game finishedGame() {
        Player red = new Player("Dana", PlayerColor.RED), green = new Player("Omar", PlayerColor.GREEN),
               blue = new Player("Lior", PlayerColor.BLUE);
        Game game = new Game(Difficulty.MEDIUM, List.of(red, green, blue), null, new Random(1));
        Random pick = new Random(2);
        while (game.getPhase() != GamePhase.GAME_OVER) {
            switch (game.getPhase()) {
                case WAITING_FOR_ROLL -> game.roll();
                case NO_MOVE -> game.passTurn();
                case CHOOSING_PIECE -> game.move(game.legalMoves().get(pick.nextInt(game.legalMoves().size())));
                default -> throw new IllegalStateException("no questions without a question bank");
            }
        }
        return game;
    }

    @Test
    void showsTheRankingInFinishingOrder() {
        Game game = finishedGame();
        EndView v = new EndView();
        v.show(game, null, null);
        List<String> rows = v.getRows();
        assertEquals(3, rows.size());
        assertTrue(rows.get(0).startsWith("1st") && rows.get(0).endsWith(game.getFinishOrder().get(0).getName()));
        assertTrue(rows.get(2).startsWith("3rd") && rows.get(2).endsWith(game.getFinishOrder().get(2).getName()));
    }

    @Test
    void showsTheLevelAndGameTime() {
        EndView v = new EndView();
        v.show(finishedGame(), null, null);
        assertTrue(v.getDetails().startsWith("Medium board, game time 00:00:"), v.getDetails());
    }

    @Test
    void saysWhereTheGameWasSaved() {
        EndView v = new EndView();
        v.show(finishedGame(), new GameRecord(7, Difficulty.MEDIUM, "20/11/2026 21:00", List.of("Dana", "Omar", "Lior"), "00:10:00"), null);
        assertEquals("Saved to the game history as game 7.", v.getSavedText());
    }

    @Test
    void showsTheSaveErrorButStillTheResults() {
        EndView v = new EndView();
        v.show(finishedGame(), null, "The result could not be saved: disk full");
        assertEquals("The result could not be saved: disk full", v.getSavedText());
        assertEquals(3, v.getRows().size());
    }

    @Test
    void placeNames() {
        assertEquals("1st", EndView.placeName(1));
        assertEquals("2nd", EndView.placeName(2));
        assertEquals("3rd", EndView.placeName(3));
        assertEquals("4th", EndView.placeName(4));
    }
}
