package util.queue;

import java.util.Collections;

public interface PriorityQueue<T> extends LossyPriorityQueue<T> {
    void delete(T element);

    @Override
    default T pop() {
        T element = peek();
        delete(element);
        return element;
    }

    @Override
    default Iterable<T> corrupted() {
        return Collections.emptyList();
    }
}
