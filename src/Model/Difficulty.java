package Model;

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
    /** @return number of Protection squares on this level (FR-2.02 to FR-2.04) */
    public int getProtectionSquares() { return protectionSquares; }
    /** @return number of Question squares on this level */
    public int getQuestionSquares() { return questionSquares; }
    /** @return number of Surprise squares on this level */
    public int getSurpriseSquares() { return surpriseSquares; }

    /** Lower-case name, as written in the course CSV ("easy", "medium", "hard"). */
    public String csvName() { return name().toLowerCase(); }

    /**
     * @param s easy, medium or hard, in any case
     * @return the matching level
     * @throws IllegalArgumentException if the text is not a level
     */
    public static Difficulty fromCsv(String s) { return valueOf(s.trim().toUpperCase()); }
}
