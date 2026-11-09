package Model;

import java.util.Random;

/**
 * Dice for each difficulty (SRS Feature 3):
 * Easy uses the Normal die, Medium adds a Boost die (1-3), Hard adds an UltraBoost die (1-6).
 */
public class Dice {
    private final Random random;

    public Dice(Random random) { this.random = random; }

    public DiceResult roll(Difficulty difficulty, boolean forceUltraBoost) {
        int normal = 1 + random.nextInt(6);
        int multiplier;
        if (forceUltraBoost || difficulty == Difficulty.HARD) multiplier = 1 + random.nextInt(6);
        else if (difficulty == Difficulty.MEDIUM) multiplier = 1 + random.nextInt(3);
        else multiplier = 1;
        return new DiceResult(normal, multiplier);
    }
}
