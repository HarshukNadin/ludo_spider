package model;

/** One square of the shared track. */
public class Square {
    private final int index;
    private SquareType type;

    public Square(int index, SquareType type) {
        this.index = index;
        this.type = type;
    }

    public int getIndex() { return index; }
    public SquareType getType() { return type; }
    void setType(SquareType type) { this.type = type; }
}
