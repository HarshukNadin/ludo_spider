package Model;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Random;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/** A turn where no piece can move (FR-3.07): the game says why and waits for passTurn(). */
class NoMoveTest {
    private Player red, green;
    private Game game;

    @BeforeEach
    void setUp() {
        red = new Player("Bothyna", PlayerColor.RED);
        green = new Player("Mahmoud", PlayerColor.GREEN);
        game = new Game(Difficulty.EASY, List.of(red, green), null, new Random(2));
        for (Square s : game.getBoard().getTrack())
            if (s.getType() != SquareType.START && s.getType() != SquareType.ARROW) s.setType(SquareType.NORMAL);
    }

    @Test
    void allPiecesInTheCastleNeedASix() {
        game.roll(new DiceResult(4, 1));
        assertEquals(GamePhase.NO_MOVE, game.getPhase());
        assertEquals("You need a 6 to bring a piece out.", game.getNoMoveReason());
        assertEquals(red, game.getCurrentPlayer(), "the turn waits until the player passes it");
    }

    @Test
    void passTurnGoesToTheNextPlayerAndMovesNothing() {
        red.getPiece(1).setProgress(Board.FINAL_INDEX - 1);
        game.roll(new DiceResult(5, 1));
        game.passTurn();
        assertEquals(green, game.getCurrentPlayer());
        assertEquals(GamePhase.WAITING_FOR_ROLL, game.getPhase());
        assertEquals(Board.FINAL_INDEX - 1, red.getPiece(1).getProgress());
        assertNull(game.getNoMoveReason());
    }

    @Test
    void rollTooHighForFinalHome() {
        red.getPiece(1).setProgress(Board.FINAL_INDEX - 2);
        game.roll(new DiceResult(3, 1));
        assertEquals("Your pieces need an exact roll to reach Final Home.", game.getNoMoveReason());
    }

    @Test
    void doubleBlockRightInFront() {
        red.getPiece(1).setProgress(10);
        int square = Board.toTrackIndex(PlayerColor.RED, 11);
        int gp = Math.floorMod(square - PlayerColor.GREEN.getStartSquare(), Board.TRACK_LENGTH);
        green.getPiece(1).setProgress(gp);
        green.getPiece(2).setProgress(gp);
        game.roll(new DiceResult(2, 1));
        assertEquals("A Double Block is in front of your pieces.", game.getNoMoveReason());
    }

    @Test
    void cannotRollAgainOrPassWhenAMoveExists() {
        game.roll(new DiceResult(4, 1));
        IllegalStateException e = assertThrows(IllegalStateException.class, () -> game.roll(new DiceResult(6, 1)));
        assertEquals("Bothyna cannot move. Click Next player to go on.", e.getMessage());

        game.passTurn();
        game.roll(new DiceResult(6, 1));
        assertEquals(GamePhase.CHOOSING_PIECE, game.getPhase());
        assertThrows(IllegalStateException.class, () -> game.passTurn(), "a legal move must not be skipped");
    }
}
