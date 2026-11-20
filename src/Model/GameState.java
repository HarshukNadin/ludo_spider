package Model;

/**
 * State pattern: what the game is waiting for, as an object.
 * <p>
 * {@link Game} hands every player action to its current state. Each state accepts only the
 * action that makes sense in it and turns the others down with a message that says what to do
 * instead. This replaces the checks of {@link GamePhase} that each action used to do, so a new
 * state (for example a paused game) is a new class and the actions stay the same.
 */
public abstract class GameState {

    /** @return the phase this state stands for, for the screen */
    public abstract GamePhase getPhase();

    /**
     * Says what the player has to do in this state; used when an action is not allowed.
     *
     * @param game the game
     * @return a message for the players
     */
    protected abstract String whatToDo(Game game);

    /**
     * The current player rolls.
     *
     * @param game   the game
     * @param result the dice values
     * @throws IllegalStateException unless the game waits for a roll
     */
    public void roll(Game game, DiceResult result) { throw notNow(game); }

    /**
     * The current player moves a piece.
     *
     * @param game  the game
     * @param piece the piece
     * @throws IllegalStateException unless the game waits for a piece to be chosen
     */
    public void move(Game game, Piece piece) { throw notNow(game); }

    /**
     * The current player passes a turn in which nothing can move.
     *
     * @param game the game
     * @throws IllegalStateException unless no piece can move
     */
    public void passTurn(Game game) { throw notNow(game); }

    /**
     * The current player answers the question.
     *
     * @param game   the game
     * @param answer 1 to 4
     * @throws IllegalStateException unless a question is waiting for an answer
     */
    public void answerQuestion(Game game, int answer) { throw notNow(game); }

    private IllegalStateException notNow(Game game) { return new IllegalStateException(whatToDo(game)); }
}
