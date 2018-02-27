package util.queue;

public interface ExtendedPriorityQueue<T> extends PriorityQueue<T> {
    void decrease(long id);
    long insertWithId(T element);
}
