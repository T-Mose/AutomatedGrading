// REPOBEE-SANITIZER-SHRED

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.lang.reflect.Field;

import org.junit.Test;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;

public class RandomTesterTest {
    @Test
    public void generateTenNumbersGivesCorrectAmountOfNumbers() {
        ArrayList<Integer> numbers = RandomTester.generateNumbers(10);
        assertEquals(10, numbers.size());
    }

    @Test
    public void generateZeroNumbersGivesCorrectAmountOfNumbers() {
        ArrayList<Integer> numbers = RandomTester.generateNumbers(0);
        assertEquals(0, numbers.size());
    }

    @Test
    public void generateNumbersGivesRandomNumbers() {
        // A quite crude randomness test. We generate NUM_METHOD_CALLS * NUM_NUMBERS_PER CALL
        // numbers, and then check that no number occurs more than MAX_ALLOWED_OCCURRENCES
        // times. This is not a very good test, but it is better than nothing.
        final int NUM_METHOD_CALLS = 1000;
        final int NUM_NUMBERS_PER_CALL = 1000;
        final int MAX_ALLOWED_OCCURRENCES = 10;
        final Map<Integer, Integer> numberCounts = new HashMap<>();
        for (int i = 0; i < NUM_METHOD_CALLS; i++) {
            ArrayList<Integer> numbers = RandomTester.generateNumbers(NUM_NUMBERS_PER_CALL);
            for (int j = 0; j < numbers.size(); j++) {
                numberCounts.put(numbers.get(j), numberCounts.getOrDefault(numbers.get(j), 0) + 1);
            }
        }
        for (Integer k : numberCounts.keySet()) {
            assertTrue(numberCounts.get(k) < MAX_ALLOWED_OCCURRENCES);
        }
    }

    @Test
    public void shuffleTenNumbersGivesCorrectAmountOfNumbers() {
        final int NUM_NUMBERS = 10;
        ArrayList<Integer> numbers = new ArrayList<>();
        for (int i = 0; i < NUM_NUMBERS; i++) {
            numbers.add(i);
        }
        ArrayList<Integer> shuffled = RandomTester.shuffle(numbers);
        assertEquals(10, shuffled.size());
    }

    @Test
    public void shuffleTenNumbersLeavesArgumentUntouched() {
        final int NUM_NUMBERS = 10;
        ArrayList<Integer> numbers = new ArrayList<>();
        for (int i = 0; i < NUM_NUMBERS; i++) {
            numbers.add(i);
        }
        RandomTester.shuffle(numbers);
        for (int i = 0; i < NUM_NUMBERS; i++) {
            assertTrue(i == numbers.get(i));
        }
    }

    @Test
    public void shuffleTenNumbersGivesSameNumbers() {
        final int NUM_NUMBERS = 10;
        ArrayList<Integer> numbers = new ArrayList<>();
        for (int i = 0; i < NUM_NUMBERS; i++) {
            numbers.add(i);
        }
        ArrayList<Integer> shuffled = RandomTester.shuffle(numbers);
        for (int i = 0; i < NUM_NUMBERS; i++) {
            assertTrue(shuffled.contains(i));
        }
    }

    @Test
    public void shuffleListWithDuplicatesGivesSameNumbers() {
        final int NUM_NUMBERS = 10;
        ArrayList<Integer> numbers = new ArrayList<>();
        for (int i = 0; i < NUM_NUMBERS; i++) {
            numbers.add(i);
            numbers.add(i);
        }
        ArrayList<Integer> shuffled = RandomTester.shuffle(numbers);
        Collections.sort(shuffled);

        for (int i = 0; i < 10; i++) {
            assertTrue(shuffled.get(i*2) == i);
            assertTrue(shuffled.get(i*2 + 1) == i);
        }
    }

    @Test
    public void sequenceOfDiceGeneratesUniqueDice() {
        ArrayList<Dice> dice = RandomTester.sequenceOfDice(10);
        for (int i = 0; i < dice.size(); i++) {
            for (int j = 0; j < dice.size(); j++) {
                if (i == j) {
                    continue;
                }
                assertTrue(dice.get(i) != dice.get(j));
            }
        }
    }

    @Test
    public void sequenceOfDiceGeneratesCorrectAmountOfDice() {
        ArrayList<Dice> dice = RandomTester.sequenceOfDice(10);
        assertEquals(10, dice.size());
    }

    @Test
    public void highestAdjacentRollsGivesHighestAdjacentRolls() {
        ArrayList<Dice> dice = new ArrayList<>();
        dice.add(new FakeDice(1));
        dice.add(new FakeDice(2));
        dice.add(new FakeDice(3));
        assertEquals(5, RandomTester.highestAdjacentRolls(dice));
    }

    @Test
    public void smallestAdjacentRollsGivesSmallestAdjacentRolls() {
        ArrayList<Dice> dice = new ArrayList<>();
        dice.add(new FakeDice(1));
        dice.add(new FakeDice(2));
        dice.add(new FakeDice(3));
        assertEquals(3, RandomTester.smallestAdjacentRolls(dice));
    }

    @Test
    public void removeRemovesCorrectElement() {
        final int NUM_DICE = 10;
        ArrayList<Dice> dice = new ArrayList<>();
        for (int i = 0; i < NUM_DICE; i++) {
            dice.add(new FakeDice(i));
        }
        ArrayList<Dice> removed = RandomTester.remove(dice, 5);
        assertTrue(!removed.contains(5));
    }

    @Test
    public void removeRemovesOnlyCorrectElement() {
        final int NUM_DICE = 1000;
        final int REMOVED = 5;
        ArrayList<Dice> dice = new ArrayList<>();
        dice.add(new FakeDice(1));
        dice.add(new FakeDice(2));
        dice.add(new FakeDice(3));
        dice.add(new FakeDice(4));
        dice.add(new FakeDice(5));
        dice.add(new FakeDice(6));

        ArrayList<Dice> removed = RandomTester.remove(dice, REMOVED);
        System.out.println(removed);
        assertTrue(removed.get(0).getValue() == 1);
        assertTrue(removed.get(1).getValue() == 2);
        assertTrue(removed.get(2).getValue() == 3);
        assertTrue(removed.get(3).getValue() == 4);
        assertTrue(removed.get(4).getValue() == 6);
    }

    class FakeDice extends Dice {
        public FakeDice(int value) {
            // Try to set value field - works with both public/protected field and private field
            try {
                Field field = Dice.class.getField("value");
                field.setInt(this, value);
            } catch (NoSuchFieldException | IllegalAccessException e) {
                // If field is not public/protected, try to access it as private/protected field
                try {
                    Field field = Dice.class.getDeclaredField("value");
                    field.setAccessible(true);
                    field.setInt(this, value);
                } catch (NoSuchFieldException | IllegalAccessException ex) {
                    throw new RuntimeException("Could not set value field on Dice", ex);
                }
            }
        }
    }
}