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

    /**
     * Creates a player with four pieces, all in the castle (FR-1.06).
     *
     * @param name  the player's name, trimmed, 1 to 15 characters
     * @param color the player's color, unique in the game
     * @throws IllegalArgumentException if the name is empty or too long
     */
    public Player(String name, PlayerColor color) {
        if (name == null || name.isBlank()) throw new IllegalArgumentException("Name is required");
        if (name.trim().length() > MAX_NAME_LENGTH) throw new IllegalArgumentException("Name is too long");
        this.name = name.trim();
        this.color = color;
        for (int i = 1; i <= PIECES; i++) pieces.add(new Piece(this, i));
    }

    /** @return the trimmed name */
    public String getName() { return name; }

    /** @return the player's color */
    public PlayerColor getColor() { return color; }

    /** @return the four pieces, in order 1 to 4 (read only) */
    public List<Piece> getPieces() { return Collections.unmodifiableList(pieces); }

    /**
     * @param number piece number, 1 to 4
     * @return that piece
     */
    public Piece getPiece(int number) { return pieces.get(number - 1); }

    /** @return true when all four pieces reached the Final Home */
    public boolean hasFinished() {
        return pieces.stream().allMatch(p -> p.getState() == PieceState.FINISHED);
    }

    /** @return true if a Surprise square made this player lose the next turn */
    public boolean isSkipNextTurn() { return skipNextTurn; }
    void setSkipNextTurn(boolean skip) { this.skipNextTurn = skip; }

    @Override
    public String toString() { return name + " (" + color + ")"; }
}
