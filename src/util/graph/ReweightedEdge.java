package util.graph;

import java.util.Objects;

public final class ReweightedEdge extends WeightedEdge {
    private final double uniqueWeight;

    public ReweightedEdge(final int from, final int to, final double weight, final double uniqueWeight) {
        super(from, to, weight);
        this.uniqueWeight = uniqueWeight;
    }

    @Override
    public double uniqueWeight() {
        return uniqueWeight;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass() || !super.equals(o)) return false;
        ReweightedEdge that = (ReweightedEdge) o;
        return Double.compare(that.uniqueWeight, uniqueWeight) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), uniqueWeight);
    }

    @Override
    public String toString() {
        return String.format("ReweightedEdge(%s, %s, w=%s, uw=%s)", from, to, weight, uniqueWeight);
    }
}
