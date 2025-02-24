// REPOBEE-SANITIZER-SHRED

import org.junit.Test;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;

public class DiceTest {
    // Since the dice are random, we perform the tests these many times.
    static final int NUM_ROLLS = 1000;

    @Test
    public void diceRollsBetweenOneAndSix() {
        for (int i = 0; i < NUM_ROLLS; i++) {
            Dice dice = new Dice();
            assertTrue(dice.getValue() >= 1);
            assertTrue(dice.getValue() <= 6);
        }
    }

    @Test
    public void diceRollsRandomly() {
        int[] counts = new int[6];
        for (int i = 0; i < 6 * NUM_ROLLS; i++) {
            Dice dice = new Dice();
            counts[dice.getValue() - 1]++;
        }
        for (int i = 0; i < 6; i++) {
            // Give arbitrary margin of error.
            assertTrue(Math.abs(counts[i] - NUM_ROLLS) < 200);
        }
    }

    @Test
    public void getValueReturnsValue() {
        for (int i = 0; i < NUM_ROLLS; i++) {
            Dice dice = new Dice();
            assertEquals(dice.getValue(), dice.value);
        }
    }

    @Test
    public void toStringGivesCorrectString() {
        for (int i = 0; i < NUM_ROLLS; i++) {
            Dice dice = new Dice();
            assertEquals(Integer.toString(dice.getValue()), dice.toString());
        }
    }
}