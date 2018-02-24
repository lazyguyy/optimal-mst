package util;

import java.util.*;

public class KAryHeap<T extends Comparable<? super T>> implements ExtendedPriorityQueue<T> {

    private final List<T> tree;
    private final Map<T, Integer> elementIndex;
    public final int K;

    public KAryHeap(final int k) {
        this(k, new ArrayList<>(), new HashMap<>());
    }

    private KAryHeap(final int k, final List<T> baseList, final Map<T, Integer> baseMap) {
        if (k <= 0)
            throw new IllegalArgumentException("K must be positive.");
        baseList.clear();
        baseMap.clear();
        K = k;
        tree = baseList;
        elementIndex = baseMap;
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

    private int size() {
        return tree.size();
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
        if (get(parent).compareTo(get(index)) > 0) {
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
            if (get(i).compareTo(get(smallest)) < 0)
                smallest = i;

        if (get(index).compareTo(get(smallest)) > 0) {
            swap(smallest, index);
            siftDown(smallest);
        }
    }

    @Override
    public void decrease(final T element, final T replacement) {
        int pos = elementIndex.get(element);
        if (get(pos).compareTo(replacement) < 0)
            throw new IllegalArgumentException("New key must be lower.");

        emplace(pos, replacement);
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
    public T extractMin() {
        if (size() == 0)
            throw new NoSuchElementException("Heap is empty.");
        return remove(0);
    }
}
