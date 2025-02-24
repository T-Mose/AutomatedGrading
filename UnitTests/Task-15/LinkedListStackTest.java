/** Tests for the linkedListStack
 * Extends the abstract test class for my implemented linked stack
 * @author Theodor Malmgren
 * @version 2024-02-01
 */
public class LinkedListStackTest extends StackTest {
    @Override
    protected Stack<Integer> getIntegerStack() {
        return new LinkedList<Integer>();
    }
}