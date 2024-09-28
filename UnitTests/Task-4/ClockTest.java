// REPOBEE-SANITIZER-SHRED

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Tests for the Clock class
 *
 * @author Linus Östlund
 * @author Gabriel Skoglund
 */
public class ClockTest {

    private Clock clock;

    @Before
    public void setUp() {
        clock = new Clock();
    }

    @Test
    public void zeroParameterConstructorSetsCorrectTime() {
        assertEquals(12, clock.getHours());
        assertEquals(0, clock.getMinutes());
        assertEquals(0, clock.getSeconds());
    }

    @Test
    public void parameterizedConstructorSetsCorrectTime() {
        clock = new Clock(8, 37, 5);
        assertEquals(8, clock.getHours());
        assertEquals(37, clock.getMinutes());
        assertEquals(5, clock.getSeconds());
    }

    @Test
    public void parameterizedConstructorDisregardsInvalidParameterValues() {
        clock = new Clock(-1, 99, 5);
        assertEquals(12, clock.getHours());
        assertEquals(0, clock.getMinutes());
        assertEquals(5, clock.getSeconds());
    }

    @Test
    public void setSecondsDoesNotAcceptValuesBelowZero() {
        clock.setSeconds(14);
        clock.setSeconds(-1);
        assertEquals(14, clock.getSeconds());
    }

    @Test
    public void setSecondsDoesNotAcceptValuesAbove59() {
        clock.setSeconds(10);
        clock.setSeconds(60);
        assertEquals(10, clock.getSeconds());
    }

    @Test
    public void setMinutesDoesNotAcceptValuesBelowZero() {
        clock.setMinutes(14);
        clock.setMinutes(-1);
        assertEquals(14, clock.getMinutes());
    }

    @Test
    public void setMinutesDoesNotAcceptValuesAbove59() {
        clock.setMinutes(10);
        clock.setMinutes(60);
        assertEquals(10, clock.getMinutes());
    }

    @Test
    public void setHoursDoesNotAcceptValuesBelowOne() {
        clock.setHours(5);
        clock.setHours(0);
        assertEquals(5, clock.getHours());
    }

    @Test
    public void setHoursDoesNotAcceptValuesAbove12() {
        clock.setHours(12);
        clock.setHours(13);
        assertEquals(12, clock.getHours());
    }

    @Test
    public void toStringOutputsCorrectFormat() {
        assertEquals("12:00:00", clock.toString().trim());

        clock = new Clock(8, 11, 7);
        assertEquals("08:11:07", clock.toString().trim());
    }

    @Test
    public void tickIncrementsSecondsByOne() {
        clock.tick();
        assertEquals(1, clock.getSeconds());
    }

    @Test
    public void clockTicksFrom125959to010000() {
        clock = new Clock(12, 59, 59);
        clock.tick();
        assertEquals(1, clock.getHours());
        assertEquals(0, clock.getMinutes());
        assertEquals(0, clock.getSeconds());
    }

    @Test
    public void tickIncrementsCorrectNumberOfSeconds() {
        clock = new Clock(8, 37, 14);
        clock.tick(57);
        assertEquals(8, clock.getHours());
        assertEquals(38, clock.getMinutes());
        assertEquals(11, clock.getSeconds());
    }
}