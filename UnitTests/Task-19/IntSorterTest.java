import org.junit.Test;
import org.junit.Before;
import static org.junit.Assert.*;

import java.util.Arrays;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.CoreMatchers.*;
import org.junit.Ignore;


/**
 * Abstract test class for IntSorter implementations.
 *
 * Implementing test classes must override the getIntSorter method.
 */
@Ignore
public abstract class IntSorterTest {
    protected IntSorter sorter;

    /**
     * Returns an implementation of the IntSorter interface. Extending classes
     * must override this method.
     *
     * @return An implementation of IntSorter.
     */
    protected abstract IntSorter getIntSorter();

    @Before
    public void setUp() {
        sorter = getIntSorter(); // Initialize the sorter before each test
    }

    // Example test case
    @Test
    public void testSortEmptyArray() {
        int[] array = {};
        sorter.sort(array);
        assertThat("Sorting an empty array should not change it", array, is(new int[] {}));
    }

    @Test
    public void testEmptyArray() {
        int[] array = {};
        sorter.sort(array);
        assertArrayEquals(new int[] {}, array);
    }

    @Test
    public void testSingleElementArray() {
        int[] array = { 1 };
        sorter.sort(array);
        assertArrayEquals(new int[] { 1 }, array);
    }

    @Test
    public void testSortedArray() {
        int[] array = { 1, 2, 3, 4, 5 };
        sorter.sort(array);
        assertArrayEquals(new int[] { 1, 2, 3, 4, 5 }, array);
    }

    @Test
    public void testReverseSortedArray() {
        int[] array = { 5, 4, 3, 2, 1 };
        sorter.sort(array);
        assertArrayEquals(new int[] { 1, 2, 3, 4, 5 }, array);
    }

    @Test
    public void testDuplicatesArray() {
        int[] array = { 1, 3, 2, 1, 3 };
        sorter.sort(array);
        assertArrayEquals(new int[] { 1, 1, 2, 3, 3 }, array);
    }

    @Test
    public void testLargeArray() {
        int size = 1000;
        int[] array = new int[size];
        for (int i = 0; i < size; i++) {
            array[i] = size - i;
        }
        sorter.sort(array);
        for (int i = 1; i <= size; i++) {
            assertEquals(i, array[i - 1]);
        }
    }

    @Test
    public void testWithNegativeNumbersAndZeroes() {
        int[] array = { -1, 3, 0, -5, 2, -3 };
        sorter.sort(array);
        assertArrayEquals(new int[] { -5, -3, -1, 0, 2, 3 }, array);
    }

    @Test
    public void testWithLargeNumbers() {
        int[] array = { Integer.MAX_VALUE, 1, -1, Integer.MIN_VALUE };
        sorter.sort(array);
        assertArrayEquals(new int[] { Integer.MIN_VALUE, -1, 1, Integer.MAX_VALUE }, array);
    }

    @Test
    public void testWithConsecutiveDuplicates() {
        int[] array = { 2, 2, 1, 1, 3, 3 };
        sorter.sort(array);
        assertArrayEquals(new int[] { 1, 1, 2, 2, 3, 3 }, array);
    }

    @Test(expected = NullPointerException.class)
    public void testWithNullArray() {
        sorter.sort(null);
    }

    @Test
    public void testWithRepeatedValues() {
        int[] array = new int[100];
        Arrays.fill(array, 1);
        sorter.sort(array);
        int[] expectedArray = new int[100];
        Arrays.fill(expectedArray, 1);
        assertArrayEquals("Array should remain unchanged when all elements are equal", expectedArray, array);
    }
    

}