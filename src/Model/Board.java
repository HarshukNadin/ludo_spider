package Model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * The game board: a shared track of 52 squares plus a 6-square home path per color.
 * Special squares are placed at random according to the difficulty.
 * (The extra outer routes of Medium and Hard are a later step.)
 */
public class Board {
    public static final int TRACK_LENGTH = 52;
    public static final int HOME_PATH_LENGTH = 6;
    /** Last progress index on a piece's own route: 51 track squares (0..50) then 6 home squares. */
    public static final int FINAL_INDEX = TRACK_LENGTH - 2 + HOME_PATH_LENGTH; // 56

    private final Difficulty difficulty;
    private final List<Square> track = new ArrayList<>();

    public Board(Difficulty difficulty, Random random) {
        this.difficulty = difficulty;
        for (int i = 0; i < TRACK_LENGTH; i++) track.add(new Square(i, SquareType.NORMAL));
        for (PlayerColor c : PlayerColor.values()) track.get(c.getStartSquare()).setType(SquareType.START);
        placeSpecialSquares(random);
    }

    private void placeSpecialSquares(Random random) {
        List<Square> free = new ArrayList<>();
        for (Square s : track) if (s.getType() == SquareType.NORMAL) free.add(s);
        Collections.shuffle(free, random);
        int i = 0;
        for (int k = 0; k < difficulty.getProtectionSquares(); k++) free.get(i++).setType(SquareType.PROTECTION);
        for (int k = 0; k < difficulty.getQuestionSquares(); k++) free.get(i++).setType(SquareType.QUESTION);
        for (int k = 0; k < difficulty.getSurpriseSquares(); k++) free.get(i++).setType(SquareType.SURPRISE);
    }

    public Difficulty getDifficulty() { return difficulty; }
    public List<Square> getTrack() { return Collections.unmodifiableList(track); }
    public Square getSquare(int trackIndex) { return track.get(trackIndex); }

    /** Converts a piece's progress on its own route to a shared-track index, or -1 if not on the shared track. */
    public static int toTrackIndex(PlayerColor color, int progress) {
        if (progress < 0 || progress >= TRACK_LENGTH - 1) return -1;
        return (color.getStartSquare() + progress) % TRACK_LENGTH;
    }

    /** Count of squares of the given type, used by tests and the view. */
    public long count(SquareType type) {
        return track.stream().filter(s -> s.getType() == type).count();
    }
}
