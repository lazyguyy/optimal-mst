package mst;

import util.graph.EdgeList;
import util.graph.Graphs;
import util.graph.WeightedEdge;
import util.queue.ExtendedPriorityQueue;
import util.queue.KAryHeap;

import java.util.*;

public class FredmanTarjanMST {

    public static EdgeList compute(int vertices, Iterable<WeightedEdge> edges) {

        double[] distances = new double[vertices];
        int[] component = new int[vertices];
        WeightedEdge[] lightest = new WeightedEdge[vertices];

        for (int i = 0; i < vertices; i++)
            distances[i] = Double.POSITIVE_INFINITY;

        // -1 represents no component
        for (int i = 0; i < vertices; i++)
            component[i] = -1;

        EdgeList[] adjacency = Graphs.adjacencyList(vertices, edges);

        int edgeCount = 0;
        for (WeightedEdge i : edges) edgeCount++;

        // TODO use fibonacci heaps
        ExtendedPriorityQueue<Integer> queue = new KAryHeap<>(2, Comparator.comparingDouble(i -> distances[i]));

        for (int i = 0; i < vertices; i++)
            queue.insert(i);

        // TODO replace by integer power
        int componentMax = (int) Math.ceil(Math.pow(2, 2 * edgeCount / vertices));

        while (!queue.empty()) {

            int componentSize = 0;

            // find arbitrary tree root
            int componentRoot = queue.peek();
            lightest[componentRoot] = null;

            while (!queue.empty() && componentSize < componentMax) {
                int vertex = queue.pop();

                component[vertex] = componentCount;

                componentSize++;

                // stop if two trees are about to merge
                int predecessor = lightest[vertex].from;
                if (component[predecessor] != component[vertex])
                    break;

                for (WeightedEdge e : adjacency[vertex]) {
                    if (component[e.to] == -1 && distances[e.to] > e.weight) {
                        distances[e.to] = e.weight;
                        lightest[e.to] = e;
                        queue.decrease(e.to);
                    }
                }
            }
            componentCount++;
        }

        // feed iterator over non-null elements into EdgeList
        EdgeList primEdges = new EdgeList(() -> Arrays.stream(lightest).filter(Objects::nonNull).iterator());

        // if a single component remains we can return the mst edges
        if (componentCount == 1)
            return primEdges;

        // otherwise we contract the components
        // TODO select appropriate collection for this
        // TODO filter duplicates
        ArrayList<WeightedEdge> contracted = new ArrayList<>();
        for (WeightedEdge e : edges)
            if (component[e.from] != component[e.to])
                contracted.add(new WeightedEdge(component[e.from], component[e.to], e.weight));

        // and recurse on the contracted graph
        return compute(componentCount, contracted).meld(primEdges);
    }
}
