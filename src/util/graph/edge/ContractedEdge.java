package util.graph.edge;

import java.util.Objects;

public final class ContractedEdge implements DirectedEdge<ContractedEdge>, Comparable<ContractedEdge> {
    private final int from;
    private final int to;
    public final WeightedEdge original;

    public ContractedEdge(final WeightedEdge original) {
        this.from = original.from();
        this.to = original.to();
        this.original = original;
    }

    public ContractedEdge(final int from, final int to, final WeightedEdge original) {
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
    public ContractedEdge reversed() {
        return new ContractedEdge(to, from, original);
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
        ContractedEdge that = (ContractedEdge) o;
        return from == that.from && to == that.to && original == that.original;
    }

    @Override
    public int hashCode() {
        return Objects.hash(from, to, original);
    }

    @Override
    public int compareTo(final ContractedEdge other) {
        return original.compareTo(other.original);
    }
}
