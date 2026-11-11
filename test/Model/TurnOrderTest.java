package Model;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Random;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TurnOrderTest {
    private Player red, green, yellow;
    private Game game;

    @BeforeEach
    void setUp() {
        red = new Player("Nadin", PlayerColor.RED);
        green = new Player("David", PlayerColor.GREEN);
        yellow = new Player("Bothyna", PlayerColor.YELLOW);
        game = new Game(Difficulty.EASY, List.of(red, green, yellow), null, new Random(1));
    }

    @Test
    void turnsFollowTheSetupOrderAndWrapAround() {
        assertEquals(red, game.getCurrentPlayer(), "the first player in the setup starts");
        for (Player expected : List.of(green, yellow, red, green)) {
            game.roll(new DiceResult(2, 1)); // nobody is out of the castle, so the turn passes
            assertEquals(expected, game.getCurrentPlayer());
            assertEquals(GamePhase.WAITING_FOR_ROLL, game.getPhase());
        }
    }

    @Test
    void anotherPlayersPieceCannotBeMoved() {
        game.roll(new DiceResult(6, 1));
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> game.move(green.getPiece(1)));
        assertTrue(e.getMessage().contains("Nadin's turn"), e.getMessage());
        assertEquals(red, game.getCurrentPlayer(), "the wrong click does not change the turn");
        assertEquals(PieceState.IN_CASTLE, green.getPiece(1).getState());
        game.move(red.getPiece(1));
        assertEquals(PieceState.ON_TRACK, red.getPiece(1).getState());
    }

    @Test
    void cannotRollAgainBeforeChoosingAPiece() {
        game.roll(new DiceResult(6, 1));
        IllegalStateException e = assertThrows(IllegalStateException.class, () -> game.roll(new DiceResult(4, 1)));
        assertEquals("Nadin has to choose a piece to move first.", e.getMessage());
        assertEquals(6, game.getLastRoll().normal(), "the first roll is kept");
    }

    @Test
    void cannotMoveBeforeRolling() {
        IllegalStateException e = assertThrows(IllegalStateException.class, () -> game.move(red.getPiece(1)));
        assertEquals("Nadin has to roll the dice first.", e.getMessage());
    }
}
