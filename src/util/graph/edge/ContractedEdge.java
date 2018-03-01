package util.graph.edge;

import java.util.Objects;

public final class ContractedEdge<E extends DirectedEdge<E> & Comparable<? super E>> implements DirectedEdge<ContractedEdge<E>>, Comparable<ContractedEdge<E>> {
    private final int from;
    private final int to;
    public final E original;

    public ContractedEdge(final E original) {
        this.from = original.from();
        this.to = original.to();
        this.original = original;
    }

    public ContractedEdge(final int from, final int to, final E original) {
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
    public ContractedEdge<E> reversed() {
        return new ContractedEdge<>(to, from, original);
    }

    @Override
    public double weight() {
        return original.weight();
    }

    @Override
    public String toString() {
        return String.format("ContractedEdge(%s, %s, original=%s)", from, to, original);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ContractedEdge<?> that = (ContractedEdge<?>) o;
        return from == that.from && to == that.to && original == that.original;
    }

    @Override
    public int hashCode() {
        return Objects.hash(from, to, original);
    }

    @Override
    public int compareTo(final ContractedEdge<E> other) {
        return original.compareTo(other.original);
    }
}
