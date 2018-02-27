package mst;

import util.graph.AdjacencyList;
import util.graph.EdgeList;
import util.graph.edge.WeightedEdge;
import util.queue.ExtendedPriorityQueue;
import util.queue.KAryHeap;

import java.util.Arrays;
import java.util.Comparator;

public final class PrimMST {

    public static EdgeList<WeightedEdge> compute(int vertices, Iterable<WeightedEdge> edges) {

        double[] distances = new double[vertices];
        boolean[] visited = new boolean[vertices];
        WeightedEdge[] lightest = new WeightedEdge[vertices];

        for (int i = 0; i < vertices; i++)
            distances[i] = Double.POSITIVE_INFINITY;

        AdjacencyList<WeightedEdge> adjacency = AdjacencyList.of(vertices, edges);

        // TODO use fib heaps
        ExtendedPriorityQueue<Integer> queue = new KAryHeap<>(2, Comparator.comparingDouble(i -> distances[i]));

        distances[0] = 0;
        lightest[0] = null;

        long[] ids = new long[vertices];
        for (int i = 0; i < vertices; i++)
            ids[i] = queue.insertWithId(i);

        while (!queue.empty()) {
            int vertex = queue.pop();
            visited[vertex] = true;

            for (WeightedEdge e : adjacency.get(vertex)) {
                if (!visited[e.to()] && distances[e.to()] > e.weight()) {
                    distances[e.to()] = e.weight();
                    lightest[e.to()] = e;
                    queue.decrease(ids[e.to()]);
                }
            }
        }

        return new EdgeList<>(Arrays.asList(lightest).subList(1, vertices));
    }
}
