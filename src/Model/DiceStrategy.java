package Model;

import java.util.Random;

/**
 * Strategy pattern: one way of rolling the dice. Each level uses its own strategy
 * (FR-3.02, FR-3.03), and {@link Dice} only asks the current strategy for a roll,
 * so it has no checks of the level inside it.
 */
public interface DiceStrategy {

    /**
     * Rolls the Normal die and, if this strategy has one, the extra die.
     *
     * @param random source of randomness
     * @return the Normal die value and the multiplier (1 when there is no extra die)
     */
    DiceResult roll(Random random);

    /** @return a short name for the screen, for example "Boost die (1-3)" */
    String name();
}
