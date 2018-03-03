package util.graph;

import util.graph.edge.DirectedEdge;

@FunctionalInterface
public interface MinimumSpanningTreeAlgorithm<E extends DirectedEdge<?, E>> {
    EdgeList<E> findMST(int vertices, Iterable<E> edges);
}
