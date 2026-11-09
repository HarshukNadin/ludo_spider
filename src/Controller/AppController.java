package Controller;

import java.io.IOException;
import java.nio.file.Path;
import javax.swing.JOptionPane;

import Model.Game;
import Model.GameHistory;
import Model.QuestionBank;
import View.GameView;
import View.HistoryView;
import View.MainFrame;
import View.MainMenuView;
import View.SetupView;

/** Creates the screens and moves between them. Owns the shared data (question bank and history). */
public class AppController {
    public static final String MENU = "menu", SETUP = "setup", GAME = "game", HISTORY = "history";

    private final MainFrame frame = new MainFrame();
    private final QuestionBank questionBank;
    private final GameHistory history;
    private final SetupController setupController;
    private final GameController gameController;
    private final HistoryController historyController;

    public AppController(Path dataDir) {
        questionBank = new QuestionBank(dataDir.resolve("questions.csv"));
        history = new GameHistory(dataDir.resolve("game_history.csv"));
        try {
            questionBank.load();
        } catch (IOException | RuntimeException e) {
            JOptionPane.showMessageDialog(null, "Could not load the question bank: " + e.getMessage());
        }

        MainMenuView menu = new MainMenuView();
        SetupView setupView = new SetupView();
        GameView gameView = new GameView();
        HistoryView historyView = new HistoryView();
        frame.addScreen(MENU, menu);
        frame.addScreen(SETUP, setupView);
        frame.addScreen(GAME, gameView);
        frame.addScreen(HISTORY, historyView);

        setupController = new SetupController(this, setupView);
        gameController = new GameController(this, gameView, history);
        historyController = new HistoryController(this, historyView, history);

        menu.onNewGame(e -> showSetup());
        menu.onHistory(e -> showHistory());
        menu.onExit(e -> System.exit(0));
    }

    public void start() {
        frame.showScreen(MENU);
        frame.setVisible(true);
    }

    public QuestionBank getQuestionBank() { return questionBank; }

    public void showMenu() { frame.showScreen(MENU); }
    public void showSetup() { setupController.open(); frame.showScreen(SETUP); }
    public void showHistory() { historyController.open(); frame.showScreen(HISTORY); }
    public void startGame(Game game) { gameController.open(game); frame.showScreen(GAME); }
}
