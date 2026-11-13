package Model;

/** What the game is waiting for. */
public enum GamePhase {
    /** The current player has to roll. */
    WAITING_FOR_ROLL,
    /** The current player has to pick one of the movable pieces. */
    CHOOSING_PIECE,
    /** No piece can move with the roll; the reason is shown until the player passes the turn (FR-3.07). */
    NO_MOVE,
    /** A question square was reached and the question has to be answered. */
    ANSWERING_QUESTION,
    /** All players but one have finished. */
    GAME_OVER
}
