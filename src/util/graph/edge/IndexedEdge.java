package util.graph.edge;

import java.util.Objects;

/**
 * 
 * A special type of edge that is associated with an index. This is used in the creation of the optimal decision trees
 * as seen in {@link util.decision.PrecomputedMSTCollection}
 * @param <T> the weight type of this edge
 * @param <E> the edge type of the edge that this edge references
 */
public final class IndexedEdge<T, E extends DirectedEdge<T, E> & Comparable<? super E>> implements DirectedEdge<T, IndexedEdge<T, E>>, Comparable<IndexedEdge<T, E>> {
	public final int index;
	public final E edge;

	/**
	 * Creates a new indexed edge with the given index, referencing the given edge
	 * @param index the index of this edge
	 * @param edge the edge this edge references
	 */
	public IndexedEdge(int index, E edge) {
		this.index = index;
		this.edge = edge;
	}
	
	@Override
	public int compareTo(IndexedEdge<T, E> other) {
		return edge.compareTo(other.edge);
	}

	@Override
	public int from() {
		return edge.from();
	}

	@Override
	public int to() {
		return edge.to();
	}

	@Override
	public T weight() {
		return edge.weight();
	}

	@Override
	public IndexedEdge<T, E> reversed() {
		return new IndexedEdge<>(index, edge.reversed());
	}

	@Override
	public String toString() {
		return String.format("IndexedEdge(index=%s, edge=%s)", index, edge);
	}

	@Override
	public boolean equals(final Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		IndexedEdge<?, ?> that = (IndexedEdge<?, ?>) o;
		return index == that.index && edge == that.edge;
	}

	@Override
	public int hashCode() {
		return Objects.hash(index, edge);
	}
}
