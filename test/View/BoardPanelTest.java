package View;

import static org.junit.jupiter.api.Assertions.*;

import java.awt.Point;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

import Model.Board;
import Model.PlayerColor;

class BoardPanelTest {

    private static boolean touching(Point a, Point b) {
        return Math.max(Math.abs(a.x - b.x), Math.abs(a.y - b.y)) == 1;
    }

    @Test
    void trackUses52DifferentCellsThatFollowEachOther() {
        Set<Point> seen = new HashSet<>();
        for (int i = 0; i < Board.TRACK_LENGTH; i++) {
            Point p = BoardPanel.trackCell(i);
            assertTrue(p.x >= 0 && p.x < BoardPanel.N && p.y >= 0 && p.y < BoardPanel.N, "cell " + i + " is off the grid");
            assertTrue(seen.add(p), "two squares share cell " + p);
            assertTrue(touching(p, BoardPanel.trackCell((i + 1) % Board.TRACK_LENGTH)), "gap after square " + i);
        }
    }

    @Test
    void startSquaresSitNextToTheirOwnCastle() {
        for (PlayerColor c : PlayerColor.values()) {
            Point start = BoardPanel.trackCell(c.getStartSquare());
            Point corner = BoardPanel.castleCorner(c);
            boolean beside = start.x >= corner.x - 1 && start.x <= corner.x + 6 && start.y >= corner.y - 1 && start.y <= corner.y + 6;
            assertTrue(beside, c + " starts far from its castle");
        }
    }

    @Test
    void homePathStartsAtTheArrowAndEndsInTheCenter() {
        for (PlayerColor c : PlayerColor.values()) {
            Point arrow = BoardPanel.trackCell(Board.arrowSquare(c));
            assertTrue(touching(arrow, BoardPanel.homeCell(c, 0)), c + " home path is not after its arrow square");
            Point last = BoardPanel.homeCell(c, Board.HOME_PATH_LENGTH - 1);
            assertTrue(last.x >= 6 && last.x <= 8 && last.y >= 6 && last.y <= 8, c + " Final Home is not in the center");
            for (int k = 0; k < Board.HOME_PATH_LENGTH - 1; k++)
                for (int i = 0; i < Board.TRACK_LENGTH; i++)
                    assertNotEquals(BoardPanel.trackCell(i), BoardPanel.homeCell(c, k));
        }
    }
}
