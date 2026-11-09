package Model;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Random;
import org.junit.jupiter.api.Test;

class BoardTest {

    @Test
    void easyBoardHasTheRequiredSquares() {
        Board board = new Board(Difficulty.EASY, new Random(7));
        assertEquals(52, board.getTrack().size());
        assertEquals(4, board.count(SquareType.START));
        assertEquals(4, board.count(SquareType.ARROW));
        assertEquals(4, board.count(SquareType.PROTECTION));
        assertEquals(0, board.count(SquareType.QUESTION));
        assertEquals(0, board.count(SquareType.SURPRISE));
    }

    @Test
    void startAndArrowSquaresDontMoveBetweenGames() {
        for (int seed = 0; seed < 20; seed++) {
            Board board = new Board(Difficulty.EASY, new Random(seed));
            for (PlayerColor c : PlayerColor.values()) {
                assertEquals(SquareType.START, board.getSquare(c.getStartSquare()).getType());
                assertEquals(SquareType.ARROW, board.getSquare(Board.arrowSquare(c)).getType());
            }
        }
    }

    @Test
    void protectionSquaresChangeBetweenGames() {
        Board a = new Board(Difficulty.EASY, new Random(1));
        Board b = new Board(Difficulty.EASY, new Random(2));
        boolean differs = false;
        for (int i = 0; i < Board.TRACK_LENGTH; i++)
            if (a.getSquare(i).getType() != b.getSquare(i).getType()) differs = true;
        assertTrue(differs);
    }

    @Test
    void arrowIsRightBeforeTheHomePath() {
        // red starts on 8, so its last shared square is 6
        assertEquals(6, Board.arrowSquare(PlayerColor.RED));
        assertEquals(-1, Board.toTrackIndex(PlayerColor.RED, Board.TRACK_LENGTH - 1));
    }
}
