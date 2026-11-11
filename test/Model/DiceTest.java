package Model;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Random;
import org.junit.jupiter.api.Test;

class DiceTest {
    private static final int ROLLS = 60_000;

    @Test
    void normalDieGivesEveryValueFromOneToSixAboutEquallyOften() {
        Dice dice = new Dice(new Random(42));
        int[] seen = new int[7];
        for (int i = 0; i < ROLLS; i++) {
            int v = dice.roll(Difficulty.EASY, false).normal();
            assertTrue(v >= 1 && v <= 6, "rolled " + v);
            seen[v]++;
        }
        int expected = ROLLS / 6;
        for (int v = 1; v <= 6; v++)
            assertTrue(Math.abs(seen[v] - expected) < expected * 0.05, v + " came up " + seen[v] + " times, expected about " + expected);
    }

    @Test
    void easyUsesOnlyTheNormalDie() {
        Dice dice = new Dice(new Random(7));
        for (int i = 0; i < 1000; i++) {
            DiceResult r = dice.roll(Difficulty.EASY, false);
            assertEquals(1, r.multiplier());
            assertEquals(r.normal(), r.total());
        }
    }

    @Test
    void onlyANormalSixCountsAsSix() {
        assertTrue(new DiceResult(6, 1).isSix());
        assertFalse(new DiceResult(3, 2).isSix(), "3 x 2 moves 6 squares but is not a 6");
        assertEquals("3 x 2", new DiceResult(3, 2).toString());
        assertEquals("5", new DiceResult(5, 1).toString());
    }
}
