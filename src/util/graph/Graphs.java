package util.graph;

import util.graph.edge.*;

import java.util.*;
import java.util.stream.Collectors;

public final class Graphs {

    public static <T, E extends DirectedEdge<T, E>> int[] componentMapping(int vertices, Iterable<E> edges) {
        return componentMapping(vertices, components(vertices, edges));
    }

    public static int[] componentMapping(int vertices, List<List<Integer>> components) {

        int componentMapping[] = new int[vertices];

        for (int c = 0; c < components.size(); c++)
            for (int vertex : components.get(c))
                componentMapping[vertex] = c;

        return componentMapping;
    }

    public static <T, E extends DirectedEdge<T, E>> List<List<Integer>> components(int vertices, Iterable<E> edges) {

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

    public static <T, E extends DirectedEdge<T, E>> EdgeList<E> sortEdges(int vertices, Iterable<E> edges) {
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

    public static <T, E extends DirectedEdge<T, E> & Comparable<? super E>> EdgeList<E>
            removeDuplicates(int vertices, Iterable<E> edges) {

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
            if (lightest.compareTo(e) > 0)
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
    public static <T, E extends DirectedEdge<T, E> & Comparable<? super E>>
            Graph<RenamedEdge<T, E>> renameVertices(Iterable<E> edges) {
        Map<Integer, Integer> renamedVertices = new HashMap<>();
        EdgeList<RenamedEdge<T, E>> renamedEdges = new EdgeList<>();
        int vertex = 0;
        for (E edge : edges) {
            int from = edge.from(), to = edge.to();
            if (!renamedVertices.containsKey(from)) {
                renamedVertices.put(from, vertex);
                vertex++;
            }
            if (!renamedVertices.containsKey(to)) {
                renamedVertices.put(to, vertex);
                vertex++;
            }
            renamedEdges.append(new RenamedEdge<>(renamedVertices.get(from), renamedVertices.get(to), edge));
        }
        return new Graph<>(vertex, renamedEdges);
    }

    public static <T, E extends DirectedEdge<T, E> & Comparable<? super E>>
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

    public static <T, S, E extends DirectedEdge<T, E> & Comparable<? super E>, D extends DirectedEdge<S, D>>
            Graph<ContractedEdge<T, E>> contract(int vertices, Iterable<D> span, Iterable<E> edges) {

        // find connected components
        List<List<Integer>> components = components(vertices, span);
        // obtain component mapping
        int[] component = componentMapping(vertices, components);
        // find number of components
        int componentCount = components.size();

        // contract components
        EdgeList<ContractedEdge<T, E>> contracted = new EdgeList<>();
        for (E e : edges) {
            if (component[e.from()] == component[e.to()])
                continue;
            contracted.append(new ContractedEdge<>(component[e.from()], component[e.to()], e));
        }
        // remove duplicates
        EdgeList<ContractedEdge<T, E>> remainingEdges = Graphs.removeDuplicates(componentCount, contracted);

        return new Graph<>(componentCount, remainingEdges);
    }

    public static <T, E extends DirectedEdge<T, E> & Comparable<? super E>>
            EdgeList<ContractedEdge<T, E>> flatten(EdgeList<ContractedEdge<T, ContractedEdge<T, E>>> list) {
        EdgeList<ContractedEdge<T, E>> flat = new EdgeList<>();
        list.forEach(e -> flat.append(new ContractedEdge<>(e.from(), e.to(), e.original.original)));
        return flat;
    }

    public static <T, E extends DirectedEdge<T, E> & Comparable<? super E>>
            Graph<ContractedEdge<T, E>> flatten(Graph<ContractedEdge<T, ContractedEdge<T, E>>> graph) {
        return new Graph<>(graph.vertices, flatten(graph.edges));
    }
}
