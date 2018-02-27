package mst;

import util.graph.edge.ContractedEdge;
import util.graph.EdgeList;
import util.graph.Graphs;
import util.graph.edge.WeightedEdge;

import java.util.*;

public final class BoruvkaMST {

    public static EdgeList compute(int vertices, Iterable<WeightedEdge> edges) {
        ArrayList<ContractedEdge> wrapper = new ArrayList<>();
        for (WeightedEdge e : edges)
            wrapper.add(new ContractedEdge(e));
        return recurse(vertices, wrapper);
    }

    private static EdgeList<WeightedEdge> recurse(int vertices, Iterable<ContractedEdge> edges) {

        if (vertices < 2)
            return new EdgeList<>();

        Set<ContractedEdge> forestEdges = Graphs.lightestEdgePerVertex(vertices, edges);
        Graphs.ContractedWrapper contracted = Graphs.contract(vertices, forestEdges, edges);

        // extract original edges
        EdgeList<WeightedEdge> markedEdges = new EdgeList<>();
        forestEdges.stream().map(e -> e.original).forEach(markedEdges::append);

        markedEdges.meld(recurse(contracted.size, contracted.edges));
        return markedEdges;
    }
}
