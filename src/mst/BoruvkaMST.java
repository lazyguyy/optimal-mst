package mst;

import util.graph.Graph;
import util.graph.edge.ContractedEdge;
import util.graph.EdgeList;
import util.graph.Graphs;
import util.graph.edge.DirectedEdge;

import java.util.*;

public final class BoruvkaMST {

    public static <E extends DirectedEdge<E> & Comparable<? super E>>
            EdgeList<E> compute(int vertices, Iterable<E> edges) {
        ArrayList<ContractedEdge<E>> wrapper = new ArrayList<>();
        for (E e : edges)
            wrapper.add(new ContractedEdge<>(e));
        return recurse(vertices, wrapper);
    }

    private static <E extends DirectedEdge<E> & Comparable<? super E>>
            EdgeList<E> recurse(int vertices, Iterable<ContractedEdge<E>> edges) {

        if (vertices < 2)
            return new EdgeList<>();

        Set<ContractedEdge<E>> forestEdges = Graphs.lightestEdgePerVertex(vertices, edges);
        Graph<ContractedEdge<E>> contracted = Graphs.contract(vertices, forestEdges, edges);

        // extract original edges
        EdgeList<E> markedEdges = new EdgeList<>();
        forestEdges.stream().map(e -> e.original).forEach(markedEdges::append);

        markedEdges.meld(recurse(contracted.vertices, contracted.edges));
        return markedEdges;
    }
}
