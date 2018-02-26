package util.queue;

// TODO rename to "corrupting"
public interface LossyPriorityQueue<T> {
    T peek();
    T pop();
    Iterable<T> corrupted();
    void insert(T element);
    int size();
    default boolean empty() {
        return size() == 0;
    }
}
