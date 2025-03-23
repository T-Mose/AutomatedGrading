/**
 * Test class for QuicksortRandomPivotTest. Based on the general tests.
 */
public class QuicksortRandomPivotTest extends IntSorterTest {
    @Override
    protected IntSorter getIntSorter() {
        return new QuicksortRandomPivot(); // Return the specific implementation
    }
}