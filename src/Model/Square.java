package Model;

/** One square of a shared route: the standard track or one of the outer routes. */
public class Square {
    private final int route;
    private final int index;
    private SquareType type;

    /**
     * A square of the standard 52-square track.
     *
     * @param index place on the track, 0 to 51
     * @param type  what kind of square it is
     */
    public Square(int index, SquareType type) {
        this(Route.STANDARD, index, type);
    }

    /**
     * @param route number of the route the square belongs to ({@link Route#getNumber()})
     * @param index place on that route, starting at 0
     * @param type  what kind of square it is
     */
    public Square(int route, int index, SquareType type) {
        this.route = route;
        this.index = index;
        this.type = type;
    }

    /** @return the route number: 0 for the standard track, 1 for the 64-square route, 2 for the 72-square route */
    public int getRoute() { return route; }
    /** @return place on its route, starting at 0 */
    public int getIndex() { return index; }
    /** @return what kind of square it is */
    public SquareType getType() { return type; }
    void setType(SquareType type) { this.type = type; }

    @Override
    public String toString() { return "route " + route + " square " + index + " (" + type + ")"; }
}
