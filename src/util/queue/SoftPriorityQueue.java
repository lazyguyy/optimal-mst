package util.queue;

public interface SoftPriorityQueue<T> {
    T peek();
    T pop();
    Iterable<T> corrupted();
    void insert(T element);
    int size();
    default boolean empty() {
        return size() == 0;
    }
}
