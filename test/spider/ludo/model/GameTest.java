package spider.ludo.model;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Random;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class GameTest {
    private Player red, green;
    private Game game;

    @BeforeEach
    void setUp() {
        red = new Player("Nadin", PlayerColor.RED);
        green = new Player("David", PlayerColor.GREEN);
        game = new Game(Difficulty.EASY, List.of(red, green), null, new Random(1));
    }

    @Test
    void needsTwoToFourPlayersWithUniqueColors() {
        assertThrows(IllegalArgumentException.class, () -> new Game(Difficulty.EASY, List.of(red), null, new Random()));
        Player red2 = new Player("Other", PlayerColor.RED);
        assertThrows(IllegalArgumentException.class, () -> new Game(Difficulty.EASY, List.of(red, red2), null, new Random()));
    }

    @Test
    void pieceLeavesCastleOnlyOnSix() {
        game.roll(new DiceResult(5, 1));
        assertEquals(green, game.getCurrentPlayer(), "No move possible, turn passes");
        game.roll(new DiceResult(3, 1));
        game.roll(new DiceResult(6, 1));
        assertEquals(GamePhase.CHOOSING_PIECE, game.getPhase());
        game.move(red.getPiece(1));
        assertEquals(PieceState.ON_TRACK, red.getPiece(1).getState());
        assertEquals(red, game.getCurrentPlayer(), "A 6 gives another roll");
    }

    @Test
    void thirdSixInARowIsCancelled() {
        red.getPiece(1).setProgress(0);
        game.roll(new DiceResult(6, 1));
        game.move(red.getPiece(1));
        game.roll(new DiceResult(6, 1));
        game.move(red.getPiece(1));
        int before = red.getPiece(1).getProgress();
        game.roll(new DiceResult(6, 1));
        assertEquals(green, game.getCurrentPlayer());
        assertEquals(before, red.getPiece(1).getProgress());
    }

    @Test
    void landingOnASingleOpponentCapturesIt() {
        red.getPiece(1).setProgress(30);
        int target = Board.toTrackIndex(PlayerColor.RED, 33);
        int greenProgress = Math.floorMod(target - PlayerColor.GREEN.getStartSquare(), Board.TRACK_LENGTH);
        green.getPiece(1).setProgress(greenProgress);
        game.getBoard().getSquare(target).setType(SquareType.NORMAL);
        game.roll(new DiceResult(3, 1));
        game.move(red.getPiece(1));
        assertEquals(PieceState.IN_CASTLE, green.getPiece(1).getState());
    }

    @Test
    void doubleBlockStopsOpponents() {
        red.getPiece(1).setProgress(30);
        int blockSquare = Board.toTrackIndex(PlayerColor.RED, 32);
        int gp = Math.floorMod(blockSquare - PlayerColor.GREEN.getStartSquare(), Board.TRACK_LENGTH);
        green.getPiece(1).setProgress(gp);
        green.getPiece(2).setProgress(gp);
        game.roll(new DiceResult(4, 1));
        game.move(red.getPiece(1));
        assertEquals(31, red.getPiece(1).getProgress(), "Stops on the square before the block");
    }

    @Test
    void finalHomeNeedsExactRoll() {
        red.getPiece(1).setProgress(Board.FINAL_INDEX - 2);
        game.roll(new DiceResult(3, 1));
        assertEquals(green, game.getCurrentPlayer(), "Too high, no move");
        game.roll(new DiceResult(1, 1));
        game.roll(new DiceResult(2, 1));
        game.move(red.getPiece(1));
        assertEquals(PieceState.FINISHED, red.getPiece(1).getState());
    }

    @Test
    void gameEndsWhenAllButOneFinish() {
        for (int i = 1; i <= 3; i++) red.getPiece(i).setProgress(Board.FINAL_INDEX);
        red.getPiece(4).setProgress(Board.FINAL_INDEX - 1);
        game.roll(new DiceResult(1, 1));
        game.move(red.getPiece(4));
        assertEquals(GamePhase.GAME_OVER, game.getPhase());
        assertEquals(List.of(red, green), game.getFinishOrder());
    }

    @Test
    void boardHasSpecialSquaresForDifficulty() {
        Board hard = new Board(Difficulty.HARD, new Random(3));
        assertEquals(12, hard.count(SquareType.PROTECTION));
        assertEquals(9, hard.count(SquareType.QUESTION));
        assertEquals(3, hard.count(SquareType.SURPRISE));
        assertEquals(4, hard.count(SquareType.START));
    }
}
