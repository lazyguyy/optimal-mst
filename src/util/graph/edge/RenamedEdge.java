package util.graph.edge;

/**
 * 
 * An implementation of {@link AbstractRenamedEdge}
 * @param <T> the weight type of this edge
 * @param <E> the edge type of the edge that this edge references
 */
public final class RenamedEdge<T, E extends DirectedEdge<T, E> & Comparable<? super E>> extends AbstractRenamedEdge<T, E, RenamedEdge<T, E>> {

	/**
     * Creates a new abstract renamed edge with new {@link from}, {@link to}, internally referencing the original edge.
     * @param from the new from
     * @param to the new to
     * @param original the edge that this new edge should reference
     */
    public RenamedEdge(final int from, final int to, final E original) {
        super(from, to, original);
    }

    @Override
    public RenamedEdge<T, E> reversed() {
        return new RenamedEdge<>(to, from, original);
    }

    @Override
    public String toString() {
        return representation("RenamedEdge");
    }
}
