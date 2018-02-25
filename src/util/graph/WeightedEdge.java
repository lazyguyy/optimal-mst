package util.graph;

import java.util.Objects;

public class WeightedEdge implements Comparable<WeightedEdge> {
    public final int from;
    public final int to;
    public final double weight;

    public WeightedEdge(int from, int to, double weight) {
        this.from = from;
        this.to = to;
        this.weight = weight;
    }

    public static WeightedEdge unweighted(int from, int to) {
        return new WeightedEdge(from, to, 1);
    }

    public WeightedEdge reversed() {
        return new WeightedEdge(to, from, weight);
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
    public int compareTo(WeightedEdge other) {
        return Double.compare(weight, other.weight);
    }
}
