import org.junit.Test;
import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.Timeout;
import static org.junit.Assert.*;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.CoreMatchers.*;

import java.util.Arrays;
/**
 * Test class for Postfix
 *
 * @author Simon Larsén
 * @version 2018-12-15
 */
public class PostfixTest {
    @Rule public Timeout globalTimeout = Timeout.seconds(5);

    @Test
    public void evaluateIsCorrectWhenExpressionContainsManyOperandsInARow() throws Exception {
        String expression = "1 2 3 4 -0 + * - +";
        int expected = -9;
        int actual = Postfix.evaluate(expression);

        assertThat(actual, equalTo(expected));
    }

    @Test
    public void evaluateIsCorrectIntWhenExprContainsOnlyAPositiveInt() throws Exception {
        int evalZero = Postfix.evaluate("0");
        int largeValue = 1234567890;
        int evalLarge = Postfix.evaluate(Integer.toString(largeValue));

        assertThat(evalZero, equalTo(0));
        assertThat(evalLarge, equalTo(largeValue));
    }

    @Test
    public void evaluateIsCorrectIntWhenExprContainsOnlyANegativeInt() throws Exception {
        int evalMinusZero = Postfix.evaluate("-0");
        int smallValue = -1234567890;
        int evalSmall = Postfix.evaluate(Integer.toString(smallValue));

        assertThat(evalMinusZero, equalTo(-0));
        assertThat(evalSmall, equalTo(smallValue));
    }

    @Test
    public void evaluateIsCorrectWhenExprIsAdditionOfPositiveInts() throws Exception {
        int expected = 1 + 23;
        int actual = Postfix.evaluate("1 23 +");
        assertThat(actual, equalTo(expected));
    }

    @Test
    public void evaluateIsCorrectWhenExprIsAdditionOfMixedInts() throws Exception {
        int expected = 1 + 23 + -432;
        int actual = Postfix.evaluate("1 23 + -432 +");
        assertThat(actual, equalTo(expected));
    }

    @Test
    public void evaluateIsCorrectWhenExprWhitespaceIsTabs() throws Exception {
        int expected = 1 + 23;
        int actual = Postfix.evaluate("1    23  +");
        assertThat(actual, equalTo(expected));
    }

    @Test
    public void evaluateIsCorrectWhenExprWithespaceIsTabsAndSpaces() throws Exception {
        int expected = 1 + 23;
        int actual = Postfix.evaluate("         1   23   +");
        assertThat(actual, equalTo(expected));
    }

    @Test
    public void evaluateIsCorrectWhenExprContainsLeadingAndTrailingWhitespace() throws Exception {
        int expected = 1 + 23;
        int actual = Postfix.evaluate(" 1 23 +    ");
        assertThat(actual, equalTo(expected));
    }

    @Test
    public void evaluateIsCorrectWhenExprIsSubtractionOfPositiveInts() throws Exception {
        int expected = 1 - 23;
        int actual = Postfix.evaluate("1 23 -");
        assertThat(actual, equalTo(expected));
    }

    @Test
    public void evaluateIsCorrectWhenExprIsSubtractionOfMixedInts() throws Exception {
        int expected = 1 - -23;
        int actual = Postfix.evaluate("1 -23 -");
        assertThat(actual, equalTo(expected));
    }

    @Test
    public void evaluateIsCorrectWhenExprIsMultiplication() throws Exception {
        int expected = 34 * 123;
        int actual = Postfix.evaluate("34 123 *");
        assertThat(actual, equalTo(expected));
    }

    @Test
    public void evaluateIsCorrectWhenExprIsDivision() throws Exception {
        int expected = 342 / 5;
        int actual = Postfix.evaluate("342 5 /");
        assertThat(actual, equalTo(expected));
    }

    @Test
    public void evaluateIsZeroWhenExprIsDivisionAndNumeratorIsZero() throws Exception {
        int expected = 0 / 1;
        int actual = Postfix.evaluate("0 1 /");
        assertThat(actual, equalTo(expected));
    }

    @Test
    public void evaluateIsCorrectWhenExprIsMixOfOperators() throws Exception {
        int expectedMinusMultPlus = (12 - 34) * (56 + -78);
        String minusMultPlusExpr = "12 34 - 56 -78 + *";
        int expectedPlusDivMinus = (32 + 5) / 2 - 3;
        String plusDivMinusExpr = "32 5 + 2 / 3 -";
        int expectedAllOperators = (((1 + 2) * 3) - 4) / 5;
        String allOperatorsExpr = "1 2 + 3 * 4 - 5 /";

        int actualMinusMultPlus = Postfix.evaluate(minusMultPlusExpr);
        int actualPlusDivMinus = Postfix.evaluate(plusDivMinusExpr);
        int actualAllOperators = Postfix.evaluate(allOperatorsExpr);

        assertThat(actualMinusMultPlus, equalTo(expectedMinusMultPlus));
        assertThat(actualPlusDivMinus, equalTo(expectedPlusDivMinus));
        assertThat(actualAllOperators, equalTo(expectedAllOperators));
    }

    @Test(expected = Postfix.ExpressionException.class)
    public void evaluateExceptionWhenExprIsEmptyString() throws Exception {
        Postfix.evaluate("");
    }

    @Test
    public void evaluateExceptionWhenExprContainsOnlyAnOperator() {
        String[] expressions = {"+", "-", "/", "*"};
        assertExpressionExceptionOnAll(expressions);
    }

    @Test
    public void evaluateExceptionWhenExprContainsIntsConnectedByDash() {
        String[] expressions = {"-1-0", "-0-1", "1-3"};
        assertExpressionExceptionOnAll(expressions);
    }

    @Test
    public void evaluateExceptionWhenExprContainsTooFewOperands() {
        String[] expressions = {"1 +", " 1 2 + +"};
        assertExpressionExceptionOnAll(expressions);
    }

    @Test(expected = Postfix.ExpressionException.class)
    public void evaluateExceptionWhenExprContainsTooManyOperands() throws Exception {
        Postfix.evaluate("1 2 3 +");
    }

    @Test
    public void evaluateExceptionWhenExprContainsIncorrectlyPlacedOperators() {
        String[] expressions = {"1 2+", "1 2) 3 +*", "1 2+"};
        assertExpressionExceptionOnAll(expressions);
    }

    @Test
    public void evaluateExceptionWhenExprContainsIllegalCharacters() {
        String[] expressions = {"1 2 ,", "1 2 .", "0x17", "x", "1234L"};
        assertExpressionExceptionOnAll(expressions);
    }

    @Test
    public void evaluateExceptionWhenExprContainsPositiveIntWithLeadingZero() {
        String[] expressions = {"03", "017"};
        assertExpressionExceptionOnAll(expressions);
    }

    @Test
    public void evaluateExceptionWhenExprContainsNegativeIntWithLeadingZero() {
        String[] expressions = {"-03", "-017"};
        assertExpressionExceptionOnAll(expressions);
    }

    @Test(expected = Postfix.ExpressionException.class)
    public void evaluateExceptionOnDivideByZero() throws Exception {
        Postfix.evaluate("1 0 /");
    }

    /**
     * Assert that every expression in expressions causes Postfix.evaluate to
     * throw an ExpressionException.
     *
     * @param expressions An array of expressions.
     */
    private static void assertExpressionExceptionOnAll(String[] expressions) {
        for (String expr : expressions) {
            try {
                // Act
                Postfix.evaluate(expr);
                fail("Expected ExpressionException on input: " + expr);
            } catch (Postfix.ExpressionException e) {
                // Exception thrown, all good!
            }
        }
    }
}