package util.graph.edge;

import java.util.Objects;

public final class WeightedEdge<T extends Comparable<? super T>> implements DirectedEdge<T, WeightedEdge<T>>, Comparable<WeightedEdge<T>> {
    private final int from;
    private final int to;
    private final T weight;

    public WeightedEdge(final int from, final int to, final T weight) {
        this.from = from;
        this.to = to;
        this.weight = weight;
    }

    public WeightedEdge<T> reweighted(final T weight) {
        return new WeightedEdge<>(from, to, weight);
    }

    public int from() {
        return from;
    }

    public int to() {
        return to;
    }

    public WeightedEdge<T> reversed() {
        return new WeightedEdge<>(to, from, weight);
    }

    @Override
    public T weight() {
        return weight;
    }

    @Override
    public String toString() {
        return String.format("Edge(%s, %s, w=%s)", from, to, weight);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WeightedEdge<?> that = (WeightedEdge<?>) o;
        return from == that.from && to == that.to && weight == that.weight;
    }

    @Override
    public int hashCode() {
        return Objects.hash(from, to, weight);
    }

    @Override
    public int compareTo(final WeightedEdge<T> other) {
        int byWeight = weight.compareTo(other.weight);
        if (byWeight != 0)
            return byWeight;
        int byFrom = Integer.compare(from, other.from);
        if (byFrom != 0)
            return byFrom;
        return Integer.compare(to, other.to);
    }
}
