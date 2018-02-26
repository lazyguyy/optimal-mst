package util.graph;

import java.util.Objects;

public class WeightedEdge implements Comparable<WeightedEdge> {
    public final int from;
    public final int to;
    public final double weight;

    public WeightedEdge(final int from, final int to) {
        this(from, to, 1);
    }

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

    public double weight() {
        return weight;
    }

    @Override
    public String toString() {
        return String.format("Edge(%s, %s, w=%s)", from, to, weight);
    }

    @Override
    public int hashCode() {
        return Objects.hash(from, to, weight);
    }

    @Override
    public int compareTo(final WeightedEdge other) {
        return Double.compare(weight, other.weight);
    }
}
