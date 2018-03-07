package util.queue;

import java.util.Collection;

/**
 *
 * A soft priority queue is a type of priority queue which is allowed to make mistakes.
 * Unlike "normal" priority queues, soft priority queues may associate elements with
 * a different key once they are inserted. Because of these so called corrupted elements,
 * instead of extracting the minimal key, a different element may be returned
 * @param <T> the type of elements to be inserted into the soft priority queue
 */
public interface SoftPriorityQueue<T> {
	/**
	 * Peeks into the top of the queue, returning the element which is believed to have
	 * minimal key. Does not remove the item from the queue.
	 * @return the item with the minimal key
	 */
    T peek();
    /**
     * Peeks into the top of the queue, returning the element which is believed to have
     * minimal key. The item will get removed from the queue in the process.
     * @return the item with the minimal key
     */
    T pop();
    /**
     * Returns a {@link Collection} of corrupted elements -- that is, elements which are associated
     * with the wrong key.
     * @return a {@link Collection} of corrupted elements
     */
    Collection<T> corrupted();
    /**
     * Inserts an element into the queue. The item or other items may be corrupted in the process.
     * @param element the element to be inserted
     */
    void insert(T element);
    /**
     * Returns the size of the queue.
     * @return the size of the queue
     */
    int size();
    /**
     * Returns whether the queue is empty.
     * @return true if the queue is empty
     */
    default boolean empty() {
        return size() == 0;
    }
}
