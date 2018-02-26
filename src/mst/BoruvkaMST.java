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
            wrapper.add(new ContractedEdge(e.from, e.to, e));
        return recurse(vertices, wrapper);
    }

    private static EdgeList recurse(int vertices, Iterable<ContractedEdge> edges) {

        if (vertices < 2)
            return new EdgeList();

        // TODO guarantee different edge weights!
        ContractedEdge[] lightest = new ContractedEdge[vertices];

        // store lightest edge for each vertex
        for (ContractedEdge ce : edges) {
            // vertex numbers correspond to those in the original graph, so we have to map them to their component
            if (lightest[ce.from] == null || lightest[ce.from].original.weight < ce.original.weight)
                lightest[ce.from] = ce;
            if (lightest[ce.to] == null || lightest[ce.to].original.weight < ce.original.weight)
                lightest[ce.to] = ce;
        }

        HashSet<ContractedEdge> forestEdges = new HashSet<>();
        for (ContractedEdge e : lightest) {
            if (e == null)
                continue;
            forestEdges.add(e);
        }

        // find connected components
        int[] component = Graphs.components(vertices, forestEdges);

        int componentCount = Arrays.stream(component).max().orElse(-1) + 1;

        ArrayList<ContractedEdge> remainingEdges = new ArrayList<>();
        for (ContractedEdge ce : edges) {
            if (component[ce.from] == component[ce.to])
                continue;
            remainingEdges.add(new ContractedEdge(component[ce.from], component[ce.to], ce.original));
        }

        // TODO remove duplicates

        EdgeList markedEdges = new EdgeList(() -> forestEdges.stream().map(e -> e.original).iterator());
        return recurse(componentCount, remainingEdges).meld(markedEdges);
    }
}
