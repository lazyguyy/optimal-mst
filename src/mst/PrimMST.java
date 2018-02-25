package mst;

import util.graph.EdgeList;
import util.graph.Graphs;
import util.graph.WeightedEdge;
import util.queue.ExtendedPriorityQueue;
import util.queue.KAryHeap;

import java.util.Arrays;
import java.util.Comparator;

public class PrimMST {

    public static EdgeList compute(int vertices, Iterable<WeightedEdge> edges) {

        double[] distances = new double[vertices];
        boolean[] visited = new boolean[vertices];
        WeightedEdge[] lightest = new WeightedEdge[vertices];

        for (int i = 0; i < vertices; i++)
            distances[i] = Double.POSITIVE_INFINITY;

        EdgeList[] adjacency = Graphs.adjacencyList(vertices, edges);

        // TODO use fib heaps
        ExtendedPriorityQueue<Integer> queue = new KAryHeap<>(2, Comparator.comparingDouble(i -> distances[i]));

        distances[0] = 0;
        lightest[0] = null;

        for (int i = 0; i < vertices; i++)
            queue.insert(i);

        while (!queue.empty()) {
            int vertex = queue.pop();
            visited[vertex] = true;

            for (WeightedEdge e : adjacency[vertex]) {
                if (!visited[e.to] && distances[e.to] > e.weight) {
                    distances[e.to] = e.weight;
                    lightest[e.to] = e;
                    queue.decrease(e.to);
                }
            }
        }

        return new EdgeList(Arrays.asList(lightest).subList(1, vertices));
    }
}
