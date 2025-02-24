import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Test cases for the Calculator class
 *
 * @author Gabriel Skoglund
 * @author YOUR NAME HERE
 */
public class CalculatorTest {

    /**
     * We allow a small error when checking the result of floating point arithmetic operations.
     * You can read more about why at: https://en.wikipedia.org/wiki/Floating-point_arithmetic#Accuracy_problems
     */
    private static final double ALLOWED_ERROR = 1e-16;
    private Calculator calculator;

    /**
     * The setUp method will be called before each test is run, making sure that
     * we get a fresh calculator object with starting value 0.
     */
    @Before
    public void setUp() {
        calculator = new Calculator();
    }

    @Test
    public void addGivesCorrectResultForPositiveValues() {
        double expected = 0;
        for (int i = 1; i < 10; i++) {
            expected += i;
            assertEquals(expected, calculator.add(i), ALLOWED_ERROR);
        }
    }

    @Test
    public void addGivesCorrectResultForNegativeValues() {
        double expected = 0;
        for (int i = -1; i > -10; i--) {
            expected += i;
            assertEquals(expected, calculator.add(i), ALLOWED_ERROR);
        }
    }

    @Test
    public void addGivesCorrectResultForZero() {
        assertEquals(0, calculator.add(0), ALLOWED_ERROR);
    }

    @Test
    public void subtractGivesCorrectResultForPositiveValues() {
        double expected = 0;
        for (int i = 1; i < 10; i++) {
            expected -= i;
            assertEquals(expected, calculator.subtract(i), ALLOWED_ERROR);
        }
    }

    @Test
    public void subtractGivesCorrectResultForNegativeValues() {
        double expected = 0;
        for (int i = -1; i > -10; i--) {
            expected -= i;
            assertEquals(expected, calculator.subtract(i), ALLOWED_ERROR);
        }
    }

    @Test
    public void subtractGivesCorrectResultForZero() {
        assertEquals(0, calculator.subtract(0), ALLOWED_ERROR);
    }

    @Test
    public void multiplyGivesCorrectResultForPositiveValues() {
        double expected = 0;
        for (int i = 1; i < 10; i++) {
            expected *= i;
            assertEquals(expected, calculator.multiply(i), ALLOWED_ERROR);
        }
    }

    @Test
    public void multiplyGivesCorrectResultForNegativeValues() {
        double expected = 0;
        for (int i = -1; i > -10; i--) {
            expected *= i;
            assertEquals(expected, calculator.multiply(i), ALLOWED_ERROR);
        }
    }

    @Test
    public void multiplyGivesCorrectResultForZero() {
        // First add something to make sure it is reduced to zero by multiplication with zero
        calculator.add(5);
        assertEquals(0, calculator.multiply(0), ALLOWED_ERROR);
    }

    @Test
    public void divideGivesCorrectResultForPositiveValues() {
        double expected = 0;
        for (int i = 1; i < 10; i++) {
            expected /= i;
            assertEquals(expected, calculator.divide(i), ALLOWED_ERROR);
        }
    }

    @Test
    public void divideGivesCorrectResultForNegativeValues() {
        double expected = 0;
        for (int i = -1; i > -10; i--) {
            expected /= i;
            assertEquals(expected, calculator.divide(i), ALLOWED_ERROR);
        }
    }

    /**
     * We want to make sure that we throw an illegal argument exception when dividing by zero
     */
    @Test (expected=IllegalArgumentException.class)
    public void divideGivesIllegalArgumentExceptionWhenDividingByZero() {
        calculator.divide(0);
    }

}