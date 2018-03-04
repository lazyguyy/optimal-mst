package mst;

import util.graph.AdjacencyList;
import util.graph.EdgeList;
import util.graph.Graph;
import util.graph.Graphs;
import util.graph.edge.ContractedEdge;
import util.graph.edge.DirectedEdge;
import util.queue.ExtendedPriorityQueue;
import util.queue.FibonacciHeap;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;

public final class FredmanTarjanMST {

    public static <T extends Comparable<? super T>, E extends DirectedEdge<T, E> & Comparable<? super E>>
        	EdgeList<E> compute(int vertices, Iterable<E> edges) {
        EdgeList<ContractedEdge<T, E>> wrapper = new EdgeList<>();
        for (E e : edges)
            wrapper.append(new ContractedEdge<>(e));
        return recurse(vertices, wrapper);
    }

    private static <T extends Comparable<? super T>, E extends DirectedEdge<T, E> & Comparable<? super E>>
            EdgeList<E> recurse(int vertices, EdgeList<ContractedEdge<T, E>> edges) {

        if (vertices < 2)
            return new EdgeList<>();

        List<T> distances = new ArrayList<>();
        List<ContractedEdge<T, E>> predecessorEdge = new ArrayList<>();
        for (int i = 0; i < vertices; i++) {
            predecessorEdge.add(null);
            distances.add(null);
        }

        AdjacencyList<ContractedEdge<T, E>> adjacency = AdjacencyList.of(vertices, edges);

        int edgeCount = edges.size();

        Comparator<Integer> nullsLast = Comparator.comparing(distances::get, Comparator.nullsLast(T::compareTo));
        ExtendedPriorityQueue<Integer> queue = new FibonacciHeap<>(nullsLast);

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

                for (ContractedEdge<T, E> e : adjacency.get(vertex)) {
                    // relax newly discovered vertices
                    if (discoveredInIteration[e.to()] == -1) {
                        if (distances.get(e.to()) == null || distances.get(e.to()).compareTo(e.weight()) > 0) {
                            distances.set(e.to(), e.weight());
                            predecessorEdge.set(e.to(), e);
                            queue.decrease(ids[e.to()]);
                        }
                    }
                }
            }
            iterations++;
        }

        // find non-null edges
        HashSet<ContractedEdge<T, E>> forestEdges = new HashSet<>();
        for (ContractedEdge<T, E> e : predecessorEdge) {
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
        Graph<ContractedEdge<T, ContractedEdge<T, E>>> contracted = Graphs.contract(vertices, forestEdges, edges);
        EdgeList<ContractedEdge<T, E>> contractedEdges = Graphs.flatten(contracted.edges);

        // and recurse on the contracted graph
        markedEdges.meld(recurse(contracted.vertices, contractedEdges));
        return markedEdges;
    }
}
