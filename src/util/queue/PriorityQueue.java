package util.queue;

import java.util.Collections;
import java.util.Collection;

public interface PriorityQueue<T> extends SoftPriorityQueue<T> {
    @Override
    default Collection<T> corrupted() {
        return Collections.emptyList();
    }
}
