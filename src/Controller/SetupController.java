package Controller;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

import Model.Game;
import Model.Player;
import Model.PlayerColor;
import Model.SetupValidation;
import View.SetupView;

/** Reads the setup form, checks it with {@link SetupValidation} and starts the game (FR-1.07, FR-1.08). */
public class SetupController {
    private final AppController app;
    private final SetupView view;

    /**
     * @param app  used to go back to the menu or open the game screen
     * @param view the setup screen
     */
    public SetupController(AppController app, SetupView view) {
        this.app = app;
        this.view = view;
        view.onStart(e -> start());
        view.onBack(e -> app.showMenu());
    }

    /** Shows an empty form. */
    public void open() { view.reset(); }

    private void start() {
        List<String> names = new ArrayList<>();
        List<PlayerColor> colors = new ArrayList<>();
        for (int i = 0; i < Game.MAX_PLAYERS; i++) {
            names.add(view.getName(i));
            colors.add(view.getColor(i));
        }
        Integer count = view.getPlayerCount();
        SetupValidation check = new SetupValidation(count, view.getDifficulty(), names, colors);
        showProblems(check);
        if (!check.isValid()) {
            view.showError("Fix the fields marked in red to start the game.");
            return;
        }
        view.showError(" ");
        List<Player> players = new ArrayList<>();
        for (int i = 0; i < count; i++) players.add(new Player(names.get(i), colors.get(i)));
        app.startGame(new Game(view.getDifficulty(), players, app.getQuestionBank(), new SecureRandom()));
    }

    /** Puts each message from the check under its own field; fields without a problem are cleared. */
    private void showProblems(SetupValidation check) {
        view.showCountError(check.getPlayerCountError());
        view.showDifficultyError(check.getDifficultyError());
        for (int i = 0; i < Game.MAX_PLAYERS; i++) {
            view.showNameError(i, check.getNameError(i));
            view.showColorError(i, check.getColorError(i));
        }
    }
}
