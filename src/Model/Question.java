package Model;

import java.util.List;

/** A multiple-choice knowledge question with four answers (SRS Feature 4 and 6). */
public class Question {
    private final int id;
    private final Difficulty difficulty;
    private final String topic;
    private final String text;
    private final List<String> answers;
    private final int correctAnswer; // 1-4

    /**
     * @param id            unique number
     * @param difficulty    easy, medium or hard
     * @param topic         course topic
     * @param text          the question
     * @param answers       exactly four answers
     * @param correctAnswer number of the right answer, 1 to 4
     */
    public Question(int id, Difficulty difficulty, String topic, String text, List<String> answers, int correctAnswer) {
        if (text == null || text.isBlank()) throw new IllegalArgumentException("Question text is required");
        if (answers == null || answers.size() != 4) throw new IllegalArgumentException("A question needs exactly 4 answers");
        if (answers.stream().anyMatch(a -> a == null || a.isBlank())) throw new IllegalArgumentException("Answers cannot be empty");
        if (correctAnswer < 1 || correctAnswer > 4) throw new IllegalArgumentException("Correct answer must be 1-4");
        this.id = id;
        this.difficulty = difficulty;
        this.topic = topic;
        this.text = text;
        this.answers = List.copyOf(answers);
        this.correctAnswer = correctAnswer;
    }

    /** @return the question's number */
    public int getId() { return id; }
    /** @return easy, medium or hard */
    public Difficulty getDifficulty() { return difficulty; }
    /** @return the course topic */
    public String getTopic() { return topic; }
    /** @return the question text */
    public String getText() { return text; }
    /** @return the four answers, A to D */
    public List<String> getAnswers() { return answers; }
    /** @return number of the right answer, 1 to 4 */
    public int getCorrectAnswer() { return correctAnswer; }
    /**
     * @param answer the chosen answer, 1 to 4
     * @return true if it is the right one
     */
    public boolean isCorrect(int answer) { return answer == correctAnswer; }
}
