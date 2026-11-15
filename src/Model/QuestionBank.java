package Model;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.Set;

/** The question bank, stored in a CSV file (data/questions.csv). */
public class QuestionBank {
    public static final String HEADER = "id,difficulty,topic,question,answer1,answer2,answer3,answer4,correct";

    private final Path file;
    private final List<Question> questions = new ArrayList<>();
    private final Set<Integer> usedThisGame = new HashSet<>();

    /** @param file the questions.csv file */
    public QuestionBank(Path file) { this.file = file; }

    /**
     * Reads all questions from the file; a missing file gives an empty bank.
     *
     * @throws IOException if the file cannot be read
     */
    public void load() throws IOException {
        questions.clear();
        if (!Files.exists(file)) return;
        List<String> lines = Files.readAllLines(file, StandardCharsets.UTF_8);
        for (int i = 1; i < lines.size(); i++) {
            if (lines.get(i).isBlank()) continue;
            List<String> f = CsvUtil.parseLine(lines.get(i));
            questions.add(new Question(Integer.parseInt(f.get(0).trim()), Difficulty.fromCsv(f.get(1)), f.get(2), f.get(3),
                    List.of(f.get(4), f.get(5), f.get(6), f.get(7)), Integer.parseInt(f.get(8).trim())));
        }
    }

    /**
     * Writes all questions back to the file.
     *
     * @throws IOException if the file cannot be written
     */
    public void save() throws IOException {
        List<String> lines = new ArrayList<>();
        lines.add(HEADER);
        for (Question q : questions) {
            List<String> f = new ArrayList<>(List.of(String.valueOf(q.getId()), q.getDifficulty().name(), q.getTopic(), q.getText()));
            f.addAll(q.getAnswers());
            f.add(String.valueOf(q.getCorrectAnswer()));
            lines.add(CsvUtil.join(f));
        }
        Files.write(file, lines, StandardCharsets.UTF_8);
    }

    /** @return every question, read only */
    public List<Question> getAll() { return Collections.unmodifiableList(questions); }

    /** Picks a random question of the given level, preferring ones not yet asked in this game. */
    public Optional<Question> randomQuestion(Difficulty level, Random random) {
        List<Question> pool = questions.stream().filter(q -> q.getDifficulty() == level && !usedThisGame.contains(q.getId())).toList();
        if (pool.isEmpty()) pool = questions.stream().filter(q -> q.getDifficulty() == level).toList();
        if (pool.isEmpty()) return Optional.empty();
        Question q = pool.get(random.nextInt(pool.size()));
        usedThisGame.add(q.getId());
        return Optional.of(q);
    }

    /** Forgets which questions were asked, for a new game (FR-4.07). */
    public void resetUsed() { usedThisGame.clear(); }

    // Admin operations (SRS Feature 6). Access control is handled by the controller.
    /** @return a free id for a new question */
    public int nextId() { return questions.stream().mapToInt(Question::getId).max().orElse(0) + 1; }
    /** @param q a new question */
    public void add(Question q) { questions.add(q); }
    /**
     * Replaces the question that has the same id.
     *
     * @param q the changed question
     * @throws IllegalArgumentException if no question has that id
     */
    public void update(Question q) {
        for (int i = 0; i < questions.size(); i++) if (questions.get(i).getId() == q.getId()) { questions.set(i, q); return; }
        throw new IllegalArgumentException("No question with id " + q.getId());
    }
    /**
     * @param id the question to remove
     * @return true if a question was removed
     */
    public boolean delete(int id) { return questions.removeIf(q -> q.getId() == id); }
}
