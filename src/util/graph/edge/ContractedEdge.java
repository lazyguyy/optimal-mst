package util.graph.edge;

public final class ContractedEdge<E extends DirectedEdge<E> & Comparable<? super E>> extends AbstractRenamedEdge<E, ContractedEdge<E>> implements Comparable<ContractedEdge<E>> {

    public ContractedEdge(final E original) {
        super(original.from(), original.to(), original);
    }

    public ContractedEdge(final int from, final int to, final E original) {
        super(from, to, original);
    }

    @Override
    public ContractedEdge<E> reversed() {
        return new ContractedEdge<>(to, from, original);
    }

    @Override
    public String toString() {
        return representation("ContractedEdge");
    }

    @Override
    public int compareTo(final ContractedEdge<E> other) {
        return original.compareTo(other.original);
    }
}
