package Controller;

import javax.swing.SwingUtilities;

import Model.Game;
import Model.GameEvent;
import Model.GameHistory;
import Model.HistoryRecorder;
import View.GameView;

/** Turns button clicks into Model calls, and redraws the View when the Model changes. */
public class GameController {
    private final AppController app;
    private final GameView view;
    private final GameHistory history;
    private Game game;
    private HistoryRecorder recorder;

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
        // Two observers of the same game: the recorder saves it when it ends, the screen shows every event.
        recorder = new HistoryRecorder(history);
        game.addListener(recorder);
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

    private void onGameChanged(Game g, GameEvent event) {
        view.render(g, event.message());
        if (event.type() == GameEvent.Type.QUESTION) {
            // Ask after this event finishes, so the board is drawn first.
            SwingUtilities.invokeLater(() -> g.answerQuestion(view.askQuestion(g.getPendingQuestion())));
        } else if (event.type() == GameEvent.Type.GAME_OVER) {
            SwingUtilities.invokeLater(this::finish);
        }
    }

    /** The game has ended: the recorder has already saved it, so show the end screen (FR-5.03). */
    private void finish() {
        app.showEnd(game, recorder.getSaved(), recorder.getError());
    }
}
