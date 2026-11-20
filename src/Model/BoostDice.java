package Model;

import java.util.Random;

/** Medium: the Normal die and a Boost die of 1 to 3; pieces on shared routes move Normal x Boost (FR-3.03). */
public class BoostDice implements DiceStrategy {
    /** Faces of the Boost die. */
    public static final int FACES = 3;

    /** @return a Normal die value and a Boost value of 1 to 3 */
    @Override
    public DiceResult roll(Random random) {
        return new DiceResult(1 + random.nextInt(Dice.NORMAL_FACES), 1 + random.nextInt(FACES));
    }

    @Override
    public String name() { return "Boost die (1-3)"; }
}
