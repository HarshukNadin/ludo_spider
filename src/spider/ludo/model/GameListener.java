package spider.ludo.model;

/** Observer: the Model tells listeners (controllers/views) that its state changed. */
public interface GameListener {
    void gameChanged(Game game, String message);
}
