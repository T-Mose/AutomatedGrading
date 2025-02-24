import java.util.Set;
import java.util.HashSet;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.stream.IntStream;

import org.junit.Test;
import org.junit.Before;
import static org.junit.Assert.*;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.CoreMatchers.*;

/**
 * Abstract test glass for graphs. Implementing test classes must override the
 * getEmptyGraph method.
 *
 * @author Glenn Olsson (adapted from test by Simon Larsén (adapted from tests
 *         by Stefan Nilsson))
 * @version 2020-07-08
 */
public class GraphTest {
    private Graph g0;
    private Graph g1;
    private Graph g5;
    private Graph graph;

    private Graph threeBiEdgeGraph;
    private final int numVertices = 5;
    private final int cost = 42;
    private final int fromADegree = 2;
    private final int toADegree = 1;
    private final int toBDegree = 2;
    int[] oorVertices = { -1, -10, numVertices, 2 * numVertices };
    int fromA = 2;
    int toA = 4;
    int fromB = 1;
    int toB = 3;
    int fromC = 3;
    int toC = 2;

    private int NO_COST = -1;

    private final int g0Edges = 0;
    private final int g0Vertices = 0;
    private final int g1Edges = 1;
    private final int g1Vertices = 1;
    private final int g5Edges = 3;
    private final int g5Vertices = 5;

    /**
     * @param numVertices The amount of vertices in the graph.
     * @return A Graph instance with no edges.
     */
    private Graph getEmptyGraph(int numVertices) {
        return new Graph(numVertices);
    }

    @Before
    public void setUp() {
        g0 = getEmptyGraph(0);
        g1 = getEmptyGraph(1);
        g5 = getEmptyGraph(5);

        g1.add(0, 0, NO_COST);
        g5.add(0, 1, NO_COST);
        g5.add(2, 3, 1);

        graph = getEmptyGraph(numVertices);

        threeBiEdgeGraph = getEmptyGraph(numVertices);
        threeBiEdgeGraph.add(fromA, toA, cost);
        threeBiEdgeGraph.add(fromB, toB, cost);
        threeBiEdgeGraph.add(fromC, toC, cost);
    }

    @Test
    public void addDuplicateEdgesDoesNotIncreasenumEdges() {
        // Arrange
        int numVertices = 10;
        Graph graph = getEmptyGraph(numVertices);
        int from = 0;
        int to = 1;
        graph.add(from, to, NO_COST);
        int expectedNumEdges = 1;

        // Act
        for (int i = 0; i < numVertices; i++) {
            graph.add(from, to, NO_COST);
        }
        int actualNumEdges = graph.numEdges();

        // Assert
        assertThat(actualNumEdges, equalTo(expectedNumEdges));
    }

    @Test
    public void addOnceWithCostIncreasesNumEdgesByOne() {
        // Arrange
        int numVertices = 5;
        int cost = 56;
        Graph graph = getEmptyGraph(numVertices);
        int expectedNumEdges = 1;

        // Act
        graph.add(2, 3, cost);
        int actualNumEdges = graph.numEdges();

        // Assert
        assertThat(actualNumEdges, equalTo(expectedNumEdges));
    }

    @Test
    public void addDuplicateEdgesWithCostDoesNotIncreasenumEdges() {
        // Arrange
        int numVertices = 10;
        int cost = 24;
        Graph graph = getEmptyGraph(numVertices);
        int from = 0;
        int to = 1;
        graph.add(from, to, cost);
        int expectedNumEdges = 1;

        // Act
        for (int i = 0; i < numVertices; i++) {
            graph.add(from, to, cost);
        }
        int actualNumEdges = graph.numEdges();

        // Assert
        assertThat(actualNumEdges, equalTo(expectedNumEdges));
    }

    /**
     * Tests for add
     */

    @Test
    public void addOnceIncreasesNumEdgesByOne() {
        // Arrange
        int expectedNumEdges = 1;

        // Act
        graph.add(2, 3, NO_COST);
        int actualNumEdges = graph.numEdges();

        // Assert
        assertThat(actualNumEdges, equalTo(expectedNumEdges));
    }

    @Test
    public void addFiveUniqueEdgesIncreasesnumEdgesByFive() {
        // Arrange
        int numVertices = 10;
        Graph graph = getEmptyGraph(numVertices);
        int addEdges = numVertices / 2;
        int expectedNumEdges = addEdges;

        // Act
        for (int i = 0; i < addEdges; i++) {
            graph.add(i, i + 1, NO_COST);
        }
        int actualNumEdges = graph.numEdges();

        // Assert
        assertThat(actualNumEdges, equalTo(expectedNumEdges));
    }

    @Test
    public void addFiveUniqueEdgesWithCostIncreasesnumEdgesByFive() {
        // Arrange
        int numVertices = 10;
        Graph graph = getEmptyGraph(numVertices);
        int addEdges = numVertices / 2;
        int expectedNumEdges = addEdges;

        // Act
        for (int i = 0; i < addEdges; i++) {
            graph.add(i, i + 1, NO_COST);
        }
        int actualNumEdges = graph.numEdges();

        // Assert
        assertThat(actualNumEdges, equalTo(expectedNumEdges));
    }

    @Test
    public void addExceptionWhenFromIsOutOfRange() {
        // Arrange

        int to = 0;
        for (int from : oorVertices) {
            // Act
            try {
                graph.add(from, to, NO_COST);
                fail(String.format(
                        "Expected IllegalArgumentException on Graph.add(%d, %d, NO_COST) with %d total vertices", from,
                        to, numVertices));
            } catch (IllegalArgumentException e) {
                // pass
            }
        }
    }

    @Test
    public void addWithCostExceptionWhenFromIsOutOfRange() {
        // Arrange
        int cost = 10;

        int to = 0;
        for (int from : oorVertices) {
            // Act
            try {
                graph.add(from, to, cost);
                fail(String.format("Expected IllegalArgumentException on Graph.add(%d, %d, %d) with %d total vertices",
                        from, to, cost, numVertices));
            } catch (IllegalArgumentException e) {
                // pass
            }
        }
    }

    @Test
    public void addExceptionWhenToIsOutOfRange() {
        // Arrange

        int from = 0;
        for (int to : oorVertices) {
            // Act
            try {
                graph.add(from, to, NO_COST);
                fail(String.format(
                        "Expected IllegalArgumentException on Graph.add(%d, %d, NO_COST) with %d total vertices", from,
                        to, numVertices));
            } catch (IllegalArgumentException e) {
                // pass
            }
        }
    }

    @Test
    public void addWithCostExceptionWhenToisOutOfRange() {
        // Arrange
        int cost = 10;

        int from = 0;
        for (int to : oorVertices) {
            // Act
            try {
                graph.add(from, to, cost);
                fail(String.format(
                        "Expected IllegalArgumentException on Graph.add(%d, %d, NO_COST) with %d total vertices", from,
                        to, numVertices));
            } catch (IllegalArgumentException e) {
                // pass
            }
        }
    }

    /**
     * Tests for add/add/hasEdge
     */

    @Test
    public void hasEdgeIsTrueWhenEdgeWasAddedWithAdd() {
        // Arrange
        int fromA = 0;
        int fromB = 0;
        int toB = 3;

        graph.add(fromA, fromA, NO_COST);
        graph.add(fromB, toB, NO_COST);

        // Act
        boolean hasEdgeA = graph.hasEdge(fromA, fromA);
        boolean hasEdgeB = graph.hasEdge(fromB, toB);

        // Assert
        assertThat(hasEdgeA, equalTo(true));
        assertThat(hasEdgeB, equalTo(true));
    }

    @Test
    public void hasEdgeIsTrueWhenEdgeWasAddedWithAddWithCost() {
        // Arrange
        int fromA = 0;
        int fromB = 0;
        int toB = 3;
        int cost = 10;

        graph.add(fromA, fromA, cost);
        graph.add(fromB, toB, cost);

        // Act
        boolean hasEdgeA = graph.hasEdge(fromA, fromA);
        boolean hasEdgeB = graph.hasEdge(fromB, toB);

        // Assert
        assertThat(hasEdgeA, equalTo(true));
        assertThat(hasEdgeB, equalTo(true));
    }

    @Test
    public void hasEdgeIsFalseWhenEdgeIsNotAssigned() {
        // Arrange
        int from = 3;
        int to = 1;
        graph.add(from, to, NO_COST);

        // Act
        boolean hasOtherEdge = graph.hasEdge(from - 1, to);

        // Assert
        assertThat(hasOtherEdge, is(false));
    }

    @Test
    public void hasEdgeExceptionWhenFromIsOutOfRange() {
        // Arrange
        int cost = 10;

        int to = 0;
        for (int from : oorVertices) {
            // Act
            try {
                graph.hasEdge(from, to);
                fail(String.format("Expected IllegalArgumentException on Graph.hasEdge(%d, %d) with %d total vertices",
                        from, to, numVertices));
            } catch (IllegalArgumentException e) {
                // pass
            }
        }
    }

    @Test
    public void hasEdgeExceptionWhenToIsOutOfRange() {
        // Arrange
        int cost = 10;

        int from = 0;
        for (int to : oorVertices) {
            // Act
            try {
                graph.hasEdge(from, to);
                fail(String.format("Expected IllegalArgumentException on Graph.hasEdge(%d, %d) with %d total vertices",
                        from, to, numVertices));
            } catch (IllegalArgumentException e) {
                // pass
            }
        }
    }

    @Test
    public void costIsCorrectWhenEdgeWasAddedWithAddWithCost() {
        // Arrange
        int fromA = 0;
        int fromB = 0;
        int toB = 3;
        int expectedCost = 43;

        graph.add(fromA, fromA, expectedCost / 2);
        // overwrite cost
        graph.add(fromA, fromA, expectedCost);
        graph.add(fromB, toB, expectedCost);

        // Act
        int actualCostA = graph.cost(fromA, fromA);
        int actualCostB = graph.cost(fromB, toB);

        // Assert
        assertThat(actualCostA, equalTo(expectedCost));
        assertThat(actualCostB, equalTo(expectedCost));
    }

    @Test
    public void costExceptionWhenFromIsOutOfRange() {
        // Arrange
        int cost = 10;

        int to = 0;
        for (int from : oorVertices) {
            // Act
            try {
                graph.cost(from, to);
                fail(String.format("Expected IllegalArgumentException on Graph.cost(%d, %d) with %d total vertices",
                        from, to, numVertices));
            } catch (IllegalArgumentException e) {
                // pass
            }
        }
    }

    @Test
    public void costEdgeExceptionWhenToIsOutOfRange() {
        // Arrange
        int cost = 10;

        int from = 0;
        for (int to : oorVertices) {
            // Act
            try {
                graph.cost(from, to);
                fail(String.format("Expected IllegalArgumentException on Graph.cost(%d, %d) with %d total vertices",
                        from, to, numVertices));
            } catch (IllegalArgumentException e) {
                // pass
            }
        }
    }

    /**
     * Tests for remove
     */

    @Test
    public void removeThreeTimesOnDifferentEdgesInGraphDeacreasesNumEdgesByTree() {
        // Arrange
        int expectedNumEdges = 0;

        // Act
        threeBiEdgeGraph.remove(fromA, toA);
        threeBiEdgeGraph.remove(toA, fromA);
        threeBiEdgeGraph.remove(fromB, toB);
        threeBiEdgeGraph.remove(toB, fromB);
        threeBiEdgeGraph.remove(fromC, toC);
        int actualNumEdges = threeBiEdgeGraph.numEdges();

        // Assert
        assertThat(actualNumEdges, equalTo(expectedNumEdges));
    }

    @Test
    public void removeEdgeNotInGraphDoesNotAlterNumEdges() {
        // Arrange
        int expectedNumEdges = threeBiEdgeGraph.numEdges();
        // Act
        // threeBiEdgeGraph has no edges to or from 0
        threeBiEdgeGraph.remove(0, 0);
        threeBiEdgeGraph.remove(0, 1);
        threeBiEdgeGraph.remove(numVertices - 1, 0);
        int actualNumEdges = threeBiEdgeGraph.numEdges();
        // Assert
        assertThat(actualNumEdges, equalTo(expectedNumEdges));
    }

    @Test
    public void removeExceptionWhenFromIsOutOfRange() {
        // Arrange
        int to = 0;
        for (int from : oorVertices) {
            // Act
            try {
                graph.remove(from, to);
                fail(String.format("Expected IllegalArgumentException on Graph.remove(%d, %d) with %d total vertices",
                        from, to, numVertices));
            } catch (IllegalArgumentException e) {
                // pass
            }
        }
    }

    /**
     * Tests for remove
     */

    @Test
    public void removeOnceOnEdgeInGraphDecreasesNumEdgesByOne() {
        // Arrange
        int expectedNumEdges = 2; // 3 edges - 1 -> 2 edges
        // Act
        threeBiEdgeGraph.remove(fromA, toA);
        int actualNumEdges = threeBiEdgeGraph.numEdges();
        // Assert
        assertThat(actualNumEdges, equalTo(expectedNumEdges));
    }

    @Test
    public void removeThreeTimesOnDifferentEdgesInGraphDeacreasesNumEdgesByThree() {
        // Arrange
        int expectedNumEdges = 0;

        // Act
        threeBiEdgeGraph.remove(fromA, toA);
        threeBiEdgeGraph.remove(fromB, toB);
        threeBiEdgeGraph.remove(fromC, toC);
        int actualNumEdges = threeBiEdgeGraph.numEdges();

        // Assert
        assertThat(actualNumEdges, equalTo(expectedNumEdges));
    }

    @Test
    public void removeOnEdgeNotInGraphDoesNotAlterNumEdges() {
        // Arrange
        int expectedNumEdges = 3;

        // Act
        // threeBiEdgeGraph has no vertices from or to 0
        threeBiEdgeGraph.remove(0, 1);
        threeBiEdgeGraph.remove(1, 0);
        threeBiEdgeGraph.remove(numVertices - 1, 0);
        int actualNumEdges = threeBiEdgeGraph.numEdges();

        // Assert
        assertThat(actualNumEdges, equalTo(expectedNumEdges));
    }

    @Test
    public void removeExceptionWhenToIsOutOfRange() {
        // Arrange
        int from = 0;
        for (int to : oorVertices) {
            // Act
            try {
                graph.remove(from, to);
                fail(String.format("Expected IllegalArgumentException on Graph.remove(%d, %d) with %d total vertices",
                        from, to, numVertices));
            } catch (IllegalArgumentException e) {
                // pass
            }
        }
    }

    /**
     * Tests for remove/remove/hasEdge
     */

    @Test
    public void hasEdgeFalseWhenEdgeWasRemovedWithRemove() {
        // Act
        threeBiEdgeGraph.remove(fromA, toA);
        threeBiEdgeGraph.remove(toB, fromB);
        boolean hasEdgeA = threeBiEdgeGraph.hasEdge(fromA, toA);
        boolean hasReverseEdgeB = threeBiEdgeGraph.hasEdge(toB, fromB);

        // Assert
        assertThat(hasEdgeA, is(false));
        assertThat(hasReverseEdgeB, is(false));
    }

    @Test
    public void hasEdgeFalseWhenEdgeWasRemovedWithremove() {
        // Act
        threeBiEdgeGraph.remove(fromA, toA);
        threeBiEdgeGraph.remove(fromB, toB);
        boolean hasEdgeA = threeBiEdgeGraph.hasEdge(fromA, toA);
        boolean hasReverseEdgeA = threeBiEdgeGraph.hasEdge(toA, fromA);
        boolean hasEdgeB = threeBiEdgeGraph.hasEdge(fromB, toB);
        boolean hasReverseEdgeB = threeBiEdgeGraph.hasEdge(toB, fromB);

        // Assert
        assertThat(hasEdgeA, is(false));
        assertThat(hasReverseEdgeA, is(false));
        assertThat(hasEdgeB, is(false));
        assertThat(hasReverseEdgeB, is(false));
    }

    /**
     * Tests for degree and methods affecting degree
     */

    @Test
    public void degreeExceptionWhenFromIsOutOfRange() {
        // Arrange
        int to = 0;
        for (int from : oorVertices) {
            // Act
            try {
                graph.add(from, to, NO_COST);
                fail(String.format(
                        "Expected IllegalArgumentException on Graph.add(%d, %d, NO_COST) with %d total vertices", from,
                        to, numVertices));
            } catch (IllegalArgumentException e) {
                // pass
            }
        }
    }

    @Test
    public void degreeIsCorrectForVerticesWithOutEdges() {
        // Act
        int actualFromADegree = threeBiEdgeGraph.degree(fromA);
        int actualToADegree = threeBiEdgeGraph.degree(toA);
        int actualToBDegree = threeBiEdgeGraph.degree(toB);
        // Assert
        assertThat(actualFromADegree, equalTo(fromADegree));
        assertThat(actualToADegree, equalTo(toADegree));
        assertThat(actualToBDegree, equalTo(toBDegree));
    }

    @Test
    public void degreeIs0ForVerticesWithoutEdges() {
        // Act
        int actualDegree = threeBiEdgeGraph.degree(0);
        // Assert
        assertThat(actualDegree, equalTo(0));
    }

    @Test
    public void degreeIs0WhenAllOutEdgesHaveBeenRemoved() {
        // Arrange
        threeBiEdgeGraph.remove(fromA, toA);
        threeBiEdgeGraph.remove(fromB, toB);
        threeBiEdgeGraph.remove(fromC, toC);

        // Act
        IntStream.range(0, numVertices).map(i -> threeBiEdgeGraph.degree(i))
                .forEach(degree -> assertThat(degree, equalTo(0)));
    }

    /**
     * Tests for numVertices
     */

    @Test
    public void testNumVertices() {
        assertThat(g0.numVertices(), equalTo(g0Vertices));
        assertThat(g1.numVertices(), equalTo(g1Vertices));
        assertThat(g5.numVertices(), equalTo(g5Vertices));
    }

    /**
     * Tests for neighbors
     */

    /**
     * I'm sorry I was out of time here so I just took the old test that does not
     * conform to the AAA pattern.
     *
     * TODO FOR FUTURE ROUNDS: Break this test up!
     */
    @Test
    public void testNeighbors() {
        Iterator<Integer> pi = g1.neighbors(0);
        assertTrue(pi.hasNext());
        assertEquals(0, pi.next().intValue());
        pi = g5.neighbors(0);
        assertEquals(1, pi.next().intValue());
        assertFalse(pi.hasNext());
        g5.add(0, 0, NO_COST);
        g5.add(0, 1, NO_COST);
        g5.add(0, 2, NO_COST);
        g5.add(0, 3, NO_COST);
        pi = g5.neighbors(0);
        Set<Integer> s = new HashSet<Integer>();
        for (int i = 0; i < 4; i++) {
            s.add(pi.next());
        }
        assertEquals(4, s.size());
        for (int i = 0; i < 4; i++) {
            assertTrue(s.contains(i));
        }
        assertFalse(pi.hasNext());
        try {
            pi.next();
            fail();
        } catch (NoSuchElementException e) {
        }
        pi = g5.neighbors(4);
        assertFalse(pi.hasNext());
        try {
            pi.next();
            fail();
        } catch (NoSuchElementException e) {
        }
    }

    /**
     * Tests for toString
     */
    @Test
    public void toStringIsBracesWhenGraphIsEmpty() {
        // Arrange
        String expected = "{}";
        // Act
        String actual = g0.toString();
        // Assert
        assertThat(actual, equalTo(expected));
    }

    @Test
    public void toStringIsCorrectWhenGraphIsNotEmpty() {
        // Arrange
        String g1Expected = "{(0,0,-1)}";

        String g5CandidateA = "{(0,1,-1), (2,3,1)}";
        String g5CandidateB = "{(2,3,1), (0,1,-1)}";

        // Act
        String g1Actual = g1.toString();
        String g5Actual = g5.toString();

        // Assert
        assertThat(g1Actual, equalTo(g1Expected));
        assertThat(g5Actual, either(equalTo(g5CandidateA)).or(equalTo(g5CandidateB)));
    }
}