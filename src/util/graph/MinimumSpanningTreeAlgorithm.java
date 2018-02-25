package util.graph;

@FunctionalInterface
public interface MinimumSpanningTreeAlgorithm {
    EdgeList findMST(int vertices, Iterable<WeightedEdge> edges);
}
