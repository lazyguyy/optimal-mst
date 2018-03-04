package util.graph;

import util.graph.edge.DirectedEdge;

import java.util.ArrayList;

/**
 * 
 * Represents the AdjacencyList of a Graph.
 * @param <E> the edge type of the graph
 */
public final class AdjacencyList<E extends DirectedEdge<?, E>> extends ArrayList<EdgeList<E>> {

	private static final long serialVersionUID = 1L;

	/**
	 * Creates a new AdjacenyList for a graph with count vertices
	 * @param count the number of vertices in the AdjacencyList
	 */
	public AdjacencyList(int count) {
        super(count);
        for (int i = 0; i < count; i++)
            add(new EdgeList<>());
    }

	/**
	 * Takes a {@link Graph} graph and transforms it into an AdjacencyList
	 * @param <T> the weight type of the edges in the graph
	 * @param <E> the edge type of the edges in the graph
	 * @param g the {@link Graph} to be transformed
	 * @return the AdjacencyList for the given Graph
	 */
    public static <T, E extends DirectedEdge<T, E>> AdjacencyList<E> of(Graph<E> g) {
        return of(g.vertices, g.edges);
    }

    /**
     * 
	 * Takes a graph given by the number of vertices and an {@link Iterable} of edges and transforms it into an AdjacencyList
	 * @param <T> the weight type of the edges in the graph
	 * @param <E> the edge type of the edges in the graph
     * @param vertices the number of vertices
     * @param edges an {@link Iterable} of edges
     * @return the AdjacencyList for the given Graph
     */
    public static <T, E extends DirectedEdge<T, E>> AdjacencyList<E> of(int vertices, Iterable<? extends E> edges) {
        AdjacencyList<E> adjacency = new AdjacencyList<>(vertices);
        for (E e : edges) {
            adjacency.append(e.from(), e);
            adjacency.append(e.to(), e.reversed());
        }
        return adjacency;
    }
    
    /**
     * Returns an {@link EdgeList} of edges for a given vertex.
     * @param vertex specifies the vertex
     * @return returns an {@link EdgeList} of edges for the given vertex 
     */
    public EdgeList<E> get(int vertex) {
        return super.get(vertex);
    }

    /**
     * Appends an edge to the adjacency of a given vertex.
     * @param vertex specifies the vertex
     * @param edge the edge to be appended
     */
    public void append(int vertex, E edge) {
        get(vertex).append(edge);
    }

    @Override
    public String toString() {
        if (size() == 0)
            return "-";
        StringBuilder sb = new StringBuilder();
        for (int v = 0; v < size(); v++)
            sb.append(v).append(": ").append(get(v)).append("\n");
        return sb.toString();
    }
}
