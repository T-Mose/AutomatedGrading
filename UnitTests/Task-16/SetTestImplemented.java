public class SetTestImplemented extends SetTest {

    @Override
    protected Set<Integer> getIntegerSet(int minCapacity) {
        return new HashSet<>(minCapacity);
    }   
}