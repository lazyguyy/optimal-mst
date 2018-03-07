package util.graph.edge;

/**
 * 
 * Represents an Edge with generic weight type.
 * @param <T> the weight type of the edge
 * @param <R> the type of edge (only needed for reverse, java generics really do suck)
 */
public interface DirectedEdge<T, R extends DirectedEdge<T, R>> {
	/**
	 * Returns where the edge starts.
	 * @return where the edge starts
	 */
    int from();
    /**
     * Returns where the edge ends.
     * @return where the edge ends
     */
    int to();
    /**
     * Returns the weight of the edge.
     * @return the weight of the edge
     */
    T weight();
    /**
     * Returns a new edge with {@link #from} and {@link #to} swapped.
     * @return a new edge with {@link #from} and {@link #to} swapped
     */
    R reversed();
}
