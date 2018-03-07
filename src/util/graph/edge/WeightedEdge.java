package util.graph.edge;

import java.util.Objects;

/**
 * 
 * Most basic implementation of the {@ink DirectedEdge} interface.
 * @param <T> the weight type of this edge.
 */
public final class WeightedEdge<T extends Comparable<? super T>> implements DirectedEdge<T, WeightedEdge<T>>, Comparable<WeightedEdge<T>> {
    private final int from;
    private final int to;
    private final T weight;

    /**
     * Create a new weighted edge from from to to with the given weight
     * @param from the vertex where this edge starts
     * @param to the vertex on which this edge ends
     * @param weight the weight of the edge
     */
    public WeightedEdge(final int from, final int to, final T weight) {
        this.from = from;
        this.to = to;
        this.weight = weight;
    }

    /**
     * Creates a new edge linking the same two vertices with the given weight.
     * @param weight the weight of the new edge
     * @return a new edge linking the same two vertices with the given weight
     */
    public WeightedEdge<T> reweighted(final T weight) {
        return new WeightedEdge<>(from, to, weight);
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
