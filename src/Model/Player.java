package Model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** A player with a name, a unique color and four pieces. */
public class Player {
    public static final int PIECES = 4;
    public static final int MAX_NAME_LENGTH = 15;

    private final String name;
    private final PlayerColor color;
    private final List<Piece> pieces = new ArrayList<>();
    private boolean skipNextTurn;

    public Player(String name, PlayerColor color) {
        if (name == null || name.isBlank()) throw new IllegalArgumentException("Name is required");
        if (name.trim().length() > MAX_NAME_LENGTH) throw new IllegalArgumentException("Name is too long");
        this.name = name.trim();
        this.color = color;
        for (int i = 1; i <= PIECES; i++) pieces.add(new Piece(this, i));
    }

    public String getName() { return name; }
    public PlayerColor getColor() { return color; }
    public List<Piece> getPieces() { return Collections.unmodifiableList(pieces); }
    public Piece getPiece(int number) { return pieces.get(number - 1); }

    public boolean hasFinished() {
        return pieces.stream().allMatch(p -> p.getState() == PieceState.FINISHED);
    }

    public boolean isSkipNextTurn() { return skipNextTurn; }
    void setSkipNextTurn(boolean skip) { this.skipNextTurn = skip; }

    @Override
    public String toString() { return name + " (" + color + ")"; }
}
