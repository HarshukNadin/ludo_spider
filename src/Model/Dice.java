package Model;

import java.util.Random;

/**
 * Dice for each difficulty (SRS Feature 3):
 * Easy uses the Normal die, Medium adds a Boost die (1-3), Hard adds an UltraBoost die (1-6).
 */
public class Dice {
    /** Faces of the Normal die (FR-3.02). */
    public static final int NORMAL_FACES = 6;

    private final Random random;

    /**
     * @param random source of randomness; tests pass a seeded one to get the same rolls every time
     */
    public Dice(Random random) { this.random = random; }

    /**
     * Rolls the Normal die, plus the extra die of the difficulty.
     * Every face has the same chance because each value comes from {@link Random#nextInt(int)}.
     *
     * @param difficulty      decides which extra die is used, if any
     * @param forceUltraBoost true when a surprise gives the player an UltraBoost roll on any difficulty
     * @return the Normal die value (1-6) and the multiplier (1 when there is no extra die)
     */
    public DiceResult roll(Difficulty difficulty, boolean forceUltraBoost) {
        int normal = 1 + random.nextInt(NORMAL_FACES);
        int multiplier;
        if (forceUltraBoost || difficulty == Difficulty.HARD) multiplier = 1 + random.nextInt(6);
        else if (difficulty == Difficulty.MEDIUM) multiplier = 1 + random.nextInt(3);
        else multiplier = 1;
        return new DiceResult(normal, multiplier);
    }
}
