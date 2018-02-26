package util.graph;

import util.queue.Meldable;

import java.util.Iterator;
import java.util.NoSuchElementException;

public final class EdgeList implements Meldable<EdgeList>, Iterable<WeightedEdge> {

    private Node first;
    private Node last;
    private int size = 0;

    public EdgeList() {}

    public EdgeList(WeightedEdge edge) {
        append(edge);
    }

    public EdgeList(Iterable<? extends WeightedEdge> other) {
        other.forEach(this::append);
    }

    @Override
    public EdgeList meld(final EdgeList other) {
        if (other == null)
            throw new NullPointerException("Attempting to meld null.");

        EdgeList union = new EdgeList();

        union.first = size > 0 ? first : other.first;
        union.last = other.size > 0 ? other.last : last;

        if (last != null)
            last.next = other.first;
        if (other.first != null)
            other.first.prev = this.last;

        union.size = size + other.size;

        clear();
        other.clear();
        return union;
    }

    public int size() {
        return size;
    }

    public void append(final WeightedEdge edge) {
        checkNotNull(edge);

        Node node = new Node(edge, last, null);
        if (last != null)
            last.next = node;

        if (first == null)
            first = node;

        last = node;
        size++;
    }

    public void prepend(final WeightedEdge edge) {
        checkNotNull(edge);

        Node node = new Node(edge, null, last);
        if (first != null)
            first.next = node;

        if (last == null)
            last = node;

        first = node;
        size++;
    }

    public double weight() {
        double sum = 0;
        for (WeightedEdge e : this)
            sum += e.weight;
        return sum;
    }

    public void clear() {
        first = last = null;
        size = 0;
    }

    @Override
    public String toString() {
        return java.util.stream.StreamSupport.stream(spliterator(), false)
                .map(WeightedEdge::toString)
                .collect(java.util.stream.Collectors.joining("\n ", "[", "]"));
    }

    public WeightedEdge[] toArray() {
        WeightedEdge[] arr = new WeightedEdge[size];
        int i = 0;
        for (WeightedEdge e : this)
            arr[i++] = e;
        return arr;
    }

    @Override
    public Iterator<WeightedEdge> iterator() {
        return new Itr(this);
    }

    private static class Itr implements Iterator<WeightedEdge> {
        Node current;

        Itr(EdgeList list) {
            current = list.first;
        }

        @Override
        public boolean hasNext() {
            return current != null;
        }

        @Override
        public WeightedEdge next() {
            if (current == null)
                throw new NoSuchElementException("No more elements.");
            WeightedEdge e = current.edge;
            current = current.next;
            return e;
        }
    }

    private void checkNotNull(final WeightedEdge edge) {
        if (edge == null) throw new IllegalArgumentException("Edges may not be null.");
    }

    private static class Node {
        final WeightedEdge edge;
        Node prev;
        Node next;

        Node(final WeightedEdge edge) {
            this.edge = edge;
        }

        Node(final WeightedEdge edge, final Node prev, final Node next) {
            this(edge);
            this.prev = prev;
            this.next = next;
        }
    }
}
