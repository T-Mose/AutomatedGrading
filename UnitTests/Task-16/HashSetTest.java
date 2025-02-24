import org.junit.Test;
import org.junit.Before;
import static org.junit.Assert.*;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.CoreMatchers.*;

import java.util.Arrays;

/**
 * Test for the HashSet implementation of the Set interface. Runs both the
 * SetTest tests, as well as tests specific to hashing.
 *
 * This test class mostly contains only hash collision tests.
 *
 * @author Simon Larsén
 * @author Tobias Hansson
 * @version 2018-02-13
 */
public class HashSetTest extends SetTest {
    private Set<SingleHashUnequal> set;
    private SingleHashUnequal[] uniqueObjsWithEqualHashes;

    /**
     * Returns an implementation of Set that can hold at least 'minCapacity'
     * Integers.
     *
     * @param minCapacity The least amount of elements the Set must be able to
     * hold.
     * @return An implementation of Set.
     */
    protected Set<Integer> getIntegerSet(int minCapacity) {
        return new HashSet<Integer>(minCapacity);
    }

    @Override
    @Before
    public void setUp() {
        // We are overriding the setUp method of SetTest, so we need to call
        // it explicitly to not break everything.
        super.setUp();

        int numDummies = 10;
        uniqueObjsWithEqualHashes = new SingleHashUnequal[numDummies];
        set = new HashSet<SingleHashUnequal>(numDummies * 2);
        for (int i = 0; i < numDummies; i++) {
            SingleHashUnequal dummy = new SingleHashUnequal();
            set.add(dummy);
            uniqueObjsWithEqualHashes[i] = dummy;
        }
    }

    @Test
    public void containsIsFalseWhenElementIsNotInSetAndHashCollides() {
        // Test that contains is false when the set contains some other element
        // with the same hash

        // Arrange
        SingleHashUnequal elem = new SingleHashUnequal();
        // Act
        boolean contained = set.contains(elem);
        // Assert
        assertThat(contained, is(false));
    }

    @Test
    public void containsIsTrueForAddedElementsWithEqualHashes() {
        // Arrange
        Arrays
            .stream(uniqueObjsWithEqualHashes)
            // Act
            .map(elem -> set.contains(elem))
            // Assert
            .forEach(contained -> assertThat(contained, is(true)));
    }

    @Test
    public void addIsTrueForUniqueElementsWithEqualHashes() {
        // Arrange
        int capacity = uniqueObjsWithEqualHashes.length;
        Set<SingleHashUnequal> set = new HashSet<SingleHashUnequal>(capacity);
        Arrays
            .stream(uniqueObjsWithEqualHashes)
            // Act
            .map(elem -> set.add(elem))
            // Assert
            .forEach(wasAdded -> assertThat(wasAdded, is(true)));
    }

    @Test
    public void addUniqueElementsWithEqualHashesIncrementsSize() {
        // Arrange
        int capacity = uniqueObjsWithEqualHashes.length;
        Set<SingleHashUnequal> set = new HashSet<SingleHashUnequal>(capacity);
        int expectedSize = 0;
        for (SingleHashUnequal elem : uniqueObjsWithEqualHashes) {
            expectedSize++;
            // Act
            set.add(elem);
            // Assert
            assertThat(set.size(), equalTo(expectedSize));
        }
    }

    @Test
    public void removeUniqueElementsWithEqualHashesDecrementsSize() {
//REPOBEE-SANITIZER-START
        // Arrange
        int expectedSize = set.size();
        for (SingleHashUnequal elem : uniqueObjsWithEqualHashes) {
            expectedSize--;
            // Act
            set.remove(elem);
            // Assert
            assertThat(set.size(), equalTo(expectedSize));
        }
//REPOBEE-SANITIZER-REPLACE-WITH
//        fail("Not implemented");
//REPOBEE-SANITIZER-END
    }

    @Test
    public void removeElementNotInSetWithCollidingHashDoesNotDecreaseSize() {
        // Test that removing an element that is not in the set, but has the
        // same hash as some other element in the set, does not decrement size

//REPOBEE-SANITIZER-START
        // Arrange
        SingleHashUnequal elem = new SingleHashUnequal();
        int expectedSize = uniqueObjsWithEqualHashes.length;

        // Act
        set.remove(elem);

        // Assert
        assertThat(set.size(), equalTo(expectedSize));
//REPOBEE-SANITIZER-REPLACE-WITH
//        fail("Not implemented");
//REPOBEE-SANITIZER-END
    }

    @Test
    public void removeIsFalseWhenElementNotInSetButEqualHashIs() {
        // Test that removing an element that is not in the set, but has the
        // same hash as some other element in the set, returns false

        // Arrange
        SingleHashUnequal elem = new SingleHashUnequal();

        // Act
        boolean removed = set.remove(elem);

        // Assert
        assertThat(removed, is(false));
    }

    /**
     * A helper class for testing hash collisions. Instances equal only
     * themselves, and all instances have the same hashCode.
     */
    private static class SingleHashUnequal {
        private static final int HASH = 0;

        @Override
        public boolean equals(Object o) {
            return this == o;
        }

        @Override
        public int hashCode() {
            return HASH;
        }
    }
}