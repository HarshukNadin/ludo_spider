package Model;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Random;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/** Move rules on Medium, where the Boost die multiplies the Normal die (FR-3.04, FR-3.05, FR-3.11). */
class MoveTest {
    private Player red, green;
    private Game game;

    @BeforeEach
    void setUp() {
        red = new Player("Mahmoud", PlayerColor.RED);
        green = new Player("Nadin", PlayerColor.GREEN);
        game = new Game(Difficulty.MEDIUM, List.of(red, green), null, new Random(5));
        // Plain squares only, so no question or surprise changes where a piece ends up.
        for (Square s : game.getBoard().getTrack())
            if (s.getType() != SquareType.START && s.getType() != SquareType.ARROW) s.setType(SquareType.NORMAL);
    }

    // ---------- Leaving the castle (FR-3.04) ----------

    @Test
    void castlePiecesCannotMoveWithoutASix() {
        game.roll(new DiceResult(5, 3));
        assertTrue(game.legalMoves().isEmpty());
        assertEquals(green, game.getCurrentPlayer(), "nothing can move, so the turn passes");
    }

    @Test
    void sixTimesBoostOnlyPutsThePieceOnItsStartSquare() {
        game.roll(new DiceResult(6, 3));
        game.move(red.getPiece(1));
        assertEquals(0, red.getPiece(1).getProgress(), "the Boost die is not used when leaving the castle");
        assertEquals(PlayerColor.RED.getStartSquare(), red.getPiece(1).getTrackIndex());
    }

    @Test
    void boostedTotalOfSixIsNotASix() {
        game.roll(new DiceResult(3, 2));
        assertTrue(game.legalMoves().isEmpty(), "3 x 2 moves 6 squares but does not open the castle");
    }
}
