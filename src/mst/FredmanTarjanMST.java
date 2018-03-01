package mst;

import util.graph.*;
import util.graph.edge.ContractedEdge;
import util.graph.edge.DirectedEdge;
import util.graph.edge.WeightedEdge;
import util.queue.ExtendedPriorityQueue;
import util.queue.FibonacciHeap;

import java.util.*;

public final class FredmanTarjanMST {

    public static <E extends DirectedEdge<E> & Comparable<? super E>>
            EdgeList<E> compute(int vertices, Iterable<E> edges) {
        EdgeList<ContractedEdge<E>> wrapper = new EdgeList<>();
        for (E e : edges)
            wrapper.append(new ContractedEdge<>(e));
        return recurse(vertices, wrapper);
    }

    private static <E extends DirectedEdge<E> & Comparable<? super E>>
            EdgeList<E> recurse(int vertices, EdgeList<ContractedEdge<E>> edges) {

        if (vertices < 2)
            return new EdgeList<>();

        double[] distances = new double[vertices];
        List<ContractedEdge<E>> predecessorEdge = new ArrayList<>();
        for (int i = 0; i < vertices; i++)
            predecessorEdge.add(null);

        for (int i = 0; i < vertices; i++)
            distances[i] = Double.POSITIVE_INFINITY;

        AdjacencyList<ContractedEdge<E>> adjacency = AdjacencyList.of(vertices, edges);

        int edgeCount = edges.size();

        ExtendedPriorityQueue<Integer> queue = new FibonacciHeap<>(Comparator.comparingDouble(i -> distances[i]));

        long[] ids = new long[vertices];
        for (int i = 0; i < vertices; i++)
            ids[i] = queue.insertWithId(i);

        // calculate upper bound for component size
        int exp = 2 * edgeCount / vertices;
        // avoid overflows
        exp = Math.min(62, exp);
        long componentMax = 1L << exp;

        int[] discoveredInIteration = new int[vertices];
        for (int i = 0; i < vertices; i++)
            discoveredInIteration[i] = -1;

        int iterations = 0;

        // grow component trees
        while (!queue.empty()) {

            int componentSize = 0;

            // find arbitrary tree root
            int componentRoot = queue.peek();
            predecessorEdge.set(componentRoot, null);

            // grow a single tree
            while (!queue.empty() && componentSize < componentMax) {
                int vertex = queue.pop();

                componentSize++;
                discoveredInIteration[vertex] = iterations;

                // stop if two trees are about to merge
                if (predecessorEdge.get(vertex) != null) {
                    int predecessor = predecessorEdge.get(vertex).from();
                    if (discoveredInIteration[predecessor] != discoveredInIteration[vertex])
                        break;
                }

                for (ContractedEdge<E> e : adjacency.get(vertex)) {
                    if (discoveredInIteration[e.to()] == -1 && distances[e.to()] > e.weight()) {
                        distances[e.to()] = e.weight();
                        predecessorEdge.set(e.to(), e);
                        queue.decrease(ids[e.to()]);
                    }
                }
            }
            iterations++;
        }

        // find non-null edges
        HashSet<ContractedEdge<E>> forestEdges = new HashSet<>();
        for (ContractedEdge<E> e : predecessorEdge) {
            if (e == null)
                continue;
            forestEdges.add(e);
        }

        // extract original edges
        EdgeList<E> markedEdges = new EdgeList<>();
        forestEdges.stream().map(e -> e.original).forEach(markedEdges::append);

        // if a single component remains we can return the mst edges
        if (iterations == 1)
            return markedEdges;

        // otherwise we contract the components
        Graph<ContractedEdge<E>> contracted = Graphs.contract(vertices, forestEdges, edges);

        // and recurse on the contracted graph
        markedEdges.meld(recurse(contracted.vertices, contracted.edges));
        return markedEdges;
    }
}
