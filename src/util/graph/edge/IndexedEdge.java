package util.graph.edge;


import java.util.Objects;

public final class IndexedEdge<E extends DirectedEdge<E> & Comparable<? super E>> implements DirectedEdge<IndexedEdge<E>>, Comparable<IndexedEdge<E>> {
	public final int index;
	public final E edge;

	public IndexedEdge(int index, E edge) {
		this.index = index;
		this.edge = edge;
	}
	
	@Override
	public int compareTo(IndexedEdge<E> other) {
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
	public double weight() {
		return edge.weight();
	}

	@Override
	public IndexedEdge<E> reversed() {
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
		IndexedEdge<?> that = (IndexedEdge<?>) o;
		return index == that.index && edge == that.edge;
	}

	@Override
	public int hashCode() {
		return Objects.hash(index, edge);
	}
}
