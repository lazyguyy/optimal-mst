package util.graph.edge;


public final class IndexedEdge<E extends DirectedEdge<E> & Comparable<? super E>> implements DirectedEdge<IndexedEdge<E>>, Comparable<IndexedEdge<E>> {
	public final int index;
	public final E edge;
	
	// TODO: Implement equals and hashcode
	
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
		return new IndexedEdge<E>(index, edge.reversed());
	}
}
