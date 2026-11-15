package Controller;

import java.io.IOException;

import Model.GameHistory;
import View.HistoryView;

/** Loads the history CSV and shows it. */
public class HistoryController {
    private final HistoryView view;
    private final GameHistory history;

    /**
     * @param app     used to go back to the menu
     * @param view    the history screen
     * @param history the history file
     */
    public HistoryController(AppController app, HistoryView view, GameHistory history) {
        this.view = view;
        this.history = history;
        view.onBack(e -> app.showMenu());
    }

    /** Reads the history file and shows its records, or an error if the file cannot be read (FR-5.09). */
    public void open() {
        try {
            view.showRecords(history.load());
        } catch (IOException | RuntimeException e) {
            view.showError("The history file could not be read: " + e.getMessage());
        }
    }
}
