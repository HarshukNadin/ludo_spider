package Model;

import java.util.Random;

/**
 * Factory pattern: the one place that decides which board class a level uses (FR-2.01).
 * Easy gets a {@link Board} with the standard track only; Medium and Hard get an
 * {@link OuterBoard} with one or two outer routes. The game asks the factory for a board
 * and does not need to know which class it gets, so a new kind of board only changes this class.
 */
public final class BoardFactory {

    private BoardFactory() { }

    /**
     * Builds a new board for a level, with its special squares already placed.
     *
     * @param difficulty the level chosen in setup
     * @param random     used to place the special squares
     * @return a {@link Board} for Easy, an {@link OuterBoard} for Medium and Hard
     */
    public static Board createBoard(Difficulty difficulty, Random random) {
        switch (difficulty) {
            case EASY: return new Board(difficulty, random);
            case MEDIUM:
            case HARD: return new OuterBoard(difficulty, random);
            default: throw new IllegalArgumentException("Unknown level " + difficulty);
        }
    }
}
