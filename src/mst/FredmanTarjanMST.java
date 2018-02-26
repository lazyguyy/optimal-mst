package mst;

import util.graph.*;
import util.queue.ExtendedPriorityQueue;
import util.queue.KAryHeap;

import java.util.*;

public final class FredmanTarjanMST {

    public static EdgeList<WeightedEdge> compute(int vertices, Iterable<WeightedEdge> edges) {
        EdgeList<ContractedEdge> wrapper = new EdgeList<>();
        for (WeightedEdge e : edges)
            wrapper.append(new ContractedEdge(e));
        return recurse(vertices, wrapper);
    }

    private static EdgeList<WeightedEdge> recurse(int vertices, EdgeList<ContractedEdge> edges) {

        if (vertices < 2)
            return new EdgeList<>();

        double[] distances = new double[vertices];
        ContractedEdge[] predecessorEdge = new ContractedEdge[vertices];

        for (int i = 0; i < vertices; i++)
            distances[i] = Double.POSITIVE_INFINITY;

        AdjacencyList<ContractedEdge> adjacency = AdjacencyList.of(vertices, edges);

        int edgeCount = edges.size();

        // TODO use fibonacci heaps
        ExtendedPriorityQueue<Integer> queue = new KAryHeap<>(2, Comparator.comparingDouble(i -> distances[i]));

        for (int i = 0; i < vertices; i++)
            queue.insert(i);

        // calculate upper bound for component size
        int exp = 2 * edgeCount / vertices;
        // avoid overflows
        exp = Math.min(63, exp);
        long componentMax = 1 << exp;

        int[] discoveredInIteration = new int[vertices];
        for (int i = 0; i < vertices; i++)
            discoveredInIteration[i] = -1;

        int iterations = 0;

        // grow component trees
        while (!queue.empty()) {

            int componentSize = 0;

            // find arbitrary tree root
            int componentRoot = queue.peek();
            predecessorEdge[componentRoot] = null;

            // grow a single tree
            while (!queue.empty() && componentSize < componentMax) {
                int vertex = queue.pop();

                componentSize++;
                discoveredInIteration[vertex] = iterations;

                // stop if two trees are about to merge
                if (predecessorEdge[vertex] != null) {
                    int predecessor = predecessorEdge[vertex].from();
                    if (discoveredInIteration[predecessor] != discoveredInIteration[vertex])
                        break;
                }

                for (ContractedEdge e : adjacency.get(vertex)) {
                    if (discoveredInIteration[e.to()] == -1 && distances[e.to()] > e.weight()) {
                        distances[e.to()] = e.weight();
                        predecessorEdge[e.to()] = e;
                        queue.decrease(e.to());
                    }
                }
            }
            iterations++;
        }

        // find non-null edges
        HashSet<ContractedEdge> forestEdges = new HashSet<>();
        for (ContractedEdge e : predecessorEdge) {
            if (e == null)
                continue;
            forestEdges.add(e);
        }

        // extract original edges
        EdgeList<WeightedEdge> markedEdges = new EdgeList<>();
        forestEdges.stream().map(e -> e.original).forEach(markedEdges::append);

        // if a single component remains we can return the mst edges
        if (iterations == 1)
            return markedEdges;

        // otherwise we contract the components
        Graphs.ContractedWrapper contracted = Graphs.contract(vertices, forestEdges, edges);

        // and recurse on the contracted graph
        return recurse(contracted.size, contracted.edges).meld(markedEdges);
    }
}
