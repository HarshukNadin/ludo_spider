package Model;

/** No piece can move with the roll; the reason stays on screen until the player passes (FR-3.07). */
public class NoMoveState extends GameState {
    @Override
    public GamePhase getPhase() { return GamePhase.NO_MOVE; }

    @Override
    protected String whatToDo(Game game) { return game.getCurrentPlayer().getName() + " cannot move. Click Next player to go on."; }

    @Override
    public void passTurn(Game game) { game.applyPassTurn(); }
}
