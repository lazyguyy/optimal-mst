package util;

public interface ExtendedPriorityQueue<T> extends PriorityQueue<T> {
    void decrease(T element, T replacement);
}
