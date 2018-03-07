package util.graph;

import util.graph.edge.ContractedEdge;
import util.graph.edge.DirectedEdge;
import util.graph.edge.RenamedEdge;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 
 * Provides a lot of utility methods for working with graphs
 */
public final class Graphs {

    private Graphs() {}

    /**
     * Returns an array representing a connected component mapping. The i-th entry is the index of the connected component
     * which the i-th vertex is part of
     * @param <T> the weight type of the edges in the graph
     * @param <E> the edge type of the edges in the graph
     * @param vertices the number of vertices in the graph
     * @param edges an {@link Iterable} of all edges in the graph
     * @return an array representing a connected component mapping as described above
     */
    public static <T, E extends DirectedEdge<T, E>> int[] componentMapping(int vertices, Iterable<E> edges) {
        return componentMapping(vertices, components(vertices, edges));
    }

    /**
     * Creates an array representing a connected component mapping. The i-th entry is the index of the connected component
     * which the i-th vertex is part of
     * @param vertices the number of vertices in the graph
     * @param components a {@link List} of {@link List} of {@link Integer} where the i-th {@link List} contains the indices of all
     * vertices that belong to the i-th component
     * @return an array representing a connected component mapping as described above 
     */
    public static int[] componentMapping(int vertices, List<List<Integer>> components) {

        int componentMapping[] = new int[vertices];

        for (int c = 0; c < components.size(); c++)
            for (int vertex : components.get(c))
                componentMapping[vertex] = c;

        return componentMapping;
    }

    /**
     * Takes a graph specified by a number of vertices and an {@link Iterable} of edges and returns a {@link List}
     * of {@link List} of {@link Integer}, where the i-th {@link List} contains the indices of all vertices that
     * belong to the i-th connected component.
     * @param <T> the weight type of the edges of the graph
     * @param <E> the edge type of the edges of the graph
     * @param vertices the number of vertices of the graph
     * @param edges an {@link Iterable} of edges of the graph
     * @return a {@link List} of {@link List} of {@link Integer} representing the connected components of the graph
     * as described above
     */
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

    /**
     * Takes a graph specified by the number of vertices and an {@link Iterable} of edges and returns an
     * {@link EdgeList} containing all edges in a sorted manner.
     * @param <T> the weight type of the edges of the graph
     * @param <E> the edge type of the edges of the graph
     * @param vertices the number of vertices in the graph
     * @param edges an {@link Iterable} of edges in the graph
     * @return an {@link EdgeList} containing all edges of the original graph, but sorted
     */
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

    /**
     * Removes multiple edges between two vertices. If there are multiple edges between two vertices, 
     * only the lightest edge is kept.
     * @param <T> the weight type of the edges of the graph
     * @param <E> the edge type of the edges of the graph
     * @param vertices the number of vertices of the graph
     * @param edges an {@link Iterable} of edges of the graph
     * @return an {@link EdgeList} containing no duplicates of edges
     */
    public static <T, E extends DirectedEdge<T, E> & Comparable<? super E>>
            EdgeList<E> removeDuplicates(int vertices, Iterable<E> edges) {

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
     * Takes an {@link Iterable} of edges and renames the vertices, so that all vertex indices
     * are between 0 and n - 1 where n is the number of vertices
     * @param <T> the weight type of the edges of the graph
     * @param <E> the edge type of the edges of the graph
     * @param edges the {@link Iterable} whose vertices shall be renamed
     * @return a {@link Graph} representing the graph but with renamed edges
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

    /**
     * Takes a graph specified by the number of vertices and an {@link Iterable} of edges.
     * For each vertex we take the outgoing edge with smallest weight and return a {@link Set} of all of these edges
     * @param <T> the weight type of the edges of the graph
     * @param <E> the edge type of the edges of the graph
     * @param vertices the number of vertices of the graph
     * @param edges an {@link Iterable} of edges of the graph
     * @return a {@link Set} containing only the lightest edge per vertex
     */
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

    /**
     * Contracts a graph specified by the number of vertices and an {@link Iterable} of edges along the edges
     * given by the {@link Iterable} span. Each group of vertices that is connected by some of the edges of span
     * will be replaced by a single vertex in the contracted graph.
     * @param <T> the weight type of the edges of the graph
     * @param <E> the edge type of the edges of the graph
     * @param <S> the weight type of the edges along which we contract
     * @param <D> the edge type of the edges along which we contract
     * @param vertices the number of vertices in the graph
     * @param span an {@link Iterable} along which the graph shall be contracted
     * @param edges an {@link Iterable} of edges of the graph
     * @return the contracted graph as a {@link Graph}
     */
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

    /**
     * Takes an {@link EdgeList} of at least doubly contracted edges and removes the second-outermost layer of contracted edges
     * @param <T> the weight type of the edges of the graph
     * @param <E> the edge type of the edges of the graph
     * @param list the {@link EdgeList} of at least doubly contracted edges
     * @return the flattened list of edges as an {@link EdgeList}
     */
    public static <T, E extends DirectedEdge<T, E> & Comparable<? super E>>
            EdgeList<ContractedEdge<T, E>> flatten(EdgeList<ContractedEdge<T, ContractedEdge<T, E>>> list) {
        EdgeList<ContractedEdge<T, E>> flat = new EdgeList<>();
        list.forEach(e -> flat.append(new ContractedEdge<>(e.from(), e.to(), e.original.original)));
        return flat;
    }
    
    /**
     * Takes a {@link Graph} of at least doubly contracted edges and removes the second-outermost layer of contracted edges
     * @param <T> the weight type of the edges of the graph
     * @param <E> the edge type of the edges of the graph
     * @param graph the {@link Graph} of at least doubly contracted edges
     * @return the flattened {@link Graph}
     */
    public static <T, E extends DirectedEdge<T, E> & Comparable<? super E>>
            Graph<ContractedEdge<T, E>> flatten(Graph<ContractedEdge<T, ContractedEdge<T, E>>> graph) {
        return new Graph<>(graph.vertices, flatten(graph.edges));
    }
}
