package Controller;

import java.io.IOException;
import java.util.List;
import javax.swing.SwingUtilities;

import Model.Game;
import Model.GameHistory;
import Model.GamePhase;
import Model.Player;
import View.GameView;

/** Turns button clicks into Model calls, and redraws the View when the Model changes. */
public class GameController {
    private final AppController app;
    private final GameView view;
    private final GameHistory history;
    private Game game;

    /**
     * Connects the game screen's buttons and keys to the game.
     *
     * @param app     used to go back to the menu
     * @param view    the game screen
     * @param history where finished games are saved
     */
    public GameController(AppController app, GameView view, GameHistory history) {
        this.app = app;
        this.view = view;
        this.history = history;
        view.onRoll(e -> act(() -> game.roll()));
        view.onNext(e -> act(() -> game.passTurn()));
        view.onPiece(n -> act(() -> game.move(game.getCurrentPlayer().getPiece(n))));
        view.onQuit(e -> app.showMenu());
    }

    /**
     * Starts showing a new game: listens to its changes and clears the old log.
     *
     * @param game the game to play
     */
    public void open(Game game) {
        this.game = game;
        game.addListener(this::onGameChanged);
        view.clearLog();
        view.render(game, "Game started on " + game.getDifficulty().csvName() + ". " + game.getCurrentPlayer().getName() + " goes first.");
    }

    private void act(Runnable action) {
        try {
            action.run();
        } catch (IllegalStateException | IllegalArgumentException ex) {
            view.showMessage(ex.getMessage());
        }
    }

    private void onGameChanged(Game g, String message) {
        view.render(g, message);
        if (g.getPhase() == GamePhase.ANSWERING_QUESTION) {
            // Ask after this event finishes, so the board is drawn first.
            SwingUtilities.invokeLater(() -> g.answerQuestion(view.askQuestion(g.getPendingQuestion())));
        } else if (g.getPhase() == GamePhase.GAME_OVER) {
            SwingUtilities.invokeLater(this::finish);
        }
    }

    private void finish() {
        List<String> places = game.getFinishOrder().stream().map(Player::getName).toList();
        StringBuilder sb = new StringBuilder("Final ranking:\n");
        for (int i = 0; i < places.size(); i++) sb.append(i + 1).append(". ").append(places.get(i)).append('\n');
        sb.append("Game time: ").append(GameHistory.formatDuration(game.getDuration()));
        try {
            history.append(game.getDifficulty(), game.getEndedAt(), places, game.getDuration());
        } catch (IOException e) {
            sb.append("\n\nThe result could not be saved: ").append(e.getMessage());
        }
        view.showMessage(sb.toString());
        app.showHistory();
    }
}
