import org.junit.Test;
import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.Timeout;
import static org.junit.Assert.*;
import org.junit.Ignore;


import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.CoreMatchers.*;

import java.util.Arrays;
import java.util.EmptyStackException;
import java.util.stream.IntStream;

/**
 * Abstract test class for Stack implementations.
 *
 * Implementing test classes must only implement the getIntegerStack
 * method. Be careful not to override ANY other methods!
 *
 * @author Simon Larsén
 * @author Tobias Hansson
 * @version 2018-12-15
 */
@Ignore
public abstract class StackTest {
    @Rule
    public Timeout globalTimeout = Timeout.seconds(5);

    private Stack<Integer> stack;
    private int[] valuesInStack;
    private int initialStackSize;

    private Stack<Integer> emptyStack;

    @Before
    public void setUp() {
        valuesInStack = new int[] { 3, 4, 1, -123, 4, 1 };
        initialStackSize = valuesInStack.length;
        stack = getIntegerStack();
        pushArrayToStack(valuesInStack, stack);
        emptyStack = getIntegerStack();
    }

    /**
     * Push an array to the stack, in order.
     *
     * @param array An int array.
     * @param stack A Stack.
     */
    private void pushArrayToStack(int[] array, Stack<Integer> stack) {
        for (int i = 0; i < array.length; i++) {
            stack.push(array[i]);
        }
    }

    /**
     * This is the only method that implementing classes need to override.
     *
     * @return An instance of Stack.
     */
    protected abstract Stack<Integer> getIntegerStack();

    @Test
    public void topIsLastPushedValue() {
        // Arrange
        int value = 1338;

        // Act
        emptyStack.push(value);
        stack.push(value);

        int emptyStackTop = emptyStack.top();
        int stackTop = stack.top();

        // Assert
        assertThat(emptyStackTop, equalTo(value));
        assertThat(stackTop, equalTo(value));
    }

    @Test(expected = EmptyStackException.class)
    public void topExceptionWhenStackIsEmpty() {
        emptyStack.top();
    }

    @Test(expected = EmptyStackException.class)
    public void popExceptionWhenStackIsEmpty() {
        emptyStack.pop();
    }

    @Test
    public void popReturnsPushedValuesInReverseOrder() {
        // Arrange
        int lastIndex = valuesInStack.length - 1;
        IntStream
                .range(0, initialStackSize)
                // Act
                .mapToObj(i -> new ResultPair<Integer>(stack.pop(), valuesInStack[lastIndex - i]))
                // Assert
                .forEach(pair -> assertThat(pair.actual, equalTo(pair.expected)));
    }

    @Test
    public void popFiveTimesDecreasesSizeByFive() {
        // Act
        int size = stack.size();
        for (int i = 5; i > 0; i--) {
            stack.pop();
        }
        // Assert
        assertEquals(stack.size(), size - 5);
    }

    @Test
    public void pushFiveTimesIncreasesSizeByFive() {
        // Act
        int size = stack.size();
        for (int i = 5; i > 0; i--) {
            stack.push(1);
        }
        // Assert
        assertEquals(stack.size(), size + 5);
    }

    @Test
    public void stateIsValidWhenPushCalledOnce() {
        // Arrange
        int val = 2;

        // Act
        emptyStack.push(val);

        // Assert
        assertThat(emptyStack.size(), equalTo(1));
        assertThat(emptyStack.top(), equalTo(val));
    }

    @Test
    public void isEmptyIsFalseWhenStackIsNotEmpty() {
        // Act
        boolean stackIsEmpty = stack.isEmpty();
        // Assert
        assertFalse(stackIsEmpty);
    }

    @Test
    public void isEmptyIsTrueWhenStackIsEmpty() {
        // Act
        boolean emptyStackIsEmpty = emptyStack.isEmpty();
        // Assert
        assertTrue(emptyStackIsEmpty);
    }

    @Test
    public void isEmptyIsTrueWhenAllElementsHaveBeenPopped() {
        // Act
        popElements(stack, initialStackSize);
        boolean stackIsEmpty = stack.isEmpty();
        // Assert
        assertTrue(stackIsEmpty);
    }

    @Test
    public void sizeIs0WhenStackIsEmpty() {
        assertEquals(emptyStack.size(), 0); // Tripple A
    }

    @Test
    public void sizeIs0WhenAllElementsHaveBeenPopped() {
        // Act
        popElements(stack, initialStackSize);
        // Assert
        assertEquals(stack.size(), 0);
    }

    // HELPERS

    /**
     * Pops the desired amount of elements.
     *
     * @param stack            A Stack.
     * @param amountOfElements The amount of elements to pop.
     */
    private void popElements(Stack<Integer> stack, int amountOfElements) {
        for (int i = 0; i < amountOfElements; i++) {
            stack.pop();
        }
    }

    /**
     * Class used for stream operations when both actual and expected values
     * need to be gather in conjunction.
     */
    private class ResultPair<T> {
        public final T actual;
        public final T expected;

        public ResultPair(T actual, T expected) {
            this.actual = actual;
            this.expected = expected;
        }
    }
}