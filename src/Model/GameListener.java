package Model;

/**
 * Observer pattern: anything that wants to know what happens in a {@link Game}.
 * The game keeps a list of listeners and sends each of them every {@link GameEvent}, without
 * knowing who they are. The game screen is one listener and {@link HistoryRecorder} is another,
 * so the Model has no code for the screen or for saving.
 */
public interface GameListener {

    /**
     * Called after every change in the game.
     *
     * @param game  the game that changed
     * @param event what happened
     */
    void gameChanged(Game game, GameEvent event);
}
