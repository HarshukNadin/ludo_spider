package Model;

import static org.junit.jupiter.api.Assertions.*;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/** Observer pattern: listeners get typed game events (T-7.2). */
class GameEventTest {
    private Player red, blue;
    private Game game;
    private final List<GameEvent> events = new ArrayList<>();

    @BeforeEach
    void setUp() {
        red = new Player("Dana", PlayerColor.RED);
        blue = new Player("Omar", PlayerColor.BLUE);
        game = new Game(Difficulty.EASY, List.of(red, blue), null, new Random(1));
        for (Square s : game.getBoard().getTrack())
            if (s.getType() != SquareType.START && s.getType() != SquareType.ARROW) s.setType(SquareType.NORMAL);
        game.addListener((g, e) -> events.add(e));
    }

    private void finishRed() {
        for (int i = 1; i <= 3; i++) red.getPiece(i).setProgress(red.getFinalIndex());
        red.getPiece(4).setProgress(red.getFinalIndex() - 1);
        game.roll(new DiceResult(1, 1));
        game.move(red.getPiece(4));
    }

    @Test
    void rollThenMoveSendsRolledThenTurn() {
        red.getPiece(1).setProgress(3);
        game.roll(new DiceResult(2, 1));
        game.move(red.getPiece(1));
        assertEquals(List.of(GameEvent.Type.ROLLED, GameEvent.Type.TURN), events.stream().map(GameEvent::type).toList());
        assertEquals(blue, events.get(1).player(), "after the move it is Omar's turn");
        assertTrue(events.get(1).message().endsWith("Omar's turn."));
    }

    @Test
    void noMoveAndPassSendTheirOwnEvents() {
        game.roll(new DiceResult(4, 1));
        game.passTurn();
        assertEquals(List.of(GameEvent.Type.NO_MOVE, GameEvent.Type.TURN), events.stream().map(GameEvent::type).toList());
    }

    @Test
    void lastFinishSendsGameOver() {
        finishRed();
        assertEquals(GameEvent.Type.GAME_OVER, events.get(events.size() - 1).type());
    }

    @Test
    void everyListenerHearsEveryEventAndRemovedOnesDoNot() {
        List<GameEvent> second = new ArrayList<>();
        GameListener l = (g, e) -> second.add(e);
        game.addListener(l);
        game.roll(new DiceResult(4, 1));
        game.removeListener(l);
        game.passTurn();
        assertEquals(2, events.size());
        assertEquals(1, second.size(), "the removed listener missed the pass");
    }

    @Test
    void historyRecorderSavesTheGameWhenItEnds(@TempDir Path dir) throws Exception {
        GameHistory history = new GameHistory(dir.resolve("game_history.csv"));
        HistoryRecorder recorder = new HistoryRecorder(history);
        game.addListener(recorder);
        game.roll(new DiceResult(4, 1));
        assertNull(recorder.getSaved(), "nothing is saved before the end");
        game.passTurn();
        game.roll(new DiceResult(4, 1));
        game.passTurn();
        finishRed();
        assertNotNull(recorder.getSaved());
        assertNull(recorder.getError());
        assertEquals(1, history.load().size(), "saved exactly once");
        assertEquals(List.of("Dana", "Omar"), history.load().get(0).places().subList(0, 2));
    }

    @Test
    void historyRecorderKeepsTheErrorWhenTheFileCannotBeWritten(@TempDir Path dir) throws Exception {
        Path notAFile = Files.createDirectory(dir.resolve("game_history.csv"));
        HistoryRecorder recorder = new HistoryRecorder(new GameHistory(notAFile));
        game.addListener(recorder);
        finishRed();
        assertNull(recorder.getSaved());
        assertTrue(recorder.getError().startsWith("The result could not be saved"), recorder.getError());
    }
}
