package Model;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Random;
import org.junit.jupiter.api.Test;

/** The dice strategies of each level (FR-3.02, FR-3.03, CR-07). */
class DiceStrategyTest {
    private static final int ROLLS = 30_000;

    private static int[] multipliers(DiceStrategy s, int faces) {
        Random random = new Random(11);
        int[] seen = new int[faces + 1];
        for (int i = 0; i < ROLLS; i++) {
            DiceResult r = s.roll(random);
            assertTrue(r.normal() >= 1 && r.normal() <= 6, "normal die " + r.normal());
            assertTrue(r.multiplier() >= 1 && r.multiplier() <= faces, s.name() + " gave " + r.multiplier());
            seen[r.multiplier()]++;
        }
        return seen;
    }

    @Test
    void normalDiceNeverMultiplies() {
        assertEquals(ROLLS, multipliers(new NormalDice(), 1)[1]);
    }

    @Test
    void boostDieGivesOneToThreeEquallyOften() {
        int[] seen = multipliers(new BoostDice(), BoostDice.FACES);
        for (int v = 1; v <= 3; v++) assertTrue(Math.abs(seen[v] - ROLLS / 3) < ROLLS / 3 * 0.05, v + " came up " + seen[v]);
    }

    @Test
    void ultraBoostDieGivesOneToSixEquallyOften() {
        int[] seen = multipliers(new UltraBoostDice(), UltraBoostDice.FACES);
        for (int v = 1; v <= 6; v++) assertTrue(Math.abs(seen[v] - ROLLS / 6) < ROLLS / 6 * 0.06, v + " came up " + seen[v]);
    }

    @Test
    void eachLevelHasItsOwnStrategy() {
        assertInstanceOf(NormalDice.class, Dice.strategyFor(Difficulty.EASY));
        assertInstanceOf(BoostDice.class, Dice.strategyFor(Difficulty.MEDIUM));
        assertInstanceOf(UltraBoostDice.class, Dice.strategyFor(Difficulty.HARD));
    }

    @Test
    void surpriseGivesUltraBoostEvenOnEasy() {
        Dice dice = new Dice(new Random(5));
        boolean multiplied = false;
        for (int i = 0; i < 200; i++) multiplied |= dice.roll(Difficulty.EASY, true).multiplier() > 1;
        assertTrue(multiplied, "the UltraBoost die was used");
    }

    @Test
    void gameRollsWithTheStrategyOfItsLevel() {
        Game g = new Game(Difficulty.MEDIUM, List.of(new Player("Dana", PlayerColor.RED), new Player("Omar", PlayerColor.BLUE)), null, new Random(4));
        assertInstanceOf(BoostDice.class, g.getDiceStrategy());
        g.roll();
        assertTrue(g.getLastRoll().multiplier() <= 3);
    }
}
