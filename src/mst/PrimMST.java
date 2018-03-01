package mst;

import util.graph.AdjacencyList;
import util.graph.EdgeList;
import util.graph.edge.DirectedEdge;
import util.queue.ExtendedPriorityQueue;
import util.queue.FibonacciHeap;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public final class PrimMST {

    public static <E extends DirectedEdge<E>> EdgeList<E> compute(int vertices, Iterable<E> edges) {

        double[] distances = new double[vertices];
        boolean[] visited = new boolean[vertices];
        List<E> lightest = new ArrayList<>();

        for (int i = 0; i < vertices; i++)
            lightest.add(null);

        for (int i = 0; i < vertices; i++)
            distances[i] = Double.POSITIVE_INFINITY;

        AdjacencyList<E> adjacency = AdjacencyList.of(vertices, edges);

        ExtendedPriorityQueue<Integer> queue = new FibonacciHeap<>(Comparator.comparingDouble(i -> distances[i]));

        distances[0] = 0;
        lightest.set(0, null);

        long[] ids = new long[vertices];
        for (int i = 0; i < vertices; i++)
            ids[i] = queue.insertWithId(i);

        while (!queue.empty()) {
            int vertex = queue.pop();
            visited[vertex] = true;

            for (E e : adjacency.get(vertex)) {
                if (!visited[e.to()] && distances[e.to()] > e.weight()) {
                    distances[e.to()] = e.weight();
                    lightest.set(e.to(), e);
                    queue.decrease(ids[e.to()]);
                }
            }
        }

        return new EdgeList<>(lightest.subList(1, vertices));
    }
}
