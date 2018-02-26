package mst;

import util.graph.ContractedEdge;
import util.graph.EdgeList;
import util.graph.Graphs;
import util.graph.WeightedEdge;

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

        // TODO guarantee different edge weights!
        ContractedEdge[] lightest = new ContractedEdge[vertices];

        // store lightest edge for each vertex
        for (ContractedEdge ce : edges) {
            // vertex numbers correspond to those in the original graph, so we have to map them to their component
            if (lightest[ce.from()] == null || lightest[ce.from()].compareTo(ce) > 0)
                lightest[ce.from()] = ce;
            if (lightest[ce.to()] == null || lightest[ce.to()].compareTo(ce) > 0)
                lightest[ce.to()] = ce;
        }

        HashSet<ContractedEdge> forestEdges = new HashSet<>();
        for (ContractedEdge e : lightest) {
            if (e == null)
                continue;
            forestEdges.add(e);
        }

        Graphs.ContractedWrapper contracted = Graphs.contract(vertices, forestEdges, edges);

        // extract original edges
        EdgeList<WeightedEdge> markedEdges = new EdgeList<>();
        forestEdges.stream().map(e -> e.original).forEach(markedEdges::append);

        return recurse(contracted.size, contracted.edges).meld(markedEdges);
    }
}
