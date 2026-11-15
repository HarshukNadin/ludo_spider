package Model;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/** Reads and appends finished games in the course CSV format (SRS Feature 5). */
public class GameHistory {
    public static final String HEADER = "gameID,difficulty,timestamp(DD/MM/YYYY; HH:MM:SS),1st_place_name,2nd_place_name,"
            + "3rd_place_name,4rd_place_name,game_duration(HH:MM:SS)";
    public static final DateTimeFormatter TIMESTAMP = DateTimeFormatter.ofPattern("dd/MM/yyyy;HH:mm:ss");

    private final Path file;

    /** @param file the game_history.csv file */
    public GameHistory(Path file) { this.file = file; }

    /**
     * Reads all saved games. A missing file means no games yet.
     *
     * @return the records in file order
     * @throws IOException if the file cannot be read or a line is damaged
     */
    public List<GameRecord> load() throws IOException {
        List<GameRecord> out = new ArrayList<>();
        if (!Files.exists(file)) return out;
        List<String> lines = Files.readAllLines(file, StandardCharsets.UTF_8);
        for (int i = 1; i < lines.size(); i++) {
            if (lines.get(i).isBlank()) continue;
            List<String> f = CsvUtil.parseLine(lines.get(i));
            if (f.size() < 8) throw new IOException("Line " + (i + 1) + " of the history file is damaged");
            List<String> places = new ArrayList<>();
            for (int p = 3; p <= 6; p++) if (!f.get(p).isBlank()) places.add(f.get(p).trim());
            out.add(new GameRecord(Integer.parseInt(f.get(0).trim()), Difficulty.fromCsv(f.get(1)), f.get(2).trim(), places, f.get(7).trim()));
        }
        return out;
    }

    /**
     * Adds one finished game to the file, with the next gameID; writes the header first if the file is new (FR-5.04, FR-5.07).
     *
     * @param difficulty the game's level
     * @param endedAt    when it ended
     * @param places     names in finishing order
     * @param duration   how long it took
     * @return the saved record
     * @throws IOException if the file cannot be written
     */
    public GameRecord append(Difficulty difficulty, LocalDateTime endedAt, List<String> places, Duration duration) throws IOException {
        int id = load().stream().mapToInt(GameRecord::gameId).max().orElse(0) + 1;
        GameRecord r = new GameRecord(id, difficulty, TIMESTAMP.format(endedAt), places, formatDuration(duration));
        if (!Files.exists(file) || Files.size(file) == 0) Files.writeString(file, HEADER + "\n", StandardCharsets.UTF_8);
        List<String> f = new ArrayList<>(List.of(String.valueOf(id), difficulty.csvName(), r.timestamp()));
        for (int p = 1; p <= 4; p++) f.add(r.place(p));
        f.add(r.duration());
        Files.writeString(file, CsvUtil.join(f) + "\n", StandardCharsets.UTF_8, StandardOpenOption.APPEND);
        return r;
    }

    /**
     * @param d a game time
     * @return the time as HH:MM:SS
     */
    public static String formatDuration(Duration d) {
        long s = d.getSeconds();
        return String.format("%02d:%02d:%02d", s / 3600, (s % 3600) / 60, s % 60);
    }
}
