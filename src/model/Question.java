package model;

import java.util.List;

/** A multiple-choice knowledge question with four answers (SRS Feature 4 and 6). */
public class Question {
    private final int id;
    private final Difficulty difficulty;
    private final String topic;
    private final String text;
    private final List<String> answers;
    private final int correctAnswer; // 1-4

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

    public int getId() { return id; }
    public Difficulty getDifficulty() { return difficulty; }
    public String getTopic() { return topic; }
    public String getText() { return text; }
    public List<String> getAnswers() { return answers; }
    public int getCorrectAnswer() { return correctAnswer; }
    public boolean isCorrect(int answer) { return answer == correctAnswer; }
}
