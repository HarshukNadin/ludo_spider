package Model;

/** A piece reached a Question square; the player has to answer before anything else (FR-4.02). */
public class AnsweringQuestionState extends GameState {
    @Override
    public GamePhase getPhase() { return GamePhase.ANSWERING_QUESTION; }

    @Override
    protected String whatToDo(Game game) { return "Answer the question first."; }

    @Override
    public void answerQuestion(Game game, int answer) { game.applyAnswer(answer); }
}
