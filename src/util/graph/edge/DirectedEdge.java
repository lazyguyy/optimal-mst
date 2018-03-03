package util.graph.edge;

public interface DirectedEdge<T, R extends DirectedEdge<T, R>> {
    int from();
    int to();
    T weight();
    R reversed();
}
