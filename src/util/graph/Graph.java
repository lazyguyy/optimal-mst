package util.graph;

import util.graph.edge.DirectedEdge;

public class Graph<E extends DirectedEdge<?, E>> {
    public final int vertices;
    public final EdgeList<E> edges;

    public Graph(int vertices, EdgeList<E> edges) {
        this.vertices = vertices;
        this.edges = edges;
    }

    @Override
    public String toString() {
        return String.format("Graph(vertices=%s, edges=\n%s)", vertices, edges);
    }
}
