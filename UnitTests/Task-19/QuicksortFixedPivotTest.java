/**
 * Test class for QuicksortFixedPivotTest. Based on the general tests.
 */
public class QuicksortFixedPivotTest extends IntSorterTest {
    @Override
    protected IntSorter getIntSorter() {
        return new QuicksortFixedPivot(); // Return the specific implementation
    }
}