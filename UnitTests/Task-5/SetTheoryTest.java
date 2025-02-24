// REPOBEE-SANITIZER-SHRED

import org.junit.Test;

import static org.junit.Assert.assertEquals;

import java.util.*;
import java.util.stream.IntStream;

/**
 * Test for SetTheory
 *
 * @author Linus Östlund
 * @author Gabriel Skoglund
 */
public class SetTheoryTest {

    private static final int MIN = 0;
    private static final int MAX = 100;
    private static final List<Integer> UNIVERSE = IntStream.range(MIN, MAX).boxed().toList();

    @Test
    public void generateSetCorrectlyCreatesUniverse() {
        List<Integer> expected = IntStream.range(MIN, MAX).boxed().toList();
        List<Integer> actual = SetTheory.generateSet(MIN, MAX);
        assertEquals(expected, actual);
    }

    @Test
    public void generateSetCorrectlyCreatesInterval() {
        List<Integer> expected = IntStream.range(67, 89).boxed().toList();
        List<Integer> actual = SetTheory.generateSet(67, 89);
        assertEquals(expected, actual);
    }
    @Test
    public void generateSetReturnsEmptySetWhenMinIsGreaterThanMax() {
        List<Integer> actual = SetTheory.generateSet(2, 1);
        assertEquals(Collections.emptyList(), actual);
    }

    @Test
    public void generateSetReturnsEmptySetWhenMinIsEqualToMax() {
        List<Integer> actual = SetTheory.generateSet(1, 1);
        assertEquals(Collections.emptyList(), actual);
    }

    @Test
    public void generateSetReturnsExpectedResultWhenMaxIsGreaterThan100() {
        List<Integer> expected = IntStream.range(50, MAX).boxed().toList();
        List<Integer> actual = SetTheory.generateSet(50, 101);
        assertEquals(expected, actual);
    }

    @Test
    public void generateSetReturnsExpectedResultWhenMinIsLessThan0() {
        List<Integer> expected = IntStream.range(MIN, 50).boxed().toList();
        List<Integer> actual = SetTheory.generateSet(-1, 50);
        assertEquals(expected, actual);
    }

    @Test
    public void unionReturnsExpectedResultWhenSetsOverlap() {
        List<Integer> a = IntStream.range(10, 55).boxed().toList();
        List<Integer> b = IntStream.range(50, 90).boxed().toList();

        Set<Integer> expected =  new HashSet<>(a);
        expected.addAll(b);
        List<Integer> actual = SetTheory.union(new ArrayList<>(a), new ArrayList<>(b));

        assertEquals(expected.stream().toList(), actual);
    }

    @Test
    public void unionReturnsExpectedResultWhenSetsAreDisjoint() {
        List<Integer> a = IntStream.range(10, 50).boxed().toList();
        List<Integer> b = IntStream.range(55, 90).boxed().toList();

        Set<Integer> expected =  new HashSet<>(a);
        expected.addAll(b);
        List<Integer> actual = SetTheory.union(new ArrayList<>(a), new ArrayList<>(b));

        assertEquals(expected.stream().toList(), actual);
    }

    @Test
    public void intersectionReturnsExpectedResultWhenSetsOverlap() {
        List<Integer> a = IntStream.range(10, 55).boxed().toList();
        List<Integer> b = IntStream.range(50, 90).boxed().toList();

        Set<Integer> expected =  new HashSet<>(a);
        expected.retainAll(b);
        List<Integer> actual = SetTheory.intersection(new ArrayList<>(a), new ArrayList<>(b));

        assertEquals(expected.stream().toList(), actual);
    }

    @Test
    public void intersectionReturnsEmptyListWhenSetsAreDisjoint() {
        List<Integer> a = IntStream.range(10, 50).boxed().toList();
        List<Integer> b = IntStream.range(55, 90).boxed().toList();
        List<Integer> actual = SetTheory.intersection(new ArrayList<>(a), new ArrayList<>(b));

        assertEquals(Collections.emptyList(), actual);
    }

    @Test
    public void complementReturnsEmptySetWhenInputIsUniverse() {
        assertEquals(Collections.emptyList(), SetTheory.complement(new ArrayList<>(UNIVERSE)));
    }

    @Test
    public void complementReturnsExpectedResultForInterval() {
        List<Integer> set = IntStream.range(45, 67).boxed().toList();
        var expected = new HashSet<>(UNIVERSE);
        expected.removeAll(set);
        assertEquals(expected.stream().toList(), SetTheory.complement(new ArrayList<>(set)));
    }

    @Test
    public void cardinalityReturnsCorrectValueForUniverse() {
        assertEquals(UNIVERSE.size(), SetTheory.cardinality(new ArrayList<>(UNIVERSE)));
    }

    @Test
    public void cardinalityReturnsCorrectValueForEmptySet() {
        assertEquals(0, SetTheory.cardinality(new ArrayList<>()));
    }

    @Test
    public void cardinalityOfUnionReturnsCorrectValueForOverlappingSets() {
        int actual = SetTheory.cardinalityOfUnion(new ArrayList<>(UNIVERSE), new ArrayList<>(UNIVERSE));
        assertEquals(UNIVERSE.size(), actual);
    }

    @Test
    public void cardinalityOfUnionReturnsCorrectValueForDisjointSets() {
        List<Integer> a = IntStream.range(MIN, 21).boxed().toList();
        List<Integer> b = IntStream.range(50, 67).boxed().toList();
        int actual = SetTheory.cardinalityOfUnion(new ArrayList<>(a), new ArrayList<>(b));
        assertEquals(a.size() + b.size(), actual);
    }

    @Test
    public void cardinalityOfUnionReturnsCorrectValueWhenBothSetsAreEmpty() {
        assertEquals(0, SetTheory.cardinalityOfUnion(new ArrayList<>(), new ArrayList<>()));
    }
}