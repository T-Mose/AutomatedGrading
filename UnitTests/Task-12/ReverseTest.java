import org.junit.Test;
import org.junit.Before;
import static org.junit.Assert.*;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.CoreMatchers.*;

import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;
/**
 * Test class for Reverse.
 *
 * @author Simon Larsén
 * @author Tobias Hansson
 * @version 2017-08-08
 */
public class ReverseTest {
    // Arrange
    private int[] oddLengthArray;
    private int[] evenLengthArray;
    private List<Integer> oddLengthList;
    private List<Integer> evenLengthList;
    private int oddLengthSequenceLength;
    private int evenLengthSequenceLength;

    private Reverse reverse;

    @Before
    public void setUp() {
        // Arrange
        reverse = new Reverse();
        oddLengthArray = new int[]{1, 5, 23, -23, 3, 4, 1, -2, 3};
        evenLengthArray = new int[]{3, 123, 4, -2, -512, -3};
        oddLengthList = intArrayToList(oddLengthArray);
        evenLengthList = intArrayToList(evenLengthArray);
        oddLengthSequenceLength = oddLengthArray.length;
        evenLengthSequenceLength = evenLengthArray.length;
    }

    /**
     * Tests for reversed(int[])
     */

    @Test
    public void reversedArrayIsEmptyWhenOriginalIsEmpty() {
        // Arrange
        int[] emptyArray = new int[0];

        // Act
        int[] reversedEmptyArray = reverse.reversed(emptyArray);

        // Assert
        assertThat(reversedEmptyArray.length, equalTo(0));
    }

    @Test
    public void reversedArrayIsSameLengthAsOriginalWhenNonEmpty() {
        // Act
        int[] reversedOddArray = reverse.reversed(oddLengthArray);
        int[] reversedEvenArray = reverse.reversed(evenLengthArray);

        // Assert
        assertThat(reversedOddArray.length, equalTo(oddLengthSequenceLength));
        assertThat(reversedEvenArray.length, equalTo(evenLengthSequenceLength));
    }

    @Test
    public void reversedArrayDoesNotMutateOriginalWhenNonEmpty() {
        // Arrange
        int[] oddArrayCopy = Arrays.copyOf(oddLengthArray, oddLengthSequenceLength);
        int[] evenArrayCopy = Arrays.copyOf(evenLengthArray, evenLengthSequenceLength);

        // Act
        reverse.reversed(oddLengthArray);
        reverse.reversed(evenLengthArray);

        // Assert
        assertArrayEquals(oddArrayCopy, oddLengthArray);
        assertArrayEquals(evenArrayCopy, evenLengthArray);
    }

    @Test
    public void reversedArrayIsReverseOfOriginalWhenNonEmpty() {
        // Act
        int[] reversedOddArray = reverse.reversed(oddLengthArray);
        int[] reversedEvenArray = reverse.reversed(evenLengthArray);

        // Assert
        assertReversed(oddLengthArray, reversedOddArray);
        assertReversed(evenLengthArray, reversedEvenArray);
    }

    @Test
    public void reversedArrayIsEqualToOriginalWhenLengthIsOne() {
        // Arrange
        int[] singleElementArray = {1338};

        // Act
        int[] reversedSingleElementArray = reverse.reversed(singleElementArray);

        // Assert
        assertArrayEquals(singleElementArray, reversedSingleElementArray);
    }

    /**
     * Tests for reversed(List<Integer>)
     */

    @Test
    public void reversedListIsEmptyWhenOriginalIsEmpty() {
//REPOBEE-SANITIZER-START
        List<Integer> emptyList = new ArrayList<Integer>();
        List<Integer> reversedEmptyList = reverse.reversed(emptyList);
        assertThat(reversedEmptyList.size(), equalTo(0));
//REPOBEE-SANITIZER-REPLACE-WITH
//        fail("Not implemented");
//REPOBEE-SANITIZER-END     
    }

    @Test
    public void reversedListIsSameLengthAsOriginalWhenNonEmpty() {
//REPOBEE-SANITIZER-START
        List<Integer> reversedEvenList = reverse.reversed(evenLengthList);
        List<Integer> reversedOddList = reverse.reversed(oddLengthList);
        assertThat(reversedEvenList.size(), equalTo(evenLengthSequenceLength));
        assertThat(reversedOddList.size(), equalTo(oddLengthSequenceLength));
//REPOBEE-SANITIZER-REPLACE-WITH
//        fail("Not implemented");
//REPOBEE-SANITIZER-END
    }

    @Test
    public void reversedListDoesNotMutateOriginalWhenNonEmpty() {
//REPOBEE-SANITIZER-START
        List<Integer> evenListCopy = new ArrayList<Integer>(evenLengthList);
        List<Integer> oddListCopy = new ArrayList<Integer>(oddLengthList);

        reverse.reversed(oddLengthList);
        reverse.reversed(evenLengthList);

        assertThat(oddLengthList, equalTo(oddListCopy));
        assertThat(evenLengthList, equalTo(evenListCopy));
//REPOBEE-SANITIZER-REPLACE-WITH
//        fail("Not implemented");
//REPOBEE-SANITIZER-END
    }

    @Test
    public void reversedListIsReverseOfOriginalWhenNonEmpty() {
//REPOBEE-SANITIZER-START
        List<Integer> reversedEvenList = reverse.reversed(evenLengthList);
        List<Integer> reversedOddList = reverse.reversed(oddLengthList);
        assertReversed(evenLengthList, reversedEvenList);
        assertReversed(oddLengthList, reversedOddList);
//REPOBEE-SANITIZER-REPLACE-WITH
//        fail("Not implemented");
//REPOBEE-SANITIZER-END
    }

    @Test
    public void reversedListIsEqualToOriginalWhenLengthIsOne() {
//REPOBEE-SANITIZER-START
        List<Integer> singleElementList = Arrays.asList(43);
        List<Integer> reversedSingleElementList = reverse.reversed(singleElementList);
        assertThat(reversedSingleElementList, equalTo(singleElementList));
//REPOBEE-SANITIZER-REPLACE-WITH
//        fail("Not implemented");
//REPOBEE-SANITIZER-END
    }

    /**
     * Helper methods.
     */

    /**
     * @param array An int array.
     * @return A List copy of array.
     */
    private List<Integer> intArrayToList(int[] array) {
        // This is a Java stream. It is not part of the course, but extra fluff
        // for the interested. It is part of Java 8's half-hearted venture into
        // functional programming.
        return Arrays.stream(array).boxed().collect(Collectors.toList());
    }

    /**
     * @param list An integer List.
     * @return An int array copy of List.
     */
    private int[] integerListToIntArray(List<Integer> list) {
        return list.stream().mapToInt(Integer::valueOf).toArray();
    }

    /**
     * Helper assert methods.
     */

    /**
     * Assert that listA is the reverse of listB.
     * Note that this is a reflexive relation.
     *
     * @param listA A list.
     * @param listB A list.
     */
    private void assertReversed(List<Integer> listA, List<Integer> listB) {
        int[] arrayA = integerListToIntArray(listA);
        int[] arrayB = integerListToIntArray(listB);
        assertReversed(arrayA, arrayB);
    }

    /**
     * Assert that arrayA is the reverse of arrayB.
     * Note that this is a reflexive relation.
     *
     * @param arrayA An array.
     * @param arrayB An array.
     */
    private void assertReversed(int[] arrayA, int[] arrayB) {
        Integer len = arrayA.length;
        for (Integer i = 0; i < len; i++) {
            assertThat(arrayB[len-i-1], equalTo(arrayA[i]));
        }
    }
}