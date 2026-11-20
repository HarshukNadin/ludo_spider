package Model;

import java.util.Random;

/**
 * Hard: the Normal die and an UltraBoost die of 1 to 6 (FR-3.03).
 * A Surprise square can also give one UltraBoost roll on any level (CR-07).
 */
public class UltraBoostDice implements DiceStrategy {
    /** Faces of the UltraBoost die. */
    public static final int FACES = 6;

    /** @return a Normal die value and an UltraBoost value of 1 to 6 */
    @Override
    public DiceResult roll(Random random) {
        return new DiceResult(1 + random.nextInt(Dice.NORMAL_FACES), 1 + random.nextInt(FACES));
    }

    @Override
    public String name() { return "UltraBoost die (1-6)"; }
}
