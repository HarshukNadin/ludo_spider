package Model;

import static org.junit.jupiter.api.Assertions.*;

import java.nio.file.Path;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/** Question squares and their effects (FR-4.01 to FR-4.07). */
class QuestionSquareTest {
    @TempDir Path dir;
    private QuestionBank bank;

    @BeforeEach
    void setUp() {
        bank = new QuestionBank(dir.resolve("questions.csv"));
        int id = 1;
        for (Difficulty d : Difficulty.values())
            for (int k = 0; k < 3; k++)
                bank.add(new Question(id++, d, "Testing", d + " question " + k, List.of("a", "b", "c", "d"), 2));
    }

    private static Map<Difficulty, Integer> levels(Board b) {
        Map<Difficulty, Integer> count = new EnumMap<>(Difficulty.class);
        for (Route r : b.getRoutes())
            for (Square s : r.getSquares())
                if (s.getType() == SquareType.QUESTION) count.merge(s.getQuestionLevel(), 1, Integer::sum);
        return count;
    }

    @Test
    void mediumHasTwoQuestionSquaresOfEachLevel() {
        assertEquals(Map.of(Difficulty.EASY, 2, Difficulty.MEDIUM, 2, Difficulty.HARD, 2), levels(BoardFactory.createBoard(Difficulty.MEDIUM, new Random(1))));
    }

    @Test
    void hardHasThreeQuestionSquaresOfEachLevel() {
        assertEquals(Map.of(Difficulty.EASY, 3, Difficulty.MEDIUM, 3, Difficulty.HARD, 3), levels(BoardFactory.createBoard(Difficulty.HARD, new Random(1))));
    }

    @Test
    void otherSquaresHaveNoQuestionLevel() {
        for (Square s : BoardFactory.createBoard(Difficulty.HARD, new Random(2)).getTrack())
            if (s.getType() != SquareType.QUESTION) assertNull(s.getQuestionLevel());
    }

    /** Puts red one step before a Question square of the given level and moves onto it. */
    private Game landOnQuestion(Difficulty level, Player red, Player blue) {
        Game game = new Game(Difficulty.HARD, List.of(red, blue), bank, new Random(3));
        List<Square> path = game.getBoard().getPath(PlayerColor.RED);
        int at = -1;
        for (int i = 10; i < path.size() - 10 && at < 0; i++)
            if (path.get(i).getType() == SquareType.QUESTION && path.get(i).getQuestionLevel() == level) at = i;
        assertTrue(at > 0, "the board has a " + level + " question square on red's path");
        for (int i = at - 10; i <= at + 10; i++)
            if (i != at && path.get(i).getType() != SquareType.START && path.get(i).getType() != SquareType.ARROW) path.get(i).setType(SquareType.NORMAL);
        red.getPiece(1).setProgress(at - 1);
        game.roll(new DiceResult(1, 1));
        game.move(red.getPiece(1));
        return game;
    }

    @Test
    void theQuestionMatchesTheSquaresLevel() {
        for (Difficulty level : Difficulty.values()) {
            Player red = new Player("Dana", PlayerColor.RED), blue = new Player("Omar", PlayerColor.BLUE);
            Game game = landOnQuestion(level, red, blue);
            assertEquals(GamePhase.ANSWERING_QUESTION, game.getPhase());
            assertEquals(level, game.getPendingQuestion().getDifficulty());
            assertEquals(4, game.getPendingQuestion().getAnswers().size(), "exactly four answers (FR-4.03)");
        }
    }

    @Test
    void rightAnswerMovesForwardByTheQuestionLevel() {
        for (Difficulty level : Difficulty.values()) {
            Player red = new Player("Dana", PlayerColor.RED), blue = new Player("Omar", PlayerColor.BLUE);
            Game game = landOnQuestion(level, red, blue);
            int before = red.getPiece(1).getProgress();
            game.answerQuestion(2);
            assertEquals(before + level.getQuestionSteps(), red.getPiece(1).getProgress(), level + ": 1, 2 or 3 forward");
        }
    }

    @Test
    void wrongAnswerMovesBackAndShowsTheRightAnswer() {
        Player red = new Player("Dana", PlayerColor.RED), blue = new Player("Omar", PlayerColor.BLUE);
        Game game = landOnQuestion(Difficulty.HARD, red, blue);
        String[] last = {null};
        game.addListener((g, e) -> last[0] = e.message());
        int before = red.getPiece(1).getProgress();
        game.answerQuestion(1);
        assertEquals(before - 3, red.getPiece(1).getProgress());
        assertTrue(last[0].contains("The right answer was B (b)"), last[0]);
    }

    @Test
    void movingAfterAnAnswerDoesNotStartAnotherQuestion() {
        Player red = new Player("Dana", PlayerColor.RED), blue = new Player("Omar", PlayerColor.BLUE);
        Game game = landOnQuestion(Difficulty.EASY, red, blue);
        Square ahead = red.getBoard().squareAt(PlayerColor.RED, red.getPiece(1).getProgress() + 1);
        ahead.setType(SquareType.QUESTION);
        ahead.setQuestionLevel(Difficulty.EASY);
        game.answerQuestion(2);
        assertNotEquals(GamePhase.ANSWERING_QUESTION, game.getPhase(), "no second question (FR-4.06)");
    }

    @Test
    void questionsRepeatOnlyAfterAllOfTheLevelWereAsked() {
        Random r = new Random(4);
        Set<Integer> firstRound = new HashSet<>();
        for (int i = 0; i < 3; i++) firstRound.add(bank.randomQuestion(Difficulty.MEDIUM, r).get().getId());
        assertEquals(3, firstRound.size(), "three different questions before any repeat (FR-4.07)");
        Set<Integer> secondRound = new HashSet<>();
        for (int i = 0; i < 3; i++) secondRound.add(bank.randomQuestion(Difficulty.MEDIUM, r).get().getId());
        assertEquals(firstRound, secondRound, "then a new round of the same three");
    }

    @Test
    void goingBackThreeStepsCanCapture() {
        Player red = new Player("Dana", PlayerColor.RED), green = new Player("Omar", PlayerColor.GREEN);
        Game game = new Game(Difficulty.EASY, List.of(red, green), null, new Random(5));
        for (Square s : game.getBoard().getTrack())
            if (s.getType() != SquareType.START && s.getType() != SquareType.ARROW) s.setType(SquareType.NORMAL);
        red.getPiece(1).setProgress(20);
        Square behind = game.getBoard().squareAt(PlayerColor.RED, 17);
        green.getPiece(1).setProgress(game.getBoard().getPath(PlayerColor.GREEN).indexOf(behind));
        String msg = game.applySurprise(red.getPiece(1), Surprise.THREE_STEPS_BACK);
        assertEquals(17, red.getPiece(1).getProgress());
        assertEquals(PieceState.IN_CASTLE, green.getPiece(1).getState(), "the move back follows the capture rule (FR-4.06)");
        assertTrue(msg.contains("Captured"), msg);
    }
}
