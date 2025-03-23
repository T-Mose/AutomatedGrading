/**
 * Test class for QuicksortFixedPivotInsertion. Based on the general tests.
 */
public class QuicksortFixedPivotInsertionTest extends IntSorterTest {
    @Override
    protected IntSorter getIntSorter() {
        return new QuicksortFixedPivotInsertion(); // Return the specific implementation
    }
}