package util.graph.edge;

import java.util.Objects;

abstract class AbstractRenamedEdge<T, E extends DirectedEdge<T, E> & Comparable<? super E>, R extends AbstractRenamedEdge<T, E, R>> implements DirectedEdge<T, R>, Comparable<R> {
    protected final int from;
    protected final int to;
    public final E original;

    AbstractRenamedEdge(final int from, final int to, final E original) {
        this.from = from;
        this.to = to;
        this.original = original;
    }

    @Override
    public int from() {
        return from;
    }

    @Override
    public int to() {
        return to;
    }

    @Override
    public T weight() {
        return original.weight();
    }

    String representation(final String name) {
        return String.format("%s(%s, %s, original=%s)", name, from, to, original);
    }

    @Override
    public String toString() {
        return representation("AbstractRenamedEdge");
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AbstractRenamedEdge<?, ?, ?> that = (AbstractRenamedEdge<?, ?, ?>) o;
        return from == that.from && to == that.to && original == that.original;
    }

    @Override
    public int hashCode() {
        return Objects.hash(from, to, original);
    }


    @Override
    public int compareTo(final R other) {
        return original.compareTo(other.original);
    }
}
