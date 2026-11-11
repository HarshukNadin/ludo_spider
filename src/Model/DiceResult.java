package Model;

/**
 * Result of one roll.
 *
 * @param normal     the Normal die, 1 to 6
 * @param multiplier the Boost or UltraBoost die, or 1 when the difficulty has no extra die
 */
public record DiceResult(int normal, int multiplier) {
    /** @return how many squares a piece already on the board moves */
    public int total() { return normal * multiplier; }

    /** @return true when the Normal die shows 6, which lets a piece leave the castle and gives another roll */
    public boolean isSix() { return normal == 6; }

    @Override
    public String toString() { return multiplier > 1 ? normal + " x " + multiplier : String.valueOf(normal); }
}
