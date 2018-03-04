package util.graph.edge;

public final class RenamedEdge<T, E extends DirectedEdge<T, E> & Comparable<? super E>> extends AbstractRenamedEdge<T, E, RenamedEdge<T, E>> implements Comparable<RenamedEdge<T, E>> {

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

    @Override
    public int compareTo(final RenamedEdge<T, E> other) {
        return original.compareTo(other.original);
    }
}
