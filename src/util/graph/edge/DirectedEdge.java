package util.graph.edge;

public interface DirectedEdge<R extends DirectedEdge<R>> {
    int from();
    int to();
    double weight();
    R reversed();
}
