import org.junit.Test;
import org.junit.Before;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.CoreMatchers.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Collections;
import java.util.List;
import java.util.ArrayList;

/**
 * Test class for the ListProcessor.
 *
 * @author Simon Larsén
 * @author Anton Lyxell
 * @version 2018-11-11
 *
 * Assistant's note: Below is a test class for
 * your list processor. This class creates an instance
 * of the ListProcessor class and invokes its methods
 * to verify the correctness of your implementation.
 */
public class ListProcessorTest {

    /* An instance of your list processor */
    private ListProcessor listProcessor;
    /* A non-empty array, initialized in the setUp method */
    private int[] nonEmptyArray;
    /* A non-empty list, initialized in the setUp method */
    private List<Integer> nonEmptyList;
    /* The sum of the elements in the arbitrary lists */
    private int nonEmptySequenceSum;

    /* The number of iterations to run in the distribution test */
    private final int SHUFFLE_DISTRIBUTION_NUM_ITERATIONS = 1000;
    /* The number of list elements in the list to shuffle in
       the distribution test */
    private final int SHUFFLE_DISTRIBUTION_NUM_LIST_ELEMENTS = 4;
    /* The maximum allowed number of occurences for each
       permutation of the list in the distribution test */
    private final int SHUFFLE_DISTRIBUTION_OCCURENCE_MAX_LIMIT = 300;

    /**
     * Set up variables to be available for each test case.
     *
     * Assistant's note: this function is executed once before every test
     * case in this class. In this method we initalize some default
     * values that will use to verify the correctness of the list processor.
     */
    @Before
    public void setUp() {
        listProcessor = new ListProcessor();
        nonEmptyArray = new int[]{-13, 16, -11, 19, 37, 16};
        nonEmptyList = Arrays.asList(-13, 16, -11, 19, 37, 16);
        nonEmptySequenceSum = -13 + 16 + -11 + 19 + 37 + 16;
    }

    /**
     * Assistant's note: Below are test cases that test certain
     * properties of the list processor.
     *
     * Before each test case we have given a brief description of
     * what property the test case tries to verify.
     */

    /**
     * Assistant's note: The first six test cases involves the
     * sequence constructions.
     */

    /**
     * Assert that arraySequence returns an empty array
     * when the upper and lower bound are equal.
     */
    @Test
    public void arraySequenceIsEmptyOnEqualBounds() {
        // Act, Assert
        assertThat(listProcessor.arraySequence(1, 1).length, equalTo(0));
    }

    /**
     * Assert that listSequence returns an empty list
     * when the upper and lower bound are equal.
     */
    @Test
    public void listSequenceIsEmptyOnEqualBounds() {
        // Act, Assert
        assertThat(listProcessor.listSequence(1, 1).size(), equalTo(0));
    }

    /**
     * Assert that arraySequence throws and exception
     * if the lower bound is greater than the upper bound.
     */
    @Test(expected = IllegalArgumentException.class)
    public void arraySequenceThrowsExceptionWhenFromIsGreaterThanTo() {
        // Act, Assert
        listProcessor.arraySequence(2, 1);
    }

    /**
     * Assert that listSequence throws an exception
     * if the lower bound is greater than the upper bound.
     */
    @Test(expected = IllegalArgumentException.class)
    public void listSequenceThrowsExceptionWhenFromIsGreaterThanTo() {
        // Act, Assert
        listProcessor.listSequence(2, 1);
    }

    /**
     * Assert that arraySequence returns an array containing
     * the correct half-open interval between the specified bounds
     * when the upper bound is greater than the lower bound.
     */
    @Test
    public void arraySequenceHasCorrectLimits() {
        // Act, Assert
        assertThat(listProcessor.arraySequence(-2, 3),
            equalTo(new int[]{-2, -1, 0, 1, 2}));
    }

    /**
     * Assert that listSequence returns a list containing
     * the correct half-open interval between the specified bounds
     * when the upper bound is greater than the lower bound.
     */
    @Test
    public void listSequenceHasCorrectLimits() {
        // Act, Assert
        assertThat(listProcessor.listSequence(-2, 3),
            equalTo(Arrays.asList(-2, -1, 0, 1, 2)));
    }

    /**
     * Assistant's note: Below are the tests for the different
     * variants of sum.
     */

    /**
     * Assert that sumIterative returns 0 when the input is
     * an empty array.
     */
    @Test
    public void iterativeSumOfEmptyArrayIsZero() {
        // Act, Assert
        assertThat(listProcessor.sumIterative(new int[0]),
            equalTo(0));
    }

    /**
     * Assert that sumIterative returns 0 when the input is
     * an empty list.
     */
    @Test
    public void iterativeSumOfEmptyListIsZero() {
        // Act, Assert
        assertThat(listProcessor.sumIterative(new ArrayList<Integer>()),
            equalTo(0));
    }

    /**
     * Assert that sumRecursive returns 0 when the input is
     * an empty array.
     */
    @Test
    public void recursiveSumOfEmptyArrayIsZero() {
        // Act, Assert
        assertThat(listProcessor.sumRecursive(new int[0]),
            equalTo(0));
    }

    /**
     * Assert that sumRecursive returns 0 when the input is an empty list.
     */
    @Test
    public void recursiveSumOfEmptyListIsZero() {
        // Act, Assert
        assertThat(listProcessor.sumRecursive(new ArrayList<Integer>()),
            equalTo(0));
    }

    /**
     * Assert that sumIterative calculates the sum of a non-empty array
     * correctly.
     */
    @Test
    public void iterativeArraySumIsCorrect() {
        // Act, Assert
        assertThat(listProcessor.sumIterative(nonEmptyArray),
            equalTo(nonEmptySequenceSum));
    }

    /**
     * Assert that sumIterative calculates the sum of a non-empty list
     * correctly.
     */
    @Test
    public void iterativeListSumIsCorrect() {
        // Act, Assert
        assertThat(listProcessor.sumIterative(nonEmptyList),
            equalTo(nonEmptySequenceSum));
    }

    /**
     * Assert that sumRecursive calculates the sum of a non-empty array
     * correctly.
     */
    @Test
    public void recursiveArraySumIsCorrect() {
        // Act, Assert
        assertThat(listProcessor.sumRecursive(nonEmptyArray),
            equalTo(nonEmptySequenceSum));
    }

    /**
     * Assert that sumRecursive calculates the sum of a non-empty list
     * correctly.
     */
    @Test
    public void recursiveListSumIsCorrect() {
        // Act, Assert
        assertThat(listProcessor.sumRecursive(nonEmptyList),
            equalTo(nonEmptySequenceSum));
    }

    /**
     * Assistant's note: Below are the tests for shuffle.
     */

    /**
     * Assert that shuffle does not modify the list given as an input.
     */
    @Test
    public void shuffleDoesNotModifyInputList() {
        // Arrange
        List<Integer> copy = new ArrayList<Integer>(nonEmptyList);
        // Act
        listProcessor.shuffled(nonEmptyList);
        // Assert
        assertThat(nonEmptyList, equalTo(copy));
    }

    /**
     * Assert that shuffle does not modify the array given as an input.
     */
    @Test
    public void shuffleDoesNotModifyInputArray() {
        // Arrange
        int[] copy = nonEmptyArray.clone();
        // Act
        listProcessor.shuffled(nonEmptyArray);
        // Assert
        assertThat(nonEmptyArray, equalTo(copy));
    }

    /**
     * Assert that a list contains the same elements before and after
     * shuffling.
     */
    @Test
    public void shuffledListContainsSameElementsAsOriginal() {
        // Act
        List<Integer> shuffledList = listProcessor.shuffled(nonEmptyList);
        Collections.sort(nonEmptyList);
        Collections.sort(shuffledList);
        // Assert
        assertThat(shuffledList, equalTo(nonEmptyList));
    }

    /**
     * Assert that an array contains the same elements before and after
     * shuffling.
     */
    @Test
    public void shuffledArrayContainsSameElementsAsOriginal() {
        // Act
        int[] shuffledArray = listProcessor.shuffled(nonEmptyArray);
        Arrays.sort(nonEmptyArray);
        Arrays.sort(shuffledArray);
        // Assert
        assertThat(shuffledArray, equalTo(nonEmptyArray));
    }

    @Test
    public void shuffleListDistributionIsFair() {
        // Arrange
        /* Create a hashmap to keep track of the occurrences of each
           permutation of our list */
        HashMap<List<Integer>, Integer> occurences = new HashMap<>();
        /* Construct the list that is to be shuffled */
        List<Integer> list = new ArrayList<>();
        for (int i = 0; i < SHUFFLE_DISTRIBUTION_NUM_LIST_ELEMENTS; i++) {
            list.add(i);
        }
        /* Shuffle the list a number of times, keeping track
           of the number of occurrences of the different permutations
           from the original list */
        // Act
        for (int i = 0; i < SHUFFLE_DISTRIBUTION_NUM_ITERATIONS; i++) {
            /* Shuffle the list */
            list = listProcessor.shuffled(list);
            /* If we have seen this permutation before, increase the
               occurrence count */
            if (occurences.containsKey(list)) {
                occurences.put(list, occurences.get(list) + 1);
            /* If we have not seen this permutation before, set its
               occurrence count to zero */
            } else {
                occurences.put(list, 0);
            }
        }
        /* Assert that the number of occurrences for each permutation is
           within the accepted limit */
        // Assert
        for (Integer occurence : occurences.values()) {
            assertThat("The distribution of shuffle must not be too biased",
                occurence < SHUFFLE_DISTRIBUTION_OCCURENCE_MAX_LIMIT);
        }
    }
    
    /**
     * Assert that the distribution of shuffle is fair when
     * given an array as input.
     */
    @Test
    public void shuffleArrayDistributionIsFair() {
        // Arrange
        /* Create a hashmap to keep track of the occurrences of each
           permutation of our list */
        HashMap<List<Integer>, Integer> occurences = new HashMap<>();
        /* Construct the list that is to be shuffled */
        int[] list = new int[SHUFFLE_DISTRIBUTION_NUM_LIST_ELEMENTS];
        for (int i = 0; i < SHUFFLE_DISTRIBUTION_NUM_LIST_ELEMENTS; i++) {
            list[i] = i;
        }
        // Act
        for (int i = 0; i < SHUFFLE_DISTRIBUTION_NUM_ITERATIONS; i++) {
            /* Shuffle the list and create a key for the hashmap */
            list = listProcessor.shuffled(list);
            List<Integer> key = new ArrayList<>();
            for (int val : list) key.add(val);
            /* If we have seen this permutation before, increase the
               occurrence count */
            if (occurences.containsKey(key)) {
                occurences.put(key, occurences.get(key) + 1);
            /* If we have not seen this permutation before, set its
               occurrence count to zero */
            } else {
                occurences.put(key, 0);
            }
        }
        /* Assert that the number of occurrences for each permutation is
           within the accepted limit */
        // Assert
        for (Integer occurence : occurences.values()) {
            assertThat("The distribution of shuffle must not be too biased",
                occurence < SHUFFLE_DISTRIBUTION_OCCURENCE_MAX_LIMIT);
        }
    }
    

}

/**
 * Assistant's note: Always remember, unit testing is great fun.
 */