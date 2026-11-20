package View;

import static org.junit.jupiter.api.Assertions.*;

import java.awt.Point;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Random;

import Model.Board;
import Model.DiceResult;
import Model.Difficulty;
import Model.OuterBoard;
import Model.Route;
import Model.Square;
import Model.Game;
import Model.Player;
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

    // ---------- Picking a piece with the mouse (T-4.4) ----------

    private static Game twoPlayerGame(Player red, Player green) {
        return new Game(Model.Difficulty.EASY, java.util.List.of(red, green), null, new java.util.Random(1));
    }

    @Test
    void clickOnAGlowingPieceInTheCastlePicksIt() {
        Player red = new Player("Nadin", PlayerColor.RED), green = new Player("David", PlayerColor.GREEN);
        Game game = twoPlayerGame(red, green);
        game.roll(new DiceResult(6, 1));
        Point corner = BoardPanel.castleCorner(PlayerColor.RED);
        assertEquals(1, BoardPanel.movablePieceAt(game, corner.x + 2.25, corner.y + 2.25));
        assertEquals(4, BoardPanel.movablePieceAt(game, corner.x + 3.75, corner.y + 3.8));
    }

    @Test
    void clickOnAnEmptySquareOrAnOpponentPicksNothing() {
        Player red = new Player("Nadin", PlayerColor.RED), green = new Player("David", PlayerColor.GREEN);
        Game game = twoPlayerGame(red, green);
        game.roll(new DiceResult(6, 1));
        Point greenCastle = BoardPanel.castleCorner(PlayerColor.GREEN);
        assertEquals(0, BoardPanel.movablePieceAt(game, greenCastle.x + 2.25, greenCastle.y + 2.25), "not Nadin's piece");
        assertEquals(0, BoardPanel.movablePieceAt(game, 7.5, 7.5), "center of the board");
    }

    @Test
    void nothingCanBePickedBeforeTheRoll() {
        Player red = new Player("Nadin", PlayerColor.RED), green = new Player("David", PlayerColor.GREEN);
        Game game = twoPlayerGame(red, green);
        Point corner = BoardPanel.castleCorner(PlayerColor.RED);
        assertEquals(0, BoardPanel.movablePieceAt(game, corner.x + 2.25, corner.y + 2.25));
    }

    // ---------- Outer routes of Medium and Hard (T-5.3) ----------

    @Test
    void ringsSurroundTheBoardWithoutOverlapping() {
        Set<Point> seen = new HashSet<>();
        for (int i = 0; i < Board.TRACK_LENGTH; i++) seen.add(BoardPanel.trackCell(i));
        for (int route : List.of(Route.RING_64, Route.RING_72)) {
            int len = Route.lengthOf(route);
            for (int i = 0; i < len; i++) {
                Point p = BoardPanel.ringCell(route, i);
                assertTrue(p.x < 0 || p.y < 0 || p.x >= BoardPanel.N || p.y >= BoardPanel.N, "ring cell " + p + " is inside the standard board");
                assertTrue(seen.add(p), "two squares share cell " + p);
                Point next = BoardPanel.ringCell(route, i + 1);
                assertEquals(1, Math.abs(p.x - next.x) + Math.abs(p.y - next.y), "gap after ring square " + i);
            }
        }
        assertEquals(new Point(-2, -2), BoardPanel.ringCell(Route.RING_72, 0), "square 0 is the top-left corner");
        assertEquals(new Point(15, -1), BoardPanel.ringCell(Route.RING_64, 16), "then clockwise");
    }

    @Test
    void everyColorPathIsDrawnAsOneConnectedLine() {
        for (Difficulty d : List.of(Difficulty.MEDIUM, Difficulty.HARD)) {
            Board board = new OuterBoard(d, new Random(1));
            for (PlayerColor c : PlayerColor.values()) {
                List<Square> path = board.getPath(c);
                for (int i = 0; i + 1 < path.size(); i++) {
                    Point a = BoardPanel.cellOf(path.get(i)), b = BoardPanel.cellOf(path.get(i + 1));
                    assertTrue(touching(a, b), d + " " + c + ": gap after step " + i);
                }
                Point last = BoardPanel.cellOf(path.get(path.size() - 1)), home = BoardPanel.homeCell(c, 0);
                assertEquals(1, Math.abs(last.x - home.x) + Math.abs(last.y - home.y), c + " arrow is next to its home path");
            }
        }
    }
}
