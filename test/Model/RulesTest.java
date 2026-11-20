package Model;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Random;
import org.junit.jupiter.api.Test;

/** The move rules checked on the Medium and Hard boards, with the outer routes (FR-3.03 to FR-3.13). */
class RulesTest {
    private Player red, green;
    private Game game;

    private void start(Difficulty d) {
        red = new Player("Dana", PlayerColor.RED);
        green = new Player("Omar", PlayerColor.GREEN);
        game = new Game(d, List.of(red, green), null, new Random(1));
        for (Route r : game.getBoard().getRoutes())
            for (Square s : r.getSquares())
                if (s.getType() != SquareType.START && s.getType() != SquareType.ARROW) s.setType(SquareType.NORMAL);
    }

    /** Puts one of green's pieces on the square that red reaches at the given progress. */
    private void greenAt(int piece, int redProgress) {
        Square sq = game.getBoard().squareAt(PlayerColor.RED, redProgress);
        int gp = game.getBoard().getPath(PlayerColor.GREEN).indexOf(sq);
        assertTrue(gp >= 0, "green also walks this square");
        green.getPiece(piece).setProgress(gp);
    }

    // ---------- FR-3.03 multiplier ----------

    @Test
    void hardMultipliesOnTheOuterRoute() {
        start(Difficulty.HARD);
        red.getPiece(1).setProgress(2);
        game.roll(new DiceResult(4, 5));
        game.move(red.getPiece(1));
        assertEquals(22, red.getPiece(1).getProgress());
        assertEquals(Route.RING_72, red.getPiece(1).getSquare().getRoute());
    }

    @Test
    void crossingAnArrowMovesInwardInTheSameMove() {
        start(Difficulty.MEDIUM);
        int lastOnRing = 0;
        while (game.getBoard().getPath(PlayerColor.RED).get(lastOnRing + 1).getRoute() == Route.RING_64) lastOnRing++;
        red.getPiece(1).setProgress(lastOnRing - 2);
        game.roll(new DiceResult(5, 1));
        game.move(red.getPiece(1));
        assertEquals(Route.STANDARD, red.getPiece(1).getSquare().getRoute(), "passed its arrow and went on to the track (FR-3.05)");
    }

    // ---------- FR-3.06 sixes ----------

    @Test
    void sixGivesAnotherRollAndThirdSixIsCancelled() {
        start(Difficulty.HARD);
        red.getPiece(1).setProgress(5);
        game.roll(new DiceResult(6, 1));
        game.move(red.getPiece(1));
        assertEquals(red, game.getCurrentPlayer(), "a 6 gives another roll");
        game.roll(new DiceResult(6, 1));
        game.move(red.getPiece(1));
        int before = red.getPiece(1).getProgress();
        game.roll(new DiceResult(6, 1));
        assertEquals(green, game.getCurrentPlayer(), "the third 6 passes the turn");
        assertEquals(before, red.getPiece(1).getProgress(), "and moves nothing");
    }

    // ---------- FR-3.08 capture, FR-4.02 protection ----------

    @Test
    void captureOnTheOuterRoute() {
        start(Difficulty.HARD);
        red.getPiece(1).setProgress(20);
        greenAt(1, 24);
        game.roll(new DiceResult(4, 1));
        game.move(red.getPiece(1));
        assertEquals(PieceState.IN_CASTLE, green.getPiece(1).getState());
    }

    @Test
    void noCaptureOnProtectionSurpriseOrStartSquares() {
        for (SquareType safe : List.of(SquareType.PROTECTION, SquareType.SURPRISE)) {
            start(Difficulty.MEDIUM);
            game.getBoard().squareAt(PlayerColor.RED, 24).setType(safe);
            red.getPiece(1).setProgress(20);
            greenAt(1, 24);
            game.roll(new DiceResult(4, 1));
            game.move(red.getPiece(1));
            assertEquals(PieceState.ON_TRACK, green.getPiece(1).getState(), safe + " protects");
        }
        start(Difficulty.MEDIUM);
        green.getPiece(1).setProgress(0);
        Square greenStart = green.getPiece(1).getSquare();
        int redProgress = game.getBoard().getPath(PlayerColor.RED).indexOf(greenStart);
        red.getPiece(1).setProgress(redProgress - 3);
        game.roll(new DiceResult(3, 1));
        game.move(red.getPiece(1));
        assertEquals(PieceState.ON_TRACK, green.getPiece(1).getState(), "a start square protects");
    }

    @Test
    void questionSquareDoesNotProtect() {
        start(Difficulty.MEDIUM);
        game.getBoard().squareAt(PlayerColor.RED, 24).setType(SquareType.QUESTION);
        red.getPiece(1).setProgress(20);
        greenAt(1, 24);
        game.roll(new DiceResult(4, 1));
        game.move(red.getPiece(1));
        assertEquals(PieceState.IN_CASTLE, green.getPiece(1).getState(), "FR-4.02");
    }

    // ---------- FR-3.09 Double Block ----------

    @Test
    void doubleBlockOnTheOuterRouteStopsTheOpponentBeforeIt() {
        start(Difficulty.HARD);
        red.getPiece(1).setProgress(20);
        greenAt(1, 23);
        greenAt(2, 23);
        game.roll(new DiceResult(6, 1));
        game.move(red.getPiece(1));
        assertEquals(22, red.getPiece(1).getProgress(), "stops on the square before the block");
    }

    @Test
    void ownPiecesPassTheirOwnBlockAndCanBreakIt() {
        start(Difficulty.MEDIUM);
        red.getPiece(1).setProgress(13);
        red.getPiece(2).setProgress(13);
        red.getPiece(3).setProgress(10);
        game.roll(new DiceResult(5, 1));
        assertEquals(15, game.targetOf(red.getPiece(3)), "a player's own block does not stop them");
        game.move(red.getPiece(1));
        assertEquals(18, red.getPiece(1).getProgress(), "moving one piece breaks the block");
    }

    @Test
    void pieceRightBehindABlockCannotMove() {
        start(Difficulty.MEDIUM);
        red.getPiece(1).setProgress(22);
        greenAt(1, 23);
        greenAt(2, 23);
        game.roll(new DiceResult(3, 1));
        assertFalse(game.legalMoves().contains(red.getPiece(1)));
    }

    // ---------- FR-3.10 exact home, FR-3.12 invalid moves ----------

    @Test
    void finalHomeNeedsTheExactRollOnHard() {
        start(Difficulty.HARD);
        red.getPiece(1).setProgress(red.getFinalIndex() - 2);
        game.roll(new DiceResult(3, 4));
        assertNull(game.targetOf(red.getPiece(1)), "3 is too many");
        assertEquals(GamePhase.NO_MOVE, game.getPhase());
        game.passTurn();
        game.roll(new DiceResult(1, 1));
        game.passTurn();
        game.roll(new DiceResult(2, 6));
        game.move(red.getPiece(1));
        assertEquals(PieceState.FINISHED, red.getPiece(1).getState(), "the home path uses only the Normal die");
    }

    @Test
    void invalidMovesAreRejected() {
        start(Difficulty.MEDIUM);
        red.getPiece(1).setProgress(5);
        game.roll(new DiceResult(2, 2));
        assertThrows(IllegalArgumentException.class, () -> game.move(green.getPiece(1)), "not your piece");
        assertThrows(IllegalArgumentException.class, () -> game.move(red.getPiece(2)), "still in the castle");
        assertEquals(5, red.getPiece(1).getProgress(), "nothing moved");
    }

    // ---------- FR-3.13 going back ----------

    @Test
    void goingBackStopsOnTheOuterStartSquare() {
        start(Difficulty.HARD);
        red.getPiece(1).setProgress(2);
        game.applySurprise(red.getPiece(1), Surprise.THREE_STEPS_BACK);
        assertEquals(0, red.getPiece(1).getProgress());
        assertEquals(SquareType.START, red.getPiece(1).getSquare().getType());
    }

    // ---------- CR-07 UltraBoost surprise ----------

    @Test
    void ultraBoostSurpriseGivesTheSamePlayerAnExtraTurn() {
        start(Difficulty.EASY);
        red.getPiece(1).setProgress(5);
        game.roll(new DiceResult(3, 1));
        // As if the piece had landed on a Surprise square that gave the UltraBoost roll.
        game.applySurprise(red.getPiece(1), Surprise.ULTRABOOST_ROLL);
        game.move(red.getPiece(1));
        assertEquals(red, game.getCurrentPlayer(), "the player who got the surprise rolls again");
        game.roll();
        assertTrue(game.getLastRoll().multiplier() >= 1 && game.getLastRoll().multiplier() <= 6);
    }
}
