package util.queue;

import java.util.*;

public class KAryHeap<T> implements ExtendedPriorityQueue<T> {

    private final List<Node<T>> tree;
    private final Map<Long, Integer> idToIndex;
    private final Comparator<? super T> comparator;
    private final int K;
    private long nextId = 0;

    public KAryHeap(final int k, final Comparator<? super T> c) {
        if (k <= 0)
            throw new IllegalArgumentException("K must be positive.");
        K = k;
        comparator = c;
        tree = new ArrayList<>();
        idToIndex = new HashMap<>();
    }

    public static <S extends Comparable<? super S>> KAryHeap<S> naturallyOrdered(final int k) {
        return new KAryHeap<>(k, S::compareTo);
    }

    public int size() {
        return tree.size();
    }

    private Node<T> get(final int index) {
        return tree.get(index);
    }

    private T remove(final int index) {
        final int last = size() - 1;
        T element = get(index).element;
        // replace element with last entry
        swap(index, last);
        if (get(last).id != - 1)
            idToIndex.remove(get(last).id);
        tree.remove(last);
        // return swapped entry to the bottom
        siftDown(index);
        return element;
    }

    private void emplace(final Node<T> node) {
        tree.add(null);
        emplace(size() - 1, node);
    }

    private void emplace(final int index, final Node<T> node) {
        tree.set(index, node);
        if (node.id != -1)
            idToIndex.put(node.id, index);
    }

    private void swap(final int i, final int j) {
        final Node<T> temp = get(i);
        emplace(i, get(j));
        emplace(j, temp);
    }

    private void siftUp(final int index) {
        if (index == 0)
            return;
        int parent = (index - 1) / K;
        if (comparator.compare(get(parent).element, get(index).element) > 0) {
            swap(index, parent);
            siftUp(parent);
        }
    }

    private void siftDown(final int index) {
        int leftmost = K * index + 1;
        if (leftmost >= size())
            return;

        int smallest = leftmost;
        int bound = Math.min(leftmost + K, size());
        for (int i = leftmost + 1; i < bound; i++)
            if (comparator.compare(get(i).element, get(smallest).element) < 0)
                smallest = i;

        if (comparator.compare(get(index).element, get(smallest).element) > 0) {
            swap(smallest, index);
            siftDown(smallest);
        }
    }

    @Override
    public void decrease(final long id) {
        if (!idToIndex.containsKey(id))
            throw new NoSuchElementException("Invalid identifier.");
        int pos = idToIndex.get(id);
        siftUp(pos);
    }

    @Override
    public void insert(final T element) {
        emplace(new Node<>(element, -1L));
        siftUp(size() - 1);
    }

    @Override
    public long insertWithId(final T element) {
        long id = nextId++;
        emplace(new Node<>(element, id));
        siftUp(size() - 1);
        return id;
    }

    @Override
    public T peek() {
        if (size() == 0)
            throw new NoSuchElementException("Heap is empty.");
        return get(0).element;
    }

    @Override
    public T pop() {
        if (size() == 0)
            throw new NoSuchElementException("Heap is empty.");
        return remove(0);
    }

    private static final class Node<T> {
        final T element;
        final long id;

        Node(T element, long id) {
            this.element = element;
            this.id = id;
        }
    }
}
