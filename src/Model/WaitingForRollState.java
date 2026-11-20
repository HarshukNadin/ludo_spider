package Model;

/** The current player has to roll. Only rolling is allowed. */
public class WaitingForRollState extends GameState {
    @Override
    public GamePhase getPhase() { return GamePhase.WAITING_FOR_ROLL; }

    @Override
    protected String whatToDo(Game game) { return game.getCurrentPlayer().getName() + " has to roll the dice first."; }

    @Override
    public void roll(Game game, DiceResult result) { game.applyRoll(result); }
}
