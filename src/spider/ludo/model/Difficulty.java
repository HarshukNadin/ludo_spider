package spider.ludo.model;

/** The three difficulty levels from the SRS (Feature 2). */
public enum Difficulty {
    EASY(1, 4, 0, 0),
    MEDIUM(2, 8, 6, 2),
    HARD(3, 12, 9, 3);

    private final int questionSteps;
    private final int protectionSquares;
    private final int questionSquares;
    private final int surpriseSquares;

    Difficulty(int questionSteps, int protectionSquares, int questionSquares, int surpriseSquares) {
        this.questionSteps = questionSteps;
        this.protectionSquares = protectionSquares;
        this.questionSquares = questionSquares;
        this.surpriseSquares = surpriseSquares;
    }

    /** Steps forward on a right answer, or back on a wrong one. */
    public int getQuestionSteps() { return questionSteps; }
    public int getProtectionSquares() { return protectionSquares; }
    public int getQuestionSquares() { return questionSquares; }
    public int getSurpriseSquares() { return surpriseSquares; }

    /** Lower-case name, as written in the course CSV ("easy", "medium", "hard"). */
    public String csvName() { return name().toLowerCase(); }

    public static Difficulty fromCsv(String s) { return valueOf(s.trim().toUpperCase()); }
}
