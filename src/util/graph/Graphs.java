package util.graph;

import java.util.ArrayDeque;
import java.util.Arrays;

public final class Graphs {

    private Graphs() {}

    public static <E extends DirectedEdge<E>> int[] components(int vertices, Iterable<E> edges) {

        // generate adjacency list
        AdjacencyList<E> adjacency = AdjacencyList.of(vertices, edges);

        // initialize components
        int component[] = new int[vertices];
        for (int v = 0; v < vertices; v++)
            component[v] = -1;

        int componentCount = 0;
        ArrayDeque<Integer> stack = new ArrayDeque<>();
        for (int v = 0; v < vertices; v++) {
            // if we discover a new component ...
            if (component[v] != -1)
                continue;

            // ... perform dfs on component
            stack.push(v);
            while (!stack.isEmpty()) {
                int neighbor = stack.pop();
                // set component id for every connected vertex
                component[neighbor] = componentCount;
                for (E e : adjacency.get(neighbor))
                    if (component[e.to()] == -1)
                        stack.push(e.to());
            }
            componentCount++;
        }

        return component;
    }

    public static int componentCount(int[] components) {
        return Arrays.stream(components).max().orElse(-1) + 1;
    }

    public static <E extends DirectedEdge<E>> EdgeList<E> removeDuplicates(int vertices, Iterable<E> edges) {

        AdjacencyList<E> firstPass = new AdjacencyList<>(vertices);
        AdjacencyList<E> secondPass = new AdjacencyList<>(vertices);

        // first pass: bucket by e.to
        for (E e : edges) {
            if (e.from() > e.to())
                e = e.reversed();
            firstPass.append(e.to(), e);
        }

        // second pass: bucket by e.from
        for (int v = 0; v < vertices; v++)
            for (E e : firstPass.get(vertices - v - 1))
                secondPass.append(e.from(), e);

        EdgeList<E> result = new EdgeList<>();
        for (int v = 0; v < vertices; v++) {
            E lightest = null;
            // find lightest edge from v to all connected vertices
            for (E e : secondPass.get(v)) {
                // skip self-loops
                if (e.to() == v)
                    continue;
                // first iteration: initialize candidate for lightest edge
                if (lightest == null)
                    lightest = e;
                // different target: save current best
                if (lightest.to() != e.to()) {
                    result.append(lightest);
                    lightest = e;
                }
                // update lightest if we find a better candidate
                if (lightest.weight() > e.weight())
                    lightest = e;
            }
            // append last result
            if (lightest != null)
                result.append(lightest);
        }
        return result;
    }

    public static ContractedWrapper contract(int vertices, Iterable<ContractedEdge> span, Iterable<ContractedEdge> edges) {

        // find connected components
        int[] component = Graphs.components(vertices, span);
        // find number of components
        int componentCount = Graphs.componentCount(component);

        // contract components
        EdgeList<ContractedEdge> contracted = new EdgeList<>();
        for (ContractedEdge ce : edges) {
            if (component[ce.from()] == component[ce.to()])
                continue;
            contracted.append(new ContractedEdge(component[ce.from()], component[ce.to()], ce.original));
        }
        // remove duplicates
        EdgeList<ContractedEdge> remainingEdges = Graphs.removeDuplicates(componentCount, contracted);

        return new ContractedWrapper(componentCount, remainingEdges);
    }

    public static final class ContractedWrapper {
        public final int size;
        public final EdgeList<ContractedEdge> edges;

        ContractedWrapper(int size, EdgeList<ContractedEdge> edges) {
            this.size = size;
            this.edges = edges;
        }
    }
}
