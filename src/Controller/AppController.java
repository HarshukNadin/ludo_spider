package Controller;

import java.io.IOException;
import java.nio.file.Path;
import javax.swing.JOptionPane;

import Model.Game;
import Model.GameHistory;
import Model.GameRecord;
import Model.QuestionBank;
import View.EndView;
import View.GameView;
import View.HistoryView;
import View.MainFrame;
import View.MainMenuView;
import View.RulesView;
import View.SetupView;

/** Creates the screens and moves between them. Owns the shared data (question bank and history). */
public class AppController {
    public static final String MENU = "menu", SETUP = "setup", GAME = "game", HISTORY = "history", RULES = "rules", END = "end";

    private final MainFrame frame = new MainFrame();
    private final MainMenuView menu;
    private final QuestionBank questionBank;
    private final GameHistory history;
    private final SetupController setupController;
    private final GameController gameController;
    private final HistoryController historyController;
    private final EndView endView;

    /**
     * Loads the question bank, builds every screen with its controller and wires the menu.
     * If the question bank cannot be read, a message is shown and the game still opens.
     *
     * @param dataDir folder with questions.csv and game_history.csv
     */
    public AppController(Path dataDir) {
        questionBank = new QuestionBank(dataDir.resolve("questions.csv"));
        history = new GameHistory(dataDir.resolve("game_history.csv"));
        try {
            questionBank.load();
        } catch (IOException | RuntimeException e) {
            JOptionPane.showMessageDialog(null, "Could not load the question bank: " + e.getMessage());
        }

        menu = new MainMenuView();
        SetupView setupView = new SetupView();
        GameView gameView = new GameView();
        HistoryView historyView = new HistoryView();
        RulesView rulesView = new RulesView();
        endView = new EndView();
        frame.addScreen(MENU, menu);
        frame.addScreen(SETUP, setupView);
        frame.addScreen(GAME, gameView);
        frame.addScreen(HISTORY, historyView);
        frame.addScreen(RULES, rulesView);
        frame.addScreen(END, endView);

        setupController = new SetupController(this, setupView);
        gameController = new GameController(this, gameView, history);
        historyController = new HistoryController(this, historyView, history);

        menu.onNewGame(e -> showSetup());
        menu.onHistory(e -> showHistory());
        menu.onRules(e -> frame.showScreen(RULES));
        rulesView.onBack(e -> showMenu());
        endView.onPlayAgain(e -> showSetup());
        endView.onHistory(e -> showHistory());
        endView.onMenu(e -> showMenu());
        menu.onQuestions(e -> JOptionPane.showMessageDialog(frame,
                "Question management needs the administrator login, which comes in Iteration 3.",
                "Question management", JOptionPane.INFORMATION_MESSAGE));
        menu.onExit(e -> System.exit(0));
    }

    /** Shows the window with the main menu. */
    public void start() {
        frame.setVisible(true);
        showMenu();
    }

    /** @return the question bank that new games take their questions from */
    public QuestionBank getQuestionBank() { return questionBank; }

    /** Shows the main menu and puts the focus on its first option. */
    public void showMenu() { frame.showScreen(MENU); menu.focusFirst(); }
    /** Opens the New game screen with an empty form. */
    public void showSetup() { setupController.open(); frame.showScreen(SETUP); }
    /** Opens the Game history screen with the records read again from the file. */
    public void showHistory() { historyController.open(); frame.showScreen(HISTORY); }
    /**
     * Shows the game screen for a game that has just been set up.
     *
     * @param game the new game
     */
    public void startGame(Game game) { gameController.open(game); frame.showScreen(GAME); }

    /**
     * Shows the end screen of a finished game (FR-5.03).
     *
     * @param game   the finished game
     * @param record its history record, or null if it was not saved
     * @param error  why it was not saved, or null
     */
    public void showEnd(Game game, GameRecord record, String error) { endView.show(game, record, error); frame.showScreen(END); }
}
