package util.queue;

import java.util.Collection;

public interface SoftPriorityQueue<T> {
    T peek();
    T pop();
    Collection<T> corrupted();
    void insert(T element);
    int size();
    default boolean empty() {
        return size() == 0;
    }
}
