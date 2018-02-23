package util;

public interface SoftPriorityQueue<T> {
    void delete(T element);
    T extractMin();
    boolean insertLossy(T element);
}
