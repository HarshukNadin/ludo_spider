package Model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A closed ring of shared squares, walked clockwise.
 * Route 0 is the standard 52-square track. Medium adds route 1, a 64-square ring around the
 * standard board, and Hard adds route 2, a 72-square ring around that (FR-2.03, FR-2.04).
 * On the outer rings square 0 is the top-left corner.
 */
public class Route {
    /** Number of the standard track. */
    public static final int STANDARD = 0;
    /** Number of the 64-square route (Medium and Hard). */
    public static final int RING_64 = 1;
    /** Number of the 72-square route (Hard). */
    public static final int RING_72 = 2;

    private final int number;
    private final List<Square> squares = new ArrayList<>();

    /**
     * Creates a route of normal squares.
     *
     * @param number {@link #STANDARD}, {@link #RING_64} or {@link #RING_72}
     */
    public Route(int number) {
        this.number = number;
        for (int i = 0; i < lengthOf(number); i++) squares.add(new Square(number, i, SquareType.NORMAL));
    }

    /**
     * @param number a route number
     * @return how many squares that route has: 52, 64 or 72
     */
    public static int lengthOf(int number) {
        switch (number) {
            case STANDARD: return Board.TRACK_LENGTH;
            case RING_64: return 64;
            case RING_72: return 72;
            default: throw new IllegalArgumentException("No route " + number);
        }
    }

    /**
     * @param number {@link #RING_64} or {@link #RING_72}
     * @return squares on one side of the ring, corner to corner: 16 or 18
     */
    public static int sideOf(int number) { return lengthOf(number) / 4; }

    /** @return the route number */
    public int getNumber() { return number; }
    /** @return how many squares the route has */
    public int getLength() { return squares.size(); }
    /** @return the squares in walking order, read only */
    public List<Square> getSquares() { return Collections.unmodifiableList(squares); }

    /**
     * @param index any whole number; it wraps around the ring
     * @return the square at that place
     */
    public Square getSquare(int index) { return squares.get(Math.floorMod(index, squares.size())); }
}
