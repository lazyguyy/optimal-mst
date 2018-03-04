package mst;

import util.graph.EdgeList;
import util.graph.Graph;
import util.graph.Graphs;
import util.graph.edge.ContractedEdge;
import util.graph.edge.DirectedEdge;

import java.util.ArrayList;
import java.util.Set;

public final class BoruvkaMST {

    public static <T, E extends DirectedEdge<T, E> & Comparable<? super E>>
            EdgeList<E> compute(int vertices, Iterable<E> edges) {
        ArrayList<ContractedEdge<T, E>> wrapper = new ArrayList<>();
        for (E e : edges)
            wrapper.add(new ContractedEdge<>(e));
        return recurse(vertices, wrapper);
    }

    private static <T, E extends DirectedEdge<T, E> & Comparable<? super E>>
            EdgeList<E> recurse(int vertices, Iterable<ContractedEdge<T, E>> edges) {

        if (vertices < 2)
            return new EdgeList<>();

        Set<ContractedEdge<T, E>> forestEdges = Graphs.lightestEdgePerVertex(vertices, edges);
        Graph<ContractedEdge<T, ContractedEdge<T, E>>> contracted = Graphs.contract(vertices, forestEdges, edges);
        EdgeList<ContractedEdge<T, E>> contractedEdges = Graphs.flatten(contracted.edges);

        // extract original edges
        EdgeList<E> markedEdges = new EdgeList<>();
        forestEdges.stream().map(e -> e.original).forEach(markedEdges::append);

        markedEdges.meld(recurse(contracted.vertices, contractedEdges));
        return markedEdges;
    }
}
