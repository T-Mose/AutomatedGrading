// REPOBEE-SANITIZER-SHRED
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

/**
 * Tests for the Loop class
 *
 * @author Linus Östlund
 * @author Gabriel Skoglund
 */
public class LoopsTest {

    Loops loops;

    @Before
    public void setUp() {
        loops = new Loops();
    }

    @Test
    @Ignore // FIXME: There appears to be an issue with tests redirecting stdout and testing with repobee,
            // skip this test until we figure it out.
    public void multipleOfSevenIsCorrect() {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        System.setOut(new PrintStream(out));
        loops.multiplesOfSeven();
        String[] lines = out.toString().trim().split("\\n");

        assertEquals(lines.length, 15); // there are 15 multiples, including 0
        for (int i = 0; i * 7 < 100; i++) {
            assertEquals(i * 7, Integer.parseInt(lines[i]));
        }

    }

    @Test
    public void sumUpToSumsAllIntegersTo100() {
        int sum = loops.sumUpTo(100);
        assertEquals(100*101/2, sum); // use closed form
    }

    @Test
    public void sumUpToSumsReturnsZeroIfZeroIsInput() {
        int sum = loops.sumUpTo(0);
        assertEquals(0, sum);
    }

    @Test
    public void sumUpToSumsReturnsZeroIfNegativeInput() {
        int sum = loops.sumUpTo(-5);
        assertEquals(0, sum);
    }

    @Test
    public void sumInBetweenReturns33When10and12IsPassedAsArgument() {
        int sum = loops.sumBetween(10, 12);
        assertEquals(33, sum);
    }

    @Test
    public void sumInBetweenReturnsZeroIfMinIsGreaterThanMax() {
        int sum = loops.sumBetween(12, 10);
        assertEquals(0, sum);
    }

    @Test
    public void sumInBetweenReturnsMinOrMaxIfTheyAreEqual() {
        int sum = loops.sumBetween(12, 12);
        assertEquals(12, sum);
    }

    @Test
    public void sumSquaresWorksUpTo100() {
        int sum = 0;
        for (int i = 0; i <= 100; i++) {
            sum += i * i;
            assertEquals(sum, loops.sumSquares(i));
        }
    }

    @Test
    public void sumReciprocalsWorksUpTo100() {
        double sum = 0;
        for (int i = 1; i <= 100; i++) {
            sum += 1.0 / i;
            assertEquals(sum, loops.sumReciprocals(i), 1e-14);
        }
    }
}