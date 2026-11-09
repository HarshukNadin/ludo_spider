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
     *         0 is the start square and {@link Board#FINAL_INDEX} is the Final Home.
     */
    public int getProgress() { return progress; }
    void setProgress(int progress) { this.progress = progress; }
    void sendToCastle() { progress = IN_CASTLE; }

    /** @return where the piece is: castle, shared track, home path or finished */
    public PieceState getState() {
        if (progress == IN_CASTLE) return PieceState.IN_CASTLE;
        if (progress == Board.FINAL_INDEX) return PieceState.FINISHED;
        if (progress >= Board.TRACK_LENGTH - 1) return PieceState.IN_HOME_PATH;
        return PieceState.ON_TRACK;
    }

    /** @return the index on the shared track, or -1 when in the castle, home path or finished */
    public int getTrackIndex() { return Board.toTrackIndex(owner.getColor(), progress); }
}
