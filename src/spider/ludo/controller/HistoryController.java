package spider.ludo.controller;

import java.io.IOException;
import spider.ludo.model.GameHistory;
import spider.ludo.view.HistoryView;

/** Loads the history CSV and shows it. */
public class HistoryController {
    private final HistoryView view;
    private final GameHistory history;

    public HistoryController(AppController app, HistoryView view, GameHistory history) {
        this.view = view;
        this.history = history;
        view.onBack(e -> app.showMenu());
    }

    public void open() {
        try {
            view.showRecords(history.load());
        } catch (IOException | RuntimeException e) {
            view.showError("The history file could not be read: " + e.getMessage());
        }
    }
}
