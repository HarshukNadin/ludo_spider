package Model;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * The Medium and Hard board: the standard board with one outer route (64 squares) on Medium,
 * or two (64 and 72 squares) on Hard (FR-2.03, FR-2.04).
 * <p>
 * A piece starts on the corner of the outermost ring next to its castle (FR-2.06) and walks
 * clockwise. At its color's Arrow square it moves one ring inward (FR-3.05). On the 64-square
 * ring of Hard it goes once around, and on the standard track it goes once around from the
 * square before its old Start square, to its Arrow square and the home path.
 */
public class OuterBoard extends Board {

    /**
     * @param difficulty {@link Difficulty#MEDIUM} or {@link Difficulty#HARD}
     * @param random     used to place the special squares
     * @throws IllegalArgumentException for Easy, which has no outer routes
     */
    public OuterBoard(Difficulty difficulty, Random random) {
        super(difficulty, random, outerRoutesFor(difficulty));
    }

    /**
     * @param difficulty a level
     * @return how many outer routes it has: 1 for Medium, 2 for Hard
     */
    public static int outerRoutesFor(Difficulty difficulty) {
        switch (difficulty) {
            case MEDIUM: return 1;
            case HARD: return 2;
            default: throw new IllegalArgumentException("Easy has no outer routes; use Board");
        }
    }

    /**
     * Index of a color's Start square on an outer ring: the corner next to its castle.
     *
     * @param color the color
     * @param route {@link Route#RING_64} or {@link Route#RING_72}
     * @return the corner index (0 is top-left, then clockwise)
     */
    public static int cornerOf(PlayerColor color, int route) {
        return Route.sideOf(route) * color.ordinal();
    }

    /**
     * Index of a color's Arrow square on an outer ring. It sits on the side before the color's
     * home arm, next to the square of the inner route where the piece goes on.
     *
     * @param color the color
     * @param route {@link Route#RING_64} or {@link Route#RING_72}
     * @return the Arrow index on that ring
     */
    public static int arrowOf(PlayerColor color, int route) {
        int side = Route.sideOf(route);
        return Math.floorMod(4 * side - 7 + side * color.ordinal(), 4 * side);
    }

    @Override
    protected List<Square> buildPath(PlayerColor color) {
        List<Square> path = new ArrayList<>();
        int outer = getRoutes().size() - 1;
        int entry = cornerOf(color, outer);
        for (int r = outer; r > Route.STANDARD; r--) {
            Route ring = getRoute(r);
            int arrow = arrowOf(color, r);
            for (int i = entry; ; i++) {
                Square s = ring.getSquare(i);
                path.add(s);
                if (s.getIndex() == arrow) break;
            }
            if (r - 1 > Route.STANDARD) entry = arrowOf(color, r - 1) + 1;
        }
        Route track = getRoute(Route.STANDARD);
        for (int k = 0; k < TRACK_LENGTH; k++) path.add(track.getSquare(color.getStartSquare() - 1 + k));
        return path;
    }
}
