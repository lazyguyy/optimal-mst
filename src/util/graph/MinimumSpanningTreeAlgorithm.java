package util.graph;

import util.graph.edge.WeightedEdge;

@FunctionalInterface
public interface MinimumSpanningTreeAlgorithm {
    EdgeList<WeightedEdge> findMST(int vertices, Iterable<WeightedEdge> edges);
}
