package util.queue;

public interface PriorityQueue<T> extends SoftPriorityQueue<T> {
    void insert(T element);

    @Override
    default boolean insertLossy(T element) {
        insert(element);
        return true;
    }
}
