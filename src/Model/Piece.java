package Model;

/** One of a player's four pieces. Its position is its progress along its own route. */
public class Piece {
    public static final int IN_CASTLE = -1;

    private final Player owner;
    private final int number;
    private int progress = IN_CASTLE;

    /**
     * New pieces start in the castle.
     *
     * @param owner  the player this piece belongs to
     * @param number 1 to 4
     */
    public Piece(Player owner, int number) {
        this.owner = owner;
        this.number = number;
    }

    /** @return the player this piece belongs to */
    public Player getOwner() { return owner; }

    /** @return the piece number, 1 to 4 */
    public int getNumber() { return number; }

    /**
     * @return steps from the start square along this color's route, or {@link #IN_CASTLE}.
     *         0 is the start square and {@link Player#getFinalIndex()} is the Final Home.
     */
    public int getProgress() { return progress; }
    void setProgress(int progress) { this.progress = progress; }
    void sendToCastle() { progress = IN_CASTLE; }

    /** @return where the piece is: castle, shared track, home path or finished */
    public PieceState getState() {
        if (progress == IN_CASTLE) return PieceState.IN_CASTLE;
        if (progress == owner.getFinalIndex()) return PieceState.FINISHED;
        if (progress >= owner.getHomeStart()) return PieceState.IN_HOME_PATH;
        return PieceState.ON_TRACK;
    }

    /** @return the shared square under the piece, or null in the castle, home path or Final Home */
    public Square getSquare() {
        Board board = owner.getBoard();
        if (board == null) {
            int idx = Board.toTrackIndex(owner.getColor(), progress);
            return idx < 0 ? null : new Square(idx, SquareType.NORMAL);
        }
        return board.squareAt(owner.getColor(), progress);
    }

    /** @return the index on the standard track, or -1 when the piece is not on it */
    public int getTrackIndex() {
        Square s = getSquare();
        return s == null || s.getRoute() != Route.STANDARD ? -1 : s.getIndex();
    }

    /** @return the place on the home path, 0 to 5 where 5 is the Final Home, or -1 when the piece is not there */
    public int getHomeStep() {
        return getState() == PieceState.IN_HOME_PATH || getState() == PieceState.FINISHED ? progress - owner.getHomeStart() : -1;
    }
}
