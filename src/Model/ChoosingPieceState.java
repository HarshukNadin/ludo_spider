package Model;

/** The roll is known and at least one piece can move; the player picks one (FR-3.05). */
public class ChoosingPieceState extends GameState {
    @Override
    public GamePhase getPhase() { return GamePhase.CHOOSING_PIECE; }

    @Override
    protected String whatToDo(Game game) { return game.getCurrentPlayer().getName() + " has to choose a piece to move first."; }

    @Override
    public void move(Game game, Piece piece) { game.applyMove(piece); }
}
