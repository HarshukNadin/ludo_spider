package model;

/** One of a player's four pieces. Its position is its progress along its own route. */
public class Piece {
    public static final int IN_CASTLE = -1;

    private final Player owner;
    private final int number;
    private int progress = IN_CASTLE;

    public Piece(Player owner, int number) {
        this.owner = owner;
        this.number = number;
    }

    public Player getOwner() { return owner; }
    /** 1 to 4. */
    public int getNumber() { return number; }
    public int getProgress() { return progress; }
    void setProgress(int progress) { this.progress = progress; }
    void sendToCastle() { progress = IN_CASTLE; }

    public PieceState getState() {
        if (progress == IN_CASTLE) return PieceState.IN_CASTLE;
        if (progress == Board.FINAL_INDEX) return PieceState.FINISHED;
        if (progress >= Board.TRACK_LENGTH - 1) return PieceState.IN_HOME_PATH;
        return PieceState.ON_TRACK;
    }

    /** Shared-track index, or -1 when in the castle, home path or finished. */
    public int getTrackIndex() { return Board.toTrackIndex(owner.getColor(), progress); }
}
