package util.queue;

import java.util.Comparator;

public class BinaryHeap<T> extends KAryHeap<T> {

    public BinaryHeap(final Comparator<? super T> c) {
        super(2, c);
    }

    public static <S extends Comparable<? super S>> BinaryHeap<S> naturallyOrdered() {
        return new BinaryHeap<>(S::compareTo);
    }
}
