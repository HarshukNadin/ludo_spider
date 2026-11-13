package Model;

import static org.junit.jupiter.api.Assertions.*;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class CsvFilesTest {
    @TempDir Path dir;

    @Test
    void historyIsWrittenInCourseFormat() throws Exception {
        GameHistory h = new GameHistory(dir.resolve("game_history.csv"));
        h.append(Difficulty.HARD, LocalDateTime.of(2026, 10, 19, 12, 1, 0), List.of("Mor", "Noy", "Safa", "Muhammed"), Duration.ofSeconds(3904));
        List<String> lines = Files.readAllLines(dir.resolve("game_history.csv"));
        assertEquals(GameHistory.HEADER, lines.get(0));
        assertEquals("1,hard,19/10/2026;12:01:00,Mor,Noy,Safa,Muhammed,01:05:04", lines.get(1));
        GameRecord r = h.load().get(0);
        assertEquals("Noy", r.place(2));
    }

    @Test
    void readsTheCourseSampleFile() throws Exception {
        Path f = dir.resolve("sample.csv");
        Files.writeString(f, GameHistory.HEADER + "\r\n1,hard,19/10/2026;12:01:00 ,Mor,Noy,Safa,Muhammed,01:05:04\r\n");
        GameRecord r = new GameHistory(f).load().get(0);
        assertEquals(Difficulty.HARD, r.difficulty());
        assertEquals("19/10/2026;12:01:00", r.timestamp());
    }

    @Test
    void questionBankLoadsAllQuestionsAndRoundTrips() throws Exception {
        QuestionBank bank = new QuestionBank(Path.of("data/questions.csv"));
        bank.load();
        assertEquals(42, bank.getAll().size());
        for (Difficulty d : Difficulty.values())
            assertEquals(14, bank.getAll().stream().filter(q -> q.getDifficulty() == d).count());
        Path copy = dir.resolve("q.csv");
        Files.copy(Path.of("data/questions.csv"), copy);
        QuestionBank b2 = new QuestionBank(copy);
        b2.load();
        b2.save();
        b2.load();
        assertEquals(bank.getAll().get(0).getAnswers(), b2.getAll().get(0).getAnswers());
        assertTrue(b2.randomQuestion(Difficulty.MEDIUM, new Random()).isPresent());
    }
}
