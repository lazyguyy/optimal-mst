package util.queue;

import java.util.Collection;
import java.util.Collections;

public interface PriorityQueue<T> extends SoftPriorityQueue<T> {
    @Override
    default Collection<T> corrupted() {
        return Collections.emptyList();
    }
}
