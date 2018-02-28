package util.graph;

import util.graph.edge.*;

import java.util.*;
import java.util.stream.Collectors;

public final class Graphs {

    public static <E extends DirectedEdge<E>> int[] componentMapping(int vertices, Iterable<E> edges) {
        return componentMapping(vertices, components(vertices, edges));
    }

    public static int[] componentMapping(int vertices, List<List<Integer>> components) {

        int componentMapping[] = new int[vertices];

        for (int c = 0; c < components.size(); c++)
            for (int vertex : components.get(c))
                componentMapping[vertex] = c;

        return componentMapping;
    }

    public static <E extends DirectedEdge<E>> List<List<Integer>> components(int vertices, Iterable<E> edges) {

        // generate adjacency list
        AdjacencyList<E> adjacency = AdjacencyList.of(vertices, edges);

        ArrayList<List<Integer>> components = new ArrayList<>();
        boolean[] visited = new boolean[vertices];

        ArrayDeque<Integer> stack = new ArrayDeque<>();
        for (int v = 0; v < vertices; v++) {
            // if we discover a new component ...
            if (visited[v])
                continue;

            ArrayList<Integer> comp = new ArrayList<>();
            // ... perform dfs on component
            stack.push(v);
            while (!stack.isEmpty()) {
                int neighbor = stack.pop();
                // set component id for every connected vertex
                comp.add(neighbor);
                visited[neighbor] = true;
                for (E e : adjacency.get(neighbor))
                    if (!visited[e.to()])
                        stack.push(e.to());
            }
            components.add(comp);
        }
        return components;
    }

    public static <E extends DirectedEdge<E>> EdgeList<E> sortEdges(int vertices, Iterable<E> edges) {
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
        for (int v = 0; v < vertices; v++)
            result.meld(secondPass.get(v));
        return result;
    }

    public static <E extends DirectedEdge<E>> EdgeList<E> removeDuplicates(int vertices, Iterable<E> edges) {

        EdgeList<E> sorted = sortEdges(vertices, edges);
        EdgeList<E> result = new EdgeList<>();

        E lightest = null;
        // find lightest edge between all pairs of vertices
        for (E e : sorted) {
            // skip self-loops
            if (e.from() == e.to())
                continue;
            // first iteration: initialize candidate for lightest edge
            if (lightest == null)
                lightest = e;
            // different endpoint: save current best
            if (lightest.from() != e.from() || lightest.to() != e.to()) {
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

        return result;
    }

    /**
     * Takes an Iterable of Contracted Edges and renames the vertices.
     * @param edges The Iterable whose vertices shall be renamed
     * @return Returns an AdjacencyList of renamed Edges
     */
    public static AdjacencyList<ContractedEdge> renameVertices(Iterable<ContractedEdge> edges) {
        Map<Integer, Integer> renamedVertices = new HashMap<>();
        List<ContractedEdge> renamedEdges = new ArrayList<>();
        int vertex = 0;
        for (ContractedEdge edge : edges) {
            int from = edge.from(), to = edge.to();
            if (!renamedVertices.containsKey(from)) {
                renamedVertices.put(from, vertex);
                vertex++;
            }
            if (!renamedVertices.containsKey(to)) {
                renamedVertices.put(to, vertex);
                vertex++;
            }
            renamedEdges.add(new ContractedEdge(renamedVertices.get(from), renamedVertices.get(to), edge.original));
        }
        return AdjacencyList.of(vertex, renamedEdges);
    }  

    public static <E extends DirectedEdge<E> & Comparable<? super E>>
            Set<E> lightestEdgePerVertex(int vertices, Iterable<E> edges) {

        ArrayList<E> lightest = new ArrayList<>(vertices);
        for (int i = 0; i < vertices; i++)
            lightest.add(null);

        // store lightest edge for each vertex
        for (E ce : edges) {
            if (lightest.get(ce.from()) == null || lightest.get(ce.from()).compareTo(ce) > 0)
                lightest.set(ce.from(), ce);
            if (lightest.get(ce.to()) == null || lightest.get(ce.to()).compareTo(ce) > 0)
                lightest.set(ce.to(), ce);
        }

        // remove nulls and duplicates
        return lightest.stream().filter(Objects::nonNull).collect(Collectors.toCollection(HashSet::new));
    }

    public static ContractedWrapper contract(int vertices, Iterable<ContractedEdge> span, Iterable<ContractedEdge> edges) {

        // find connected components
        List<List<Integer>> components = components(vertices, span);
        // obtain component mapping
        int[] component = componentMapping(vertices, components);
        // find number of components
        int componentCount = components.size();

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
