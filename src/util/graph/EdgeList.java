package util.graph;

import util.graph.edge.DirectedEdge;
import util.queue.Meldable;

import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * A linked list of edges for a graph that supports concatenation in O(1).
 * @param <E> the edge type of the graph
 */
public final class EdgeList<E extends DirectedEdge<?, E>> implements Meldable<EdgeList<E>>, Iterable<E> {

    private Node<E> first;
    private Node<E> last;
    private int size = 0;

    /**
     * Creates an empty {@link EdgeList}
     */
    public EdgeList() {}

    /**
     * Takes another {@link EdgeList} an appends it to this one
     * @param other the {@link EdgeList} to be appended
     */
    public EdgeList(Iterable<? extends E> other) {
        other.forEach(this::append);
    }

    @Override
    public void meld(final EdgeList<E> other) {
        if (other == null)
            throw new NullPointerException("Attempting to meld null.");

        if (last != null)
            last.next = other.first;
        if (other.first != null)
            other.first.prev = this.last;

        first = size > 0 ? first : other.first;
        last = other.size > 0 ? other.last : last;

        size += other.size;
        other.clear();
    }

    /**
     * Returns the number of edges in this {@link EdgeList}.
     * @return the number of edges in this {@link EdgeList}
     */
    public int size() {
        return size;
    }

    /**
     * Takes an edge and appends it to this {@link EdgeList}.
     * @param edge the edge to be appended to this {@link EdgeList}
     */
    public void append(final E edge) {
        checkNotNull(edge);

        Node<E> node = new Node<>(edge, last, null);
        if (last != null)
            last.next = node;

        if (first == null)
            first = node;

        last = node;
        size++;
    }

    /**
     * Takes an edge and prepends it to this {@link EdgeList}.
     * @param edge the edge to be prepended to this {@link EdgeList}
     */
    public void prepend(final E edge) {
        checkNotNull(edge);

        Node<E> node = new Node<>(edge, null, last);
        if (first != null)
            first.next = node;

        if (last == null)
            last = node;

        first = node;
        size++;
    }

    /**
     * Clears the {@link EdgeList}
     */
    public void clear() {
        first = last = null;
        size = 0;
    }

    /**
     * Returns a {@link Stream} of the edges of this {@link EdgeList}.
     * @return a {@link Stream} of the edges of this {@link EdgeList}
     */
    public Stream<E> stream() {
        return StreamSupport.stream(spliterator(), false);
    }

    /**
     * Creates an array of the edges of this {@link EdgeList} using the provided {@code generator}
     * @param generator a function which produces a new array of the desired type and the provided length
     * @return an array containing the edges of this {@link EdgeList}
     */
    public E[] toArray(IntFunction<E[]> generator) {
        return stream().toArray(generator);
    }

    /**
     * Returns a new {@code Collection} of the elements of this {@link EdgeList} using the provided factory.
     * @param collectionFactory a {@code Supplier} which returns a new, empty {@code Collection} of the appropriate type
     * @param <C> the type of the resulting {@code Collection}
     * @return a {@code Collection} of the elements of this {@link EdgeList}
     */
    public <C extends Collection<E>> C collect(Supplier<C> collectionFactory) {
        return stream().collect(Collectors.toCollection(collectionFactory));
    }

    /**
     * Returns a new {@code Collection} of the elements of this {@link EdgeList} using the provided factory after
     * transforming each element using the provided function.
     * @param mapper the mapping function
     * @param collectionFactory a {@code Supplier} which returns a new, empty {@code Collection} of the appropriate type
     * @param <T> the result type of the mapping function
     * @param <C> the type of the resulting {@code Collection}
     * @return a {@code Collection} containing the mapped elements of this {@link EdgeList}
     */
    public <T, C extends Collection<T>> C mapCollect(Function<E, T> mapper, Supplier<C> collectionFactory) {
        return stream().map(mapper).collect(Collectors.toCollection(collectionFactory));
    }

    /**
     * Transforms the elements of this {@link EdgeList} using the provided function.
     * @param mapper the mapping function
     * @param <T> the result type of the mapping function
     * @return a new {@link EdgeList} containing the mapped elements of this {@link EdgeList}
     */
    public <T extends DirectedEdge<?, T>> EdgeList<T> map(Function<E, T> mapper) {
        EdgeList<T> mapped = new EdgeList<>();
        stream().map(mapper).forEach(mapped::append);
        return mapped;
    }

    /**
     * Returns a string representation of this {@link EdgeList}.
     * @return a string representation of this {@link EdgeList}.
     */
    @Override
    public String toString() {
        return stream()
                .map(E::toString)
                .collect(java.util.stream.Collectors.joining(",\n ", "[", "]"));
    }

    /**
     * Checks if another object is equal to this {@link EdgeList}. Two instances of {@link EdgeList} are equal
     * iff they have the same size and contain the same elements in identical order.
     * @param o object to be tested for equality
     * @return true if the argument is equal to this {@link EdgeList}.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EdgeList<?> other = (EdgeList<?>) o;
        if (size != other.size)
            return false;
        for (Iterator<?> me = iterator(), them = other.iterator(); me.hasNext() && them.hasNext();)
            if (me.next() != them.next())
                return false;
        return true;
    }

    /**
     * Returns an {@code Iterator} over the elements in this {@link EdgeList}.
     * @return an {@code Iterator} over the elements in this {@link EdgeList}.
     */
    @Override
    public Iterator<E> iterator() {
        return new Itr<>(this);
    }

    private static class Itr<E extends DirectedEdge<?, E>> implements Iterator<E> {
        private Node<E> current;

        Itr(EdgeList<E> list) {
            current = list.first;
        }

        @Override
        public boolean hasNext() {
            return current != null;
        }

        @Override
        public E next() {
            if (current == null)
                throw new NoSuchElementException("No more elements.");
            E e = current.edge;
            current = current.next;
            return e;
        }
    }

    /**
     * Checks whether an Edge is null
     * @param edge the edge to be checked for null
     */
    private void checkNotNull(final E edge) {
        if (edge == null) throw new IllegalArgumentException("Edges may not be null.");
    }

    /**
     * This class represents a node of the linked list
     * @param <E> the edge type of the graph
     */
    private static class Node<E extends DirectedEdge<?, E>> {
        final E edge;
        Node<E> prev;
        Node<E> next;

        Node(final E edge) {
            this.edge = edge;
        }

        Node(final E edge, final Node<E> prev, final Node<E> next) {
            this(edge);
            this.prev = prev;
            this.next = next;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Node<?> that = (Node<?>) o;
            return edge == that.edge;
        }
    }
}
