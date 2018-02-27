package util.queue;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

public class FibonacciHeap<T> implements ExtendedPriorityQueue<T> {

    private final Map<Long, Integer> idToNode;
    private final Comparator<? super T> comparator;

    private FibonacciHeap(final Comparator<? super T> c) {
        comparator = c;
        idToNode = new HashMap<>();
    }

    public static <S extends Comparable<? super S>> FibonacciHeap<S> naturallyOrdered() {
        return new FibonacciHeap<>(S::compareTo);
    }

    @Override
    public T peek() {
        return null;
    }

    @Override
    public void insert(T element) {

    }

    @Override
    public long insertWithId(T element) {
        return 0;
    }

    @Override
    public int size() {
        return 0;
    }

    @Override
    public T pop() {
        return null;
    }

    @Override
    public void decrease(long id) {

    }

    private static final class Node<E> {

    }
}
