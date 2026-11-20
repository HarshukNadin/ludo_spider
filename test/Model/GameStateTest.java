package Model;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Random;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/** Each game state accepts only its own action (State pattern, T-8.3). */
class GameStateTest {
    private Player red, blue;
    private Game game;

    @BeforeEach
    void setUp() {
        red = new Player("Dana", PlayerColor.RED);
        blue = new Player("Omar", PlayerColor.BLUE);
        game = new Game(Difficulty.EASY, List.of(red, blue), null, new Random(1));
        for (Square s : game.getBoard().getTrack())
            if (s.getType() != SquareType.START && s.getType() != SquareType.ARROW) s.setType(SquareType.NORMAL);
    }

    @Test
    void aNewGameWaitsForARoll() {
        assertInstanceOf(WaitingForRollState.class, game.getState());
        assertEquals(GamePhase.WAITING_FOR_ROLL, game.getPhase());
        IllegalStateException e = assertThrows(IllegalStateException.class, () -> game.move(red.getPiece(1)));
        assertEquals("Dana has to roll the dice first.", e.getMessage());
        assertThrows(IllegalStateException.class, () -> game.passTurn());
        assertThrows(IllegalStateException.class, () -> game.answerQuestion(1));
    }

    @Test
    void aSixLeadsToChoosingAPiece() {
        game.roll(new DiceResult(6, 1));
        assertInstanceOf(ChoosingPieceState.class, game.getState());
        IllegalStateException e = assertThrows(IllegalStateException.class, () -> game.roll(new DiceResult(2, 1)));
        assertEquals("Dana has to choose a piece to move first.", e.getMessage());
        assertThrows(IllegalStateException.class, () -> game.passTurn());
    }

    @Test
    void noLegalMoveLeadsToNoMoveUntilPassed() {
        game.roll(new DiceResult(3, 1));
        assertInstanceOf(NoMoveState.class, game.getState());
        assertThrows(IllegalStateException.class, () -> game.move(red.getPiece(1)));
        assertThrows(IllegalStateException.class, () -> game.roll(new DiceResult(6, 1)));
        game.passTurn();
        assertInstanceOf(WaitingForRollState.class, game.getState());
        assertEquals(blue, game.getCurrentPlayer());
    }

    @Test
    void afterAMoveTheNextPlayerRolls() {
        red.getPiece(1).setProgress(4);
        game.roll(new DiceResult(2, 1));
        game.move(red.getPiece(1));
        assertInstanceOf(WaitingForRollState.class, game.getState());
        assertEquals(blue, game.getCurrentPlayer());
    }

    @Test
    void finishedGameAcceptsNothing() {
        for (int i = 1; i <= 3; i++) red.getPiece(i).setProgress(red.getFinalIndex());
        red.getPiece(4).setProgress(red.getFinalIndex() - 1);
        game.roll(new DiceResult(1, 1));
        game.move(red.getPiece(4));
        assertInstanceOf(GameOverState.class, game.getState());
        IllegalStateException e = assertThrows(IllegalStateException.class, () -> game.roll(new DiceResult(6, 1)));
        assertEquals("The game is over. Start a new game from the menu.", e.getMessage());
        assertThrows(IllegalStateException.class, () -> game.move(blue.getPiece(1)));
        assertThrows(IllegalStateException.class, () -> game.passTurn());
        assertThrows(IllegalStateException.class, () -> game.answerQuestion(2));
    }

    @Test
    void everyStateNamesItsPhase() {
        assertEquals(GamePhase.WAITING_FOR_ROLL, new WaitingForRollState().getPhase());
        assertEquals(GamePhase.CHOOSING_PIECE, new ChoosingPieceState().getPhase());
        assertEquals(GamePhase.NO_MOVE, new NoMoveState().getPhase());
        assertEquals(GamePhase.ANSWERING_QUESTION, new AnsweringQuestionState().getPhase());
        assertEquals(GamePhase.GAME_OVER, new GameOverState().getPhase());
    }
}
