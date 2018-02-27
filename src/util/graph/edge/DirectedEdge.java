package util.graph.edge;

public interface DirectedEdge<R extends DirectedEdge> {
    int from();
    int to();
    double weight();
    R reversed();
}
