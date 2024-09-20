import org.junit.Test;
import org.junit.Before;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.hamcrest.CoreMatchers.equalTo;

public class UnitTests {
 // Place the specific unit tests here for the current weeks assignment
 // Take the already made tests and make more by:
 // plugging the current weeks instructions and the correct answer
 // into ChatGPT (Currently 4o) to generate Junit tests
 Arithmetic arithmetic;
 private static final double epsilon = 1e-6;

 @Before
 public void setUp() {
     arithmetic = new Arithmetic();
 }

 @Test
 public void testSumOfZerosIsZero() {
     assertThat(arithmetic.sum(0, 0), equalTo(0));
 }

 @Test
 public void testSumOfZeroAndOneIsOne() {
     assertThat(arithmetic.sum(0, 1), equalTo(1));
 }

 @Test
 public void testSumOfOneAndZeroIsOne() {
     assertThat(arithmetic.sum(1, 0), equalTo(1));
 }

 @Test
 public void testSumOfNegativeOnes() {
     assertThat(arithmetic.sum(-1, -1), equalTo(-2));
 }

 @Test
 public void testSumOfBigNumbers() {
     assertThat(arithmetic.sum(123456, 789101112), equalTo(123456 + 789101112));
 }

 @Test
 public void testDifferenceWithZeroDoesNothing() {
     for (int n = -10; n <= 10; n++) {
         assertThat(arithmetic.difference(n, 0), equalTo(n));
     }
 }

 @Test
 public void testZeroMinusNEqualsMinusN() {
     for (int n = -10; n <= 10; n++) {
         assertThat(arithmetic.difference(0, n), equalTo(-n));
     }
 }

 @Test
 public void testAverage() {
     for (double n = 0.0; n < 1.0; n += 0.1) {
         for (double m = 0.0; m < 1.0; m += 0.1) {
             double expected = (n+m) / 2;
             double actual = arithmetic.average(n, m);
             double error = Math.abs(expected - actual);
             Boolean errorInBounds = error < epsilon;
             assertThat(errorInBounds, equalTo(true));
         }
     }
 }
}


     