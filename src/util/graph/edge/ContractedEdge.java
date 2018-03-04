package util.graph.edge;

public final class ContractedEdge<T, E extends DirectedEdge<T, E> & Comparable<? super E>> extends AbstractRenamedEdge<T, E, ContractedEdge<T, E>> implements Comparable<ContractedEdge<T, E>> {

    public ContractedEdge(final E original) {
        super(original.from(), original.to(), original);
    }

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

    @Override
    public int compareTo(final ContractedEdge<T, E> other) {
        return original.compareTo(other.original);
    }
}
