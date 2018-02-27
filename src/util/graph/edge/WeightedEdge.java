package util.graph.edge;

import java.util.Objects;

public final class WeightedEdge implements DirectedEdge<WeightedEdge>, Comparable<WeightedEdge> {
    private final int from;
    private final int to;
    private final double weight;

    public WeightedEdge(final int from, final int to, final double weight) {
        this.from = from;
        this.to = to;
        this.weight = weight;
    }

    public int from() {
        return from;
    }

    public int to() {
        return to;
    }

    public WeightedEdge reversed() {
        return new WeightedEdge(to, from, weight);
    }

    @Override
    public double weight() {
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
        WeightedEdge that = (WeightedEdge) o;
        return from == that.from && to == that.to && Double.compare(that.weight, weight) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(from, to, weight);
    }

    @Override
    public int compareTo(final WeightedEdge other) {
        int byWeight = Double.compare(weight, other.weight);
        if (byWeight != 0)
            return byWeight;
        int byFrom = Integer.compare(from, other.from);
        if (byFrom != 0)
            return byFrom;
        return Integer.compare(to, other.to);
    }

}
