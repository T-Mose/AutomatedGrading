// REPOBEE-SANITIZER-SHRED

import org.junit.Test;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;

public class BiasedDiceTest {
    // Since the dice are random, we perform the tests these many times.
    static final int NUM_ROLLS = 1000;

    @Test
    public void biasedDiceRollsBetweenOneAndSix() {
        for (int i = 0; i < NUM_ROLLS; i++) {
            BiasedDice dice = new BiasedDice();
            assertTrue(dice.getValue() >= 1);
            assertTrue(dice.getValue() <= 6);
        }
    }

    @Test
    public void biasedDiceRollsBiasedly() {
        int[] counts = new int[6];
        for (int i = 0; i < 6 * NUM_ROLLS; i++) {
            BiasedDice dice = new BiasedDice();
            counts[dice.getValue() - 1]++;
        }
        // Give arbitrary margin of error.
        // We expect half of the rolls to be 6, and the other half of the rolls to be 1 .. 5.
        assertTrue(Math.abs(counts[5] - 6 * NUM_ROLLS / 2) < 200);
        for (int i = 0; i < 5; i++) {
            assertTrue(Math.abs(counts[i] - NUM_ROLLS / 2) < 200);
        }
    }

    @Test
    public void getValueReturnsValue() {
        for (int i = 0; i < NUM_ROLLS; i++) {
            BiasedDice dice = new BiasedDice();
            assertEquals(dice.getValue(), dice.value);
        }
    }

    @Test
    public void toStringGivesCorrectString() {
        for (int i = 0; i < NUM_ROLLS; i++) {
            BiasedDice dice = new BiasedDice();
            assertEquals(Integer.toString(dice.getValue()), dice.toString());
        }
    }
}