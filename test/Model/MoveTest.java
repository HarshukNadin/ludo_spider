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
        assertEquals(GamePhase.NO_MOVE, game.getPhase(), "nothing can move");
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

    // ---------- Moving along the route (FR-3.05) ----------

    @Test
    void trackPieceMovesNormalTimesBoost() {
        red.getPiece(1).setProgress(5);
        game.roll(new DiceResult(2, 3));
        game.move(red.getPiece(1));
        assertEquals(11, red.getPiece(1).getProgress());
    }

    @Test
    void homePathPieceMovesByTheNormalDieOnly() {
        red.getPiece(1).setProgress(52);
        red.getPiece(2).setProgress(40);
        game.roll(new DiceResult(2, 3));
        assertEquals(54, game.targetOf(red.getPiece(1)), "home path: 2, not 6 (CR-01)");
        assertEquals(46, game.targetOf(red.getPiece(2)), "shared track: 2 x 3");
        game.move(red.getPiece(1));
        assertEquals(54, red.getPiece(1).getProgress());
        assertEquals(40, red.getPiece(2).getProgress(), "the other piece stays");
    }

    @Test
    void pieceTurnsIntoItsHomePathAfterTheArrow() {
        red.getPiece(1).setProgress(48);
        game.roll(new DiceResult(2, 2));
        game.move(red.getPiece(1));
        assertEquals(PieceState.IN_HOME_PATH, red.getPiece(1).getState());
        assertEquals(-1, red.getPiece(1).getTrackIndex(), "it left the shared track instead of going around again");
    }

    // ---------- Going back (CR-02, CR-10) ----------

    private void greenBlockAtRedProgress(int redProgress) {
        int square = Board.toTrackIndex(PlayerColor.RED, redProgress);
        int gp = Math.floorMod(square - PlayerColor.GREEN.getStartSquare(), Board.TRACK_LENGTH);
        green.getPiece(1).setProgress(gp);
        green.getPiece(2).setProgress(gp);
    }

    @Test
    void doubleBlockAlsoStopsAPieceGoingBack() {
        red.getPiece(1).setProgress(10);
        greenBlockAtRedProgress(8);
        game.applySurprise(red.getPiece(1), Surprise.THREE_STEPS_BACK);
        assertEquals(9, red.getPiece(1).getProgress());
    }

    @Test
    void goingBackStopsOnTheStartSquare() {
        red.getPiece(1).setProgress(1);
        game.applySurprise(red.getPiece(1), Surprise.THREE_STEPS_BACK);
        assertEquals(0, red.getPiece(1).getProgress(), "never behind the start square");
    }

    // ---------- The screen hears about every move (FR-3.11) ----------

    @Test
    void listenersSeeTheNewPositionAfterAMove() {
        red.getPiece(1).setProgress(5);
        int[] seen = {-99};
        game.addListener((g, message) -> seen[0] = red.getPiece(1).getProgress());
        game.roll(new DiceResult(1, 2));
        game.move(red.getPiece(1));
        assertEquals(7, seen[0]);
    }
}
