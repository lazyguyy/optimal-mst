package util.graph.edge;

public final class RenamedEdge<E extends DirectedEdge<E> & Comparable<? super E>> extends AbstractRenamedEdge<E, RenamedEdge<E>> implements Comparable<RenamedEdge<E>> {

    public RenamedEdge(final int from, final int to, final E original) {
        super(from, to, original);
    }

    @Override
    public RenamedEdge<E> reversed() {
        return new RenamedEdge<>(to, from, original);
    }

    @Override
    public String toString() {
        return representation("RenamedEdge");
    }

    @Override
    public int compareTo(final RenamedEdge<E> other) {
        return original.compareTo(other.original);
    }
}
