package Model;

import java.util.Random;

/** Surprise square events and their chances (SRS Feature 4). */
public enum Surprise {
    GO_TO_FINAL_HOME(10, "Your piece jumps straight to the Final Home!"),
    OPPONENT_DICE(10, "An opponent's next roll counts for you."),
    BACK_TO_CASTLE(10, "Your piece goes back to its castle."),
    THREE_STEPS_BACK(30, "Your piece moves 3 steps back."),
    SKIP_NEXT_TURN(30, "You skip your next turn."),
    ULTRABOOST_ROLL(10, "Your next roll uses the UltraBoost die.");

    private final int percent;
    private final String message;

    Surprise(int percent, String message) {
        this.percent = percent;
        this.message = message;
    }

    /** @return the chance of this event, in percent */
    public int getPercent() { return percent; }
    /** @return the text shown to the players */
    public String getMessage() { return message; }

    /**
     * Picks one event; each event comes up with its own chance ({@link #getPercent}).
     *
     * @param random source of randomness
     * @return the event
     */
    public static Surprise draw(Random random) {
        int r = random.nextInt(100), acc = 0;
        for (Surprise s : values()) {
            acc += s.percent;
            if (r < acc) return s;
        }
        return SKIP_NEXT_TURN;
    }
}
