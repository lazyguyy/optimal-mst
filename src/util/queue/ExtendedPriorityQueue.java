package util.queue;

public interface ExtendedPriorityQueue<T> extends PriorityQueue<T> {
    void decrease(T element);
}
