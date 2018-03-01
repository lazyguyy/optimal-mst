package util.graph.edge;

import java.util.Objects;

public final class RenamedEdge<E extends DirectedEdge<E> & Comparable<? super E>> implements DirectedEdge<RenamedEdge<E>>, Comparable<RenamedEdge<E>> {
    private final int from;
    private final int to;
    public final E original;

    public RenamedEdge(final E original) {
        this.from = original.from();
        this.to = original.to();
        this.original = original;
    }

    public RenamedEdge(final int from, final int to, final E original) {
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
    public RenamedEdge<E> reversed() {
        return new RenamedEdge<E>(to, from, original);
    }

    @Override
    public double weight() {
        return original.weight();
    }

    @Override
    public String toString() {
        return String.format("RenamedEdge(%s, %s, original=%s)", from, to, original);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RenamedEdge<?> that = (RenamedEdge) o;
        return from == that.from && to == that.to && original == that.original;
    }

    @Override
    public int hashCode() {
        return Objects.hash(from, to, original);
    }

    @Override
    public int compareTo(final RenamedEdge<E> other) {
        return original.compareTo(other.original);
    }
}
