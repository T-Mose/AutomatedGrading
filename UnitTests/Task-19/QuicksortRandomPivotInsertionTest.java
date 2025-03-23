/**
 * Test class for QuicksortRandomPivotInsertionTest. Based on the general tests.
 */
public class QuicksortRandomPivotInsertionTest extends IntSorterTest {
    @Override
    protected IntSorter getIntSorter() {
        return new QuicksortRandomPivotInsertion(); // Return the specific implementation
    }
}