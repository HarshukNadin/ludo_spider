package Model;

import static org.junit.jupiter.api.Assertions.*;

import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import org.junit.jupiter.api.Test;

/** The outer routes of Medium and Hard (FR-2.03, FR-2.04, FR-2.05, FR-2.06, FR-3.05). */
class OuterBoardTest {

    @Test
    void mediumHasTheStandardTrackAndA64SquareRoute() {
        Board b = new OuterBoard(Difficulty.MEDIUM, new Random(1));
        assertEquals(2, b.getRoutes().size());
        assertEquals(52, b.getRoute(Route.STANDARD).getLength());
        assertEquals(64, b.getRoute(Route.RING_64).getLength());
    }

    @Test
    void hardAlsoHasA72SquareRoute() {
        Board b = new OuterBoard(Difficulty.HARD, new Random(1));
        assertEquals(3, b.getRoutes().size());
        assertEquals(72, b.getRoute(Route.RING_72).getLength());
    }

    @Test
    void easyHasNoOuterRoute() {
        assertThrows(IllegalArgumentException.class, () -> new OuterBoard(Difficulty.EASY, new Random(1)));
    }

    @Test
    void mediumSquareCountsMatchTheSrs() {
        for (int seed = 0; seed < 20; seed++) {
            Board b = new OuterBoard(Difficulty.MEDIUM, new Random(seed));
            assertEquals(4, b.count(SquareType.START));
            assertEquals(8, b.count(SquareType.ARROW));
            assertEquals(8, b.count(SquareType.PROTECTION));
            assertEquals(6, b.count(SquareType.QUESTION));
            assertEquals(2, b.count(SquareType.SURPRISE));
        }
    }

    @Test
    void hardSquareCountsMatchTheSrs() {
        for (int seed = 0; seed < 20; seed++) {
            Board b = new OuterBoard(Difficulty.HARD, new Random(seed));
            assertEquals(4, b.count(SquareType.START));
            assertEquals(12, b.count(SquareType.ARROW));
            assertEquals(12, b.count(SquareType.PROTECTION));
            assertEquals(9, b.count(SquareType.QUESTION));
            assertEquals(3, b.count(SquareType.SURPRISE));
        }
    }

    @Test
    void startSquareIsTheOuterCornerNextToTheCastle() {
        Board b = new OuterBoard(Difficulty.HARD, new Random(2));
        for (PlayerColor c : PlayerColor.values()) {
            Square first = b.getPath(c).get(0);
            assertEquals(Route.RING_72, first.getRoute());
            assertEquals(18 * c.ordinal(), first.getIndex(), c + " corner");
            assertEquals(SquareType.START, first.getType());
        }
    }

    @Test
    void everyPathWalksClockwiseAndOnlyStepsInwardAtItsArrows() {
        for (Difficulty d : List.of(Difficulty.MEDIUM, Difficulty.HARD)) {
            Board b = new OuterBoard(d, new Random(3));
            for (PlayerColor c : PlayerColor.values()) {
                List<Square> path = b.getPath(c);
                for (int i = 0; i + 1 < path.size(); i++) {
                    Square now = path.get(i), next = path.get(i + 1);
                    if (now.getRoute() == next.getRoute()) {
                        int len = Route.lengthOf(now.getRoute());
                        assertEquals((now.getIndex() + 1) % len, next.getIndex(), d + " " + c + " step " + i);
                    } else {
                        assertEquals(now.getRoute() - 1, next.getRoute(), "one ring inward at a time");
                        assertEquals(SquareType.ARROW, now.getType(), "the turn is on an Arrow square");
                    }
                }
                assertEquals(Board.arrowSquare(c), path.get(path.size() - 1).getIndex(), "ends on the color's arrow of the track");
            }
        }
    }

    @Test
    void piecesGoOnceAroundEachInnerRoute() {
        Board b = new OuterBoard(Difficulty.HARD, new Random(4));
        for (PlayerColor c : PlayerColor.values()) {
            Set<Square> ring64 = new HashSet<>(), track = new HashSet<>();
            for (Square s : b.getPath(c)) {
                if (s.getRoute() == Route.RING_64) ring64.add(s);
                if (s.getRoute() == Route.STANDARD) track.add(s);
            }
            assertEquals(64, ring64.size(), c + " walks the whole 64-square ring");
            assertEquals(52, track.size(), c + " walks the whole track");
        }
    }

    @Test
    void outerArrowLeadsToTheSquareBeforeTheOldStartSquare() {
        Board b = new OuterBoard(Difficulty.MEDIUM, new Random(5));
        for (PlayerColor c : PlayerColor.values()) {
            List<Square> path = b.getPath(c);
            int firstOnTrack = 0;
            while (path.get(firstOnTrack).getRoute() != Route.STANDARD) firstOnTrack++;
            assertEquals(Math.floorMod(c.getStartSquare() - 1, 52), path.get(firstOnTrack).getIndex());
        }
    }

    @Test
    void allColorsHaveTheSameDistanceToFinalHome() {
        assertEquals(51, new Board(Difficulty.EASY, new Random(1)).getHomeStart());
        assertEquals(58 + 52, new OuterBoard(Difficulty.MEDIUM, new Random(1)).getHomeStart());
        assertEquals(66 + 64 + 52, new OuterBoard(Difficulty.HARD, new Random(1)).getHomeStart());
        Board b = new OuterBoard(Difficulty.HARD, new Random(1));
        for (PlayerColor c : PlayerColor.values()) assertEquals(b.getHomeStart(), b.getPath(c).size());
    }

    @Test
    void specialSquaresAreNeverOnStartOrArrowSquares() {
        Board b = new OuterBoard(Difficulty.HARD, new Random(6));
        for (PlayerColor c : PlayerColor.values()) {
            List<Square> path = b.getPath(c);
            assertEquals(SquareType.START, path.get(0).getType());
            assertEquals(SquareType.ARROW, path.get(path.size() - 1).getType());
        }
    }

    @Test
    void gameOnHardUsesTheOuterBoard() {
        Player red = new Player("Dana", PlayerColor.RED), blue = new Player("Omar", PlayerColor.BLUE);
        Game game = new Game(Difficulty.HARD, List.of(red, blue), null, new Random(7));
        assertInstanceOf(OuterBoard.class, game.getBoard());
        assertEquals(game.getBoard().getFinalIndex(), red.getFinalIndex());
        red.getPiece(1).setProgress(red.getFinalIndex());
        assertEquals(PieceState.FINISHED, red.getPiece(1).getState());
    }

    @Test
    void captureWorksOnTheOuterRoute() {
        Player red = new Player("Dana", PlayerColor.RED), green = new Player("Omar", PlayerColor.GREEN);
        Game game = new Game(Difficulty.MEDIUM, List.of(red, green), null, new Random(8));
        for (Route r : game.getBoard().getRoutes())
            for (Square s : r.getSquares())
                if (s.getType() != SquareType.START && s.getType() != SquareType.ARROW) s.setType(SquareType.NORMAL);
        Square target = game.getBoard().squareAt(PlayerColor.RED, 20);
        green.getPiece(1).setProgress(game.getBoard().getPath(PlayerColor.GREEN).indexOf(target));
        red.getPiece(1).setProgress(16);
        game.roll(new DiceResult(2, 2));
        game.move(red.getPiece(1));
        assertSame(target, red.getPiece(1).getSquare());
        assertEquals(PieceState.IN_CASTLE, green.getPiece(1).getState(), "green was captured on the 64-square route");
    }
}
