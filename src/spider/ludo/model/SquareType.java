package spider.ludo.model;

/** Kinds of squares on the board (SRS Feature 2 and 4). */
public enum SquareType {
    NORMAL, START, PROTECTION, QUESTION, SURPRISE;

    /** Pieces on these squares cannot be captured. */
    public boolean isSafe() { return this == START || this == PROTECTION || this == SURPRISE; }
}
