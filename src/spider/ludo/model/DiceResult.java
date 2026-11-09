package spider.ludo.model;

/** Result of one roll: the Normal die value and the multiplier from a Boost/UltraBoost die (1 when none). */
public record DiceResult(int normal, int multiplier) {
    public int total() { return normal * multiplier; }
    public boolean isSix() { return normal == 6; }

    @Override
    public String toString() { return multiplier > 1 ? normal + " x " + multiplier : String.valueOf(normal); }
}
