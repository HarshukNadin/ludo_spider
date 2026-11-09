package controller;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

import model.Game;
import model.Player;
import view.SetupView;

/** Reads the setup form, validates it through the Model and starts the game. */
public class SetupController {
    private final AppController app;
    private final SetupView view;

    public SetupController(AppController app, SetupView view) {
        this.app = app;
        this.view = view;
        view.onStart(e -> start());
        view.onBack(e -> app.showMenu());
    }

    public void open() { view.reset(); }

    private void start() {
        try {
            List<Player> players = new ArrayList<>();
            for (int i = 0; i < view.getPlayerCount(); i++) {
                String name = view.getName(i);
                if (name.isBlank()) throw new IllegalArgumentException("Enter a name for player " + (i + 1));
                players.add(new Player(name, view.getColor(i)));
            }
            Game game = new Game(view.getDifficulty(), players, app.getQuestionBank(), new SecureRandom());
            app.startGame(game);
        } catch (IllegalArgumentException ex) {
            view.showError(ex.getMessage());
        }
    }
}
