package Model;

/** All players but one have finished (FR-5.02). No action is allowed any more. */
public class GameOverState extends GameState {
    @Override
    public GamePhase getPhase() { return GamePhase.GAME_OVER; }

    @Override
    protected String whatToDo(Game game) { return "The game is over. Start a new game from the menu."; }
}
