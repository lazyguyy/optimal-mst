package util.graph;

import java.util.Objects;

public class ContractedEdge extends WeightedEdge {
    public final WeightedEdge original;

    public ContractedEdge(int from, int to, WeightedEdge original) {
        super(from, to, original.weight);
        this.original = original;
    }

    @Override
    public ContractedEdge reversed() {
        return new ContractedEdge(to, from, original);
    }

    @Override
    public String toString() {
        return String.format("ContractedEdge(%s, %s, original=%s)", from, to, original);
    }

    @Override
    public int hashCode() {
        return Objects.hash(from, to, original);
    }
}
