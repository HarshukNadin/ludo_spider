package Model;

/**
 * What just happened in a game, sent to every {@link GameListener} (Observer pattern).
 *
 * @param type    the kind of event, so a listener can react only to the events it needs
 * @param message a sentence for the players, for example "Dana rolled 4. Choose a piece."
 * @param player  the player whose turn it is after the event
 */
public record GameEvent(Type type, String message, Player player) {

    /** The kinds of events a game sends. */
    public enum Type {
        /** The dice were rolled and a piece can be chosen. */
        ROLLED,
        /** The dice were rolled and no piece can move (FR-3.07). */
        NO_MOVE,
        /** A piece reached a Question square and a question waits for an answer (FR-4.02). */
        QUESTION,
        /** A move, answer or pass ended; the same player rolls again after a 6, otherwise the next player. */
        TURN,
        /** All players but one have finished (FR-5.02). */
        GAME_OVER
    }
}
