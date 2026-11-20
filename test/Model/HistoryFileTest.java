package Model;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/** The history file against the course template (FR-5.04 to FR-5.09). */
class HistoryFileTest {
    @TempDir Path dir;
    private static final LocalDateTime END = LocalDateTime.of(2026, 12, 1, 18, 30, 5);

    private GameHistory history() { return new GameHistory(dir.resolve("game_history.csv")); }

    @Test
    void headerIsExactlyTheCourseTemplate() {
        assertEquals("gameID,difficulty,timestamp(DD/MM/YYYY; HH:MM:SS),1st_place_name,2nd_place_name,"
                + "3rd_place_name,4rd_place_name,game_duration(HH:MM:SS)", GameHistory.HEADER);
    }

    @Test
    void newFileGetsTheHeaderAndIdsStartAtOne() throws Exception {
        GameRecord r = history().append(Difficulty.EASY, END, List.of("Dana", "Omar"), Duration.ofSeconds(65));
        assertEquals(1, r.gameId());
        List<String> lines = Files.readAllLines(dir.resolve("game_history.csv"));
        assertEquals(GameHistory.HEADER, lines.get(0));
        assertEquals("1,easy,01/12/2026;18:30:05,Dana,Omar,,,00:01:05", lines.get(1), "unused places stay empty (FR-5.06)");
    }

    @Test
    void idsKeepGoingUp() throws Exception {
        GameHistory h = history();
        h.append(Difficulty.EASY, END, List.of("A", "B"), Duration.ZERO);
        h.append(Difficulty.HARD, END, List.of("C", "D", "E"), Duration.ZERO);
        assertEquals(3, h.append(Difficulty.MEDIUM, END, List.of("F", "G"), Duration.ZERO).gameId());
        assertEquals(3, h.load().size());
    }

    @Test
    void appendingToTheCourseSampleWithoutAFinalLineBreak() throws Exception {
        Path f = dir.resolve("game_history.csv");
        Files.writeString(f, GameHistory.HEADER + "\r\n1,hard,19/10/2026;12:01:00 ,Mor,Noy,Safa,Muhammed,01:05:04");
        new GameHistory(f).append(Difficulty.MEDIUM, END, List.of("Dana", "Omar"), Duration.ofMinutes(12));
        List<GameRecord> all = new GameHistory(f).load();
        assertEquals(2, all.size());
        assertEquals("Muhammed", all.get(0).place(4), "the old line is not changed");
        assertEquals(2, all.get(1).gameId());
    }

    @Test
    void namesWithCommasAreQuoted() throws Exception {
        GameHistory h = history();
        h.append(Difficulty.EASY, END, List.of("Lee, Jr", "Omar"), Duration.ZERO);
        assertEquals("Lee, Jr", h.load().get(0).place(1));
    }

    @Test
    void damagedLineIsAnErrorWithItsLineNumber() throws Exception {
        Files.writeString(dir.resolve("game_history.csv"), GameHistory.HEADER + "\n1,easy,01/12/2026;18:30:05,A,B,,,00:00:01\nx,easy,oops\n");
        IOException e = assertThrows(IOException.class, () -> history().load());
        assertEquals("Line 3 of the history file is damaged", e.getMessage());
    }

    @Test
    void unknownLevelIsAnError() throws Exception {
        Files.writeString(dir.resolve("game_history.csv"), GameHistory.HEADER + "\n1,expert,01/12/2026;18:30:05,A,B,,,00:00:01\n");
        assertThrows(IOException.class, () -> history().load(), "never shown as valid (FR-5.09)");
    }

    @Test
    void fileWithAnotherHeaderIsNotRead() throws Exception {
        Files.writeString(dir.resolve("game_history.csv"), "id,level,when\n1,easy,today\n");
        assertThrows(IOException.class, () -> history().load());
    }

    @Test
    void savingToADamagedFileFailsWithoutChangingIt() throws Exception {
        Path f = dir.resolve("game_history.csv");
        String damaged = GameHistory.HEADER + "\nnot,a,record\n";
        Files.writeString(f, damaged);
        assertThrows(IOException.class, () -> history().append(Difficulty.EASY, END, List.of("A", "B"), Duration.ZERO));
        assertEquals(damaged, Files.readString(f));
    }

    @Test
    void noTemporaryFilesAreLeft() throws Exception {
        history().append(Difficulty.EASY, END, List.of("A", "B"), Duration.ZERO);
        try (Stream<Path> files = Files.list(dir)) {
            assertEquals(List.of("game_history.csv"), files.map(p -> p.getFileName().toString()).toList());
        }
    }

    @Test
    void missingOrEmptyFileMeansNoGames() throws Exception {
        assertTrue(history().load().isEmpty());
        Files.writeString(dir.resolve("game_history.csv"), "");
        assertTrue(history().load().isEmpty());
    }
}
