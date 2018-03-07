package util.graph.edge;

import java.util.Objects;

/**
 * 
 * Edges in a subgraph may need to be renamed. For example, imagine that you are contracting certain sets of vertices
 * of a graph. The new graph keeps all edges between the individual components, but the contracted edges link between different
 * vertices. This behavior is reflected in this class. <br>
 * There are multiple implementations of this class that do the same thing and could be used interchangeably.
 * The reason for this is to give the edge variables that are used throughout the mst algorithms a semantic value, so that
 * it is easier to understand what the purpose of a given edge in the algorithm is.
 * @param <T> the weight type of this edge
 * @param <E> the type of edge that this edge internally references
 * @param <R> the edge type of this edge
 */
abstract class AbstractRenamedEdge<T, E extends DirectedEdge<T, E> & Comparable<? super E>, R extends AbstractRenamedEdge<T, E, R>> implements DirectedEdge<T, R>, Comparable<R> {
    protected final int from;
    protected final int to;
    public final E original;

    /**
     * Creates a new abstract renamed edge with new {@link from}, {@link to}, internally referencing the original edge.
     * @param from the new from
     * @param to the new to
     * @param original the edge that this new edge should reference
     */
    AbstractRenamedEdge(final int from, final int to, final E original) {
        this.from = from;
        this.to = to;
        this.original = original;
    }

    @Override
    public int from() {
        return from;
    }

    @Override
    public int to() {
        return to;
    }

    @Override
    public T weight() {
        return original.weight();
    }

    /**
     * Return a {@link String} representation of this edge.
     * @param name the name of this edge
     * @return a {@link String} representation of this edge
     */
    String representation(final String name) {
        return String.format("%s(%s, %s, original=%s)", name, from, to, original);
    }

    @Override
    public String toString() {
        return representation("AbstractRenamedEdge");
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AbstractRenamedEdge<?, ?, ?> that = (AbstractRenamedEdge<?, ?, ?>) o;
        return from == that.from && to == that.to && original == that.original;
    }

    @Override
    public int hashCode() {
        return Objects.hash(from, to, original);
    }


    @Override
    public int compareTo(final R other) {
        return original.compareTo(other.original);
    }
}
