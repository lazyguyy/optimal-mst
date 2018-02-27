package util.queue;

import java.util.Collections;

public interface PriorityQueue<T> extends SoftPriorityQueue<T> {
    @Override
    default Iterable<T> corrupted() {
        return Collections.emptyList();
    }
}
