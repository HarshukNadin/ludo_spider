package Model;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
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
     * Reads all saved games. A missing or empty file means no games yet.
     * A file with another header, or a line that cannot be read, is an error, so the screen
     * never shows half a history as if it were complete (FR-5.09).
     *
     * @return the records in file order
     * @throws IOException if the file cannot be read, is not a history file, or a line is damaged
     */
    public List<GameRecord> load() throws IOException {
        List<GameRecord> out = new ArrayList<>();
        if (!Files.exists(file)) return out;
        List<String> lines = Files.readAllLines(file, StandardCharsets.UTF_8);
        if (lines.isEmpty()) return out;
        String header = lines.get(0).replace("\uFEFF", "").trim();
        if (!header.equals(HEADER)) throw new IOException("The history file does not start with the course header, so it was not read");
        for (int i = 1; i < lines.size(); i++) {
            if (lines.get(i).isBlank()) continue;
            try {
                List<String> f = CsvUtil.parseLine(lines.get(i));
                if (f.size() < 8) throw new IllegalArgumentException("too few fields");
                List<String> places = new ArrayList<>();
                for (int p = 3; p <= 6; p++) if (!f.get(p).isBlank()) places.add(f.get(p).trim());
                out.add(new GameRecord(Integer.parseInt(f.get(0).trim()), Difficulty.fromCsv(f.get(1)), f.get(2).trim(), places, f.get(7).trim()));
            } catch (RuntimeException e) {
                throw new IOException("Line " + (i + 1) + " of the history file is damaged");
            }
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
        List<String> f = new ArrayList<>(List.of(String.valueOf(id), difficulty.csvName(), r.timestamp()));
        for (int p = 1; p <= 4; p++) f.add(r.place(p));
        f.add(r.duration());

        String old = Files.exists(file) ? Files.readString(file, StandardCharsets.UTF_8) : "";
        StringBuilder text = new StringBuilder(old);
        if (old.isBlank()) text.setLength(0);
        if (text.length() == 0) text.append(HEADER).append('\n');
        // The course sample file has no line break at the end; without one the new game would join the last line.
        else if (text.charAt(text.length() - 1) != '\n') text.append('\n');
        text.append(CsvUtil.join(f)).append('\n');
        writeSafely(text.toString());
        return r;
    }

    /**
     * Writes the whole file to a temporary file next to it and then renames it, so a crash while
     * writing never leaves half a history file (risk R-08, REL-2).
     */
    private void writeSafely(String text) throws IOException {
        Path dir = file.toAbsolutePath().getParent();
        Path tmp = Files.createTempFile(dir, "game_history", ".tmp");
        try {
            Files.writeString(tmp, text, StandardCharsets.UTF_8);
            try {
                Files.move(tmp, file, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
            } catch (AtomicMoveNotSupportedException e) {
                Files.move(tmp, file, StandardCopyOption.REPLACE_EXISTING);
            }
        } finally {
            Files.deleteIfExists(tmp);
        }
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
