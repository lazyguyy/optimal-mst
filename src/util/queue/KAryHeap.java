package util.queue;

import java.util.*;

public class KAryHeap<T> implements ExtendedPriorityQueue<T> {

    private final List<T> tree;
    private final Map<T, Integer> elementIndex;
    private final Comparator<? super T> comparator;
    public final int K;

    public KAryHeap(final int k, final Comparator<? super T> c) {
        this(k, c, new ArrayList<>(), new HashMap<>());
    }

    private KAryHeap(final int k, final Comparator<? super T> c, final List<T> baseList, final Map<T, Integer> baseMap) {
        if (k <= 0)
            throw new IllegalArgumentException("K must be positive.");
        baseList.clear();
        baseMap.clear();
        K = k;
        comparator = c;
        tree = baseList;
        elementIndex = baseMap;
    }

    public static <S extends Comparable<? super S>> KAryHeap<S> naturallyOrdered(final int k) {
        return new KAryHeap<>(k, S::compareTo);
    }

    public int size() {
        return tree.size();
    }

    private T get(final int index) {
        return tree.get(index);
    }

    private void emplace(final int index, final T value) {
        tree.set(index, value);
        elementIndex.put(value, index);
    }

    private T remove(final int index) {
        T element = get(index);
        // replace element with last entry
        emplace(index, get(size() - 1));
        tree.remove(size() - 1);
        elementIndex.remove(element);
        // return last entry to the bottom
        siftDown(index);
        return element;
    }

    private void swap(final int i, final int j) {
        final T temp = get(i);
        emplace(i, get(j));
        emplace(j, temp);
    }

    private void siftUp(final int index) {
        if (index == 0)
            return;
        int parent = (index - 1) / K;
        if (comparator.compare(get(parent), get(index)) > 0) {
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
            if (comparator.compare(get(i), get(smallest)) < 0)
                smallest = i;

        if (comparator.compare(get(index), get(smallest)) > 0) {
            swap(smallest, index);
            siftDown(smallest);
        }
    }

    @Override
    public void decrease(final T element) {
        int pos = elementIndex.get(element);
        siftUp(pos);
    }

    @Override
    public void insert(final T element) {
        tree.add(null);
        emplace(size() - 1, element);
        siftUp(size() - 1);
    }

    @Override
    public void delete(final T element) {
        int pos = elementIndex.get(element);
        remove(pos);
    }

    @Override
    public T peek() {
        if (size() == 0)
            throw new NoSuchElementException("Heap is empty.");
        return get(0);
    }

    @Override
    public T pop() {
        if (size() == 0)
            throw new NoSuchElementException("Heap is empty.");
        return remove(0);
    }
}
