package util.queue;

public interface SoftPriorityQueue<T> {
    T peek();
    T pop();
    boolean insertLossy(T element);
    void delete(T element);
    int size();
    default boolean empty() {
        return size() == 0;
    }
}
