package Model;

import java.io.IOException;
import java.util.List;

/**
 * Listens to a game and saves it to the history file when it ends (FR-5.04).
 * It only reacts to {@link GameEvent.Type#GAME_OVER}; every other event is ignored.
 * If the file cannot be written, the error is kept so the screen can show it (FR-5.07).
 */
public class HistoryRecorder implements GameListener {
    private final GameHistory history;
    private GameRecord saved;
    private String error;

    /** @param history the history file to append to */
    public HistoryRecorder(GameHistory history) { this.history = history; }

    @Override
    public void gameChanged(Game game, GameEvent event) {
        if (event.type() != GameEvent.Type.GAME_OVER || saved != null) return;
        List<String> places = game.getFinishOrder().stream().map(Player::getName).toList();
        try {
            saved = history.append(game.getDifficulty(), game.getEndedAt(), places, game.getDuration());
            error = null;
        } catch (IOException e) {
            error = "The result could not be saved: " + e.getMessage();
        }
    }

    /** @return the saved record, or null if the game has not ended or could not be saved */
    public GameRecord getSaved() { return saved; }

    /** @return why saving failed, or null if there was no error */
    public String getError() { return error; }
}
