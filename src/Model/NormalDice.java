package Model;

import java.util.Random;

/** Easy: the Normal die only, 1 to 6 with the same chance each (FR-3.02). */
public class NormalDice implements DiceStrategy {

    /** @return a 1 to 6 Normal die value with multiplier 1 */
    @Override
    public DiceResult roll(Random random) {
        return new DiceResult(1 + random.nextInt(Dice.NORMAL_FACES), 1);
    }

    @Override
    public String name() { return "Normal die"; }
}
