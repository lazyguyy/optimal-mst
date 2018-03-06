package util.queue;

import java.util.Collection;
import java.util.Collections;

/**
 * 
 * In contrast to soft priority queues, "standard" priority queues have to be exact.
 * That means, that at any given point the number of corrupted elements has to be zero.
 * @param <T> the type of the elements to be stored in the priority queue
 */
public interface PriorityQueue<T> extends SoftPriorityQueue<T> {
    @Override
    default Collection<T> corrupted() {
        return Collections.emptyList();
    }
}
