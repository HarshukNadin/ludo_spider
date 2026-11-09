package Model;

/** The four player colors, in turn order around the board. */
public enum PlayerColor {
    RED(8), GREEN(21), YELLOW(34), BLUE(47);

    private final int startSquare;

    PlayerColor(int startSquare) { this.startSquare = startSquare; }

    /** Index of this color's start square on the shared track. */
    public int getStartSquare() { return startSquare; }
}
