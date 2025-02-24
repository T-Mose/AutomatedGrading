// REPOBEE-SANITIZER-SHRED

import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

/**
 * Test cases for the Arrays class
 * NOTE: We do not require the students to handle edge cases such as
 * empty arrays, so these cases are not tested.
 *
 * @author Linus Östlund
 * @author Gabriel Skoglund
 */
public class ArraysTest {
    private final int[] intArrayWithNegativeNumbers = new int[] {0, -1, -2, -3, -4, -5};
    private final int[] intArrayWithPositiveNumbers = new int[] {1, 2, 3, 4, 5, 0};
    private final double[] doubleArrayWithNegativeNumbers = new double[] {0, -1, -2, -3, -4, -5};
    private final double[] doubleArrayWithPositiveNumbers = new double[] {0, 1, 2, 3, 4, 5};


    @Test
    public void intAveragePositiveNumbersGivesExpectedResult() {
        int expected = java.util.Arrays.stream(intArrayWithPositiveNumbers).sum() /
                       intArrayWithPositiveNumbers.length;
        assertEquals(expected, Arrays.average(intArrayWithPositiveNumbers));
    }

    @Test
    public void intAverageNegativeNumbersGivesExpectedResult() {
        int expected = java.util.Arrays.stream(intArrayWithNegativeNumbers).sum() /
                       intArrayWithNegativeNumbers.length;
        assertEquals(expected, Arrays.average(intArrayWithNegativeNumbers));
    }

    @Test
    public void doubleAveragePositiveNumbersGivesExpectedResult() {
        double expected = java.util.Arrays.stream(doubleArrayWithPositiveNumbers).sum() /
                          doubleArrayWithPositiveNumbers.length;
        assertEquals(expected, Arrays.average(doubleArrayWithPositiveNumbers), 0);
    }

    @Test
    public void doubleAverageNegativeNumbersGivesExpectedResult() {
        double expected = java.util.Arrays.stream(doubleArrayWithNegativeNumbers).sum() /
                          doubleArrayWithNegativeNumbers.length;
        assertEquals(expected, Arrays.average(doubleArrayWithNegativeNumbers), 0);
    }

    @Test
    public void smallestElementFindsSmallestInPositiveNumbers() {
        int expected = java.util.Arrays.stream(intArrayWithPositiveNumbers).min().orElse(0);
        assertEquals(expected, Arrays.smallestElement(intArrayWithPositiveNumbers));
    }

    @Test
    public void smallestElementFindsSmallestInNegativeNumbers() {
        int expected = java.util.Arrays.stream(intArrayWithNegativeNumbers).min().orElse(0);
        assertEquals(expected, Arrays.smallestElement(intArrayWithNegativeNumbers));
    }

    @Test
    public void reverseCorrectlyCreatesReversedCopy() {
        int[] reversed = Arrays.reverse(intArrayWithPositiveNumbers);
        assertEquals(intArrayWithPositiveNumbers.length, reversed.length);
        for (int i = 0; i < reversed.length; i++)
            assertEquals(intArrayWithPositiveNumbers[i], reversed[reversed.length - i - 1]);
    }

    @Test
    public void reverseDoesNotModifyOriginalArray() {
        int[] original = java.util.Arrays.copyOf(intArrayWithPositiveNumbers,
                                                 intArrayWithPositiveNumbers.length);
        Arrays.reverse(intArrayWithPositiveNumbers);
        assertArrayEquals(original, intArrayWithPositiveNumbers);
    }

    @Test
    public void evenNumbersGivesCorrectResultForPositiveNumbers() {
        int[] expected = java.util.Arrays.stream(intArrayWithPositiveNumbers)
                                         .filter(i -> i % 2 == 0)
                                         .toArray();
        assertArrayEquals(expected, Arrays.evenNumbers(intArrayWithPositiveNumbers));
    }

    @Test
    public void evenNumbersGivesCorrectResultForNegativeNumbers() {
        int[] expected = java.util.Arrays.stream(intArrayWithNegativeNumbers)
                                         .filter(i -> i % 2 == 0)
                                         .toArray();
        assertArrayEquals(expected, Arrays.evenNumbers(intArrayWithNegativeNumbers));
    }

    @Test
    public void evenNumbersDoesNotModifyOriginalArray() {
        int[] original = java.util.Arrays.copyOf(intArrayWithPositiveNumbers,
                                                 intArrayWithPositiveNumbers.length);
        Arrays.evenNumbers(intArrayWithPositiveNumbers);
        assertArrayEquals(original, intArrayWithPositiveNumbers);
    }
}