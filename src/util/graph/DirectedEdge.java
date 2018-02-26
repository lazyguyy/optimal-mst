package util.graph;

public interface DirectedEdge<R extends DirectedEdge> {
    int from();
    int to();
    double weight();
    default double uniqueWeight() {
        return weight();
    }
    R reversed();
}
