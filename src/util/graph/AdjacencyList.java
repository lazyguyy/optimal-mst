package util.graph;

import util.graph.edge.DirectedEdge;

import java.util.ArrayList;

public final class AdjacencyList<E extends DirectedEdge<?, E>> extends ArrayList<EdgeList<E>> {

	private static final long serialVersionUID = 1L;

	public AdjacencyList(int count) {
        super(count);
        for (int i = 0; i < count; i++)
            add(new EdgeList<>());
    }

    public static <T, E extends DirectedEdge<T, E>> AdjacencyList<E> of(Graph<E> g) {
        return of(g.vertices, g.edges);
    }

    public static <T, E extends DirectedEdge<T, E>> AdjacencyList<E> of(int vertices, Iterable<? extends E> edges) {
        AdjacencyList<E> adjacency = new AdjacencyList<>(vertices);
        for (E e : edges) {
            adjacency.append(e.from(), e);
            adjacency.append(e.to(), e.reversed());
        }
        return adjacency;
    }

    public EdgeList<E> get(int vertex) {
        return super.get(vertex);
    }

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
