// REPOBEE-SANITIZER-SHRED

import org.junit.Test;

import java.lang.reflect.Field;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;

public class DiceTest {
    // Helper method to get value field - works with both public/protected field and private field + getter
    private int getValueField(Dice dice) {
        try {
            // First try to access the field directly (handles public/protected field case)
            Field field = dice.getClass().getField("value");
            return field.getInt(dice);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            // If field is not public/protected, use getValue() method (private field + getter case)
            return dice.getValue();
        }
    }
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
            assertEquals(dice.getValue(), getValueField(dice));
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