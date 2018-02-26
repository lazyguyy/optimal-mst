package mst;

import util.graph.EdgeList;
import util.graph.WeightedEdge;

@FunctionalInterface
public interface MinimumSpanningTreeAlgorithm {
    // TODO FIND "vertices" MANUALLY (Graphs.vertices())?!
    EdgeList<WeightedEdge> findMST(int vertices, Iterable<WeightedEdge> edges);
}
