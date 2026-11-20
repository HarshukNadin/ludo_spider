package Model;

import java.util.Random;

/**
 * The dice of a game (SRS Feature 3). The way of rolling is a {@link DiceStrategy}:
 * Easy uses {@link NormalDice}, Medium {@link BoostDice} and Hard {@link UltraBoostDice}.
 */
public class Dice {
    /** Faces of the Normal die (FR-3.02). */
    public static final int NORMAL_FACES = 6;

    private final Random random;
    private final DiceStrategy ultraBoost = new UltraBoostDice();

    /**
     * @param random source of randomness; tests pass a seeded one to get the same rolls every time
     */
    public Dice(Random random) { this.random = random; }

    /**
     * @param difficulty a level
     * @return the dice strategy of that level
     */
    public static DiceStrategy strategyFor(Difficulty difficulty) {
        switch (difficulty) {
            case MEDIUM: return new BoostDice();
            case HARD: return new UltraBoostDice();
            default: return new NormalDice();
        }
    }

    /**
     * Rolls the dice of a level.
     * Every face has the same chance because each value comes from {@link Random#nextInt(int)}.
     *
     * @param difficulty      decides which strategy is used
     * @param forceUltraBoost true when a surprise gives the player an UltraBoost roll on any difficulty
     * @return the Normal die value (1-6) and the multiplier (1 when there is no extra die)
     */
    public DiceResult roll(Difficulty difficulty, boolean forceUltraBoost) {
        return roll(forceUltraBoost ? ultraBoost : strategyFor(difficulty));
    }

    /**
     * Rolls with a given strategy.
     *
     * @param strategy how to roll
     * @return the roll
     */
    public DiceResult roll(DiceStrategy strategy) {
        return strategy.roll(random);
    }
}
