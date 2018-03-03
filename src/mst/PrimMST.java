package mst;

import util.graph.AdjacencyList;
import util.graph.EdgeList;
import util.graph.edge.DirectedEdge;
import util.queue.ExtendedPriorityQueue;
import util.queue.FibonacciHeap;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

public final class PrimMST {

    public static <T extends Comparable<? super T>, E extends DirectedEdge<T, E>> EdgeList<E> compute(int vertices, Iterable<E> edges) {

        boolean[] visited = new boolean[vertices];

        List<T> distances = new ArrayList<>();
        List<E> lightest = new ArrayList<>();

        for (int i = 0; i < vertices; i++) {
            lightest.add(null);
            distances.add(null);
        }

        AdjacencyList<E> adjacency = AdjacencyList.of(vertices, edges);

        Comparator<Integer> nullsLast = Comparator.comparing(distances::get, Comparator.nullsLast(T::compareTo));
        ExtendedPriorityQueue<Integer> queue = new FibonacciHeap<>(nullsLast);

        long[] ids = new long[vertices];
        for (int i = 0; i < vertices; i++)
            ids[i] = queue.insertWithId(i);

        while (!queue.empty()) {
            int vertex = queue.pop();
            visited[vertex] = true;

            for (E e : adjacency.get(vertex)) {
                if (!visited[e.to()]) {
                    if (distances.get(e.to()) == null || distances.get(e.to()).compareTo(e.weight()) > 0) {
                        distances.set(e.to(), e.weight());
                        lightest.set(e.to(), e);
                        queue.decrease(ids[e.to()]);
                    }
                }
            }
        }

        EdgeList<E> result = new EdgeList<>();
        lightest.stream().filter(Objects::nonNull).forEach(result::append);
        return result;
    }
}
