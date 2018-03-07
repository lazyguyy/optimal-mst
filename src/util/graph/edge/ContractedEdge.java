package util.graph.edge;

/**
 * 
 * An implementation of {@link AbstractRenamedEdge}, which represents contracted edges.
 * @param <T> the weight type of this edge
 * @param <E> the edge type of the edge that this edge references
 */
public final class ContractedEdge<T, E extends DirectedEdge<T, E> & Comparable<? super E>> extends AbstractRenamedEdge<T, E, ContractedEdge<T, E>> {

	/**
	 * Creates a new contracted edge that keeps the start and end indices from the original edge.
	 * @param original the original edge that we would like to contract
	 */
    public ContractedEdge(final E original) {
        super(original.from(), original.to(), original);
    }

    /**
     * Creates a new contracted edge that connects the vertices from and to and internally references the original edge. 
     * @param from the from index of the contracted edge
     * @param to the to index of the contracted edge
     * @param original the original edge that should be referenced
     */
    public ContractedEdge(final int from, final int to, final E original) {
        super(from, to, original);
    }

    @Override
    public ContractedEdge<T, E> reversed() {
        return new ContractedEdge<>(to, from, original);
    }

    @Override
    public String toString() {
        return representation("ContractedEdge");
    }
}
