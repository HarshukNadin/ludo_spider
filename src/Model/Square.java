package Model;

/** One square of the shared track. */
public class Square {
    private final int index;
    private SquareType type;

    /**
     * @param index place on the track, 0 to 51
     * @param type  what kind of square it is
     */
    public Square(int index, SquareType type) {
        this.index = index;
        this.type = type;
    }

    /** @return place on the track, 0 to 51 */
    public int getIndex() { return index; }
    /** @return what kind of square it is */
    public SquareType getType() { return type; }
    void setType(SquareType type) { this.type = type; }
}
