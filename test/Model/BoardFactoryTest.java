package Model;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Random;
import org.junit.jupiter.api.Test;

/** The board factory gives each level the right kind of board (FR-2.01). */
class BoardFactoryTest {

    @Test
    void easyGetsTheStandardBoard() {
        Board b = BoardFactory.createBoard(Difficulty.EASY, new Random(1));
        assertEquals(Board.class, b.getClass());
        assertEquals(1, b.getRoutes().size());
    }

    @Test
    void mediumGetsOneOuterRoute() {
        Board b = BoardFactory.createBoard(Difficulty.MEDIUM, new Random(1));
        assertInstanceOf(OuterBoard.class, b);
        assertEquals(2, b.getRoutes().size());
    }

    @Test
    void hardGetsTwoOuterRoutes() {
        Board b = BoardFactory.createBoard(Difficulty.HARD, new Random(1));
        assertInstanceOf(OuterBoard.class, b);
        assertEquals(3, b.getRoutes().size());
    }

    @Test
    void theBoardKeepsTheLevel() {
        for (Difficulty d : Difficulty.values()) assertEquals(d, BoardFactory.createBoard(d, new Random(2)).getDifficulty());
    }

    @Test
    void sameSeedGivesTheSameBoard() {
        Board a = BoardFactory.createBoard(Difficulty.HARD, new Random(9)), b = BoardFactory.createBoard(Difficulty.HARD, new Random(9));
        for (int r = 0; r < a.getRoutes().size(); r++)
            for (int i = 0; i < a.getRoute(r).getLength(); i++)
                assertEquals(a.getRoute(r).getSquare(i).getType(), b.getRoute(r).getSquare(i).getType());
    }

    @Test
    void theGameUsesTheFactory() {
        Game g = new Game(Difficulty.MEDIUM, List.of(new Player("Dana", PlayerColor.RED), new Player("Omar", PlayerColor.BLUE)), null, new Random(3));
        assertInstanceOf(OuterBoard.class, g.getBoard());
    }
}
