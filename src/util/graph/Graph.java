package util.graph;

import util.graph.edge.DirectedEdge;
/**
 * 
 * Represents a Graph as a number of vertices and an {@link EdgeList}
 * @param <E> the type of edge in the graph
 */
public class Graph<E extends DirectedEdge<?, E>> {
    /**
     * The number of vertices in the graph
     */
    public final int vertices;
    /**
     * The {@link EdgeList} of edges in the graph
     */
    public final EdgeList<E> edges;

    /**
     * Creates a new Graph with the given number of vertices and the {@link EdgeList}
     * @param vertices the number of vertices in the graph
     * @param edges the {@link EdgeList} of edges
     */
    public Graph(int vertices, EdgeList<E> edges) {
        this.vertices = vertices;
        this.edges = edges;
    }

    @Override
    public String toString() {
        return String.format("Graph(vertices=%s, edges=\n%s)", vertices, edges);
    }
}
