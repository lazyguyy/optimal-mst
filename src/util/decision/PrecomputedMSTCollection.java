package util.decision;

import util.graph.EdgeList;
import util.graph.edge.DirectedEdge;

import java.util.*;

public class PrecomputedMSTCollection {

    // graphs[vertex count][edge structure id]
    private final Map<Integer, Map<Integer, GraphStructureMSTLookup>> graphs;
    private final int maxVertices;

    private PrecomputedMSTCollection(int maxVertices, Map<Integer, Map<Integer, GraphStructureMSTLookup>> graphs) {
        this.graphs = graphs;
        this.maxVertices = maxVertices;
    }

    public static PrecomputedMSTCollection computeUpTo(int maxVertices) {

        Map<Integer, Map<Integer, GraphStructureMSTLookup>> lookups = new HashMap<>();

        // iterate over all vertex counts
        for (int vertices = 2; vertices < maxVertices + 1; vertices++) {
            lookups.put(vertices, new HashMap<>());

            List<Iterators.IntTuple> possibleEdges = new ArrayList<>();
            Iterators.ascendingIntPairs(vertices).forEach(possibleEdges::add);

            // generate every combination of edges
            edgecombinations:
            for (List<Iterators.IntTuple> edges : Iterators.powerSet(possibleEdges)) {

                // iterate over all decision tree depths
                for (int depth = 0; depth < vertices * vertices; depth++) {

                    decisiontrees:
                    // iterate over all decision trees
                    for (DecisionTree tree : DecisionTree.enumerateTrees(depth, vertices)) {

                        Map<Integer, List<Integer>> mstIndices = new HashMap<>();

                        for (List<Integer> permutation : Iterators.indexPermutations(edges.size())) {
                            int bucket = tree.classify(permutation, Comparator.naturalOrder());
                            // calculate mst indices here

                            if (mstIndices.containsKey(bucket)) {
                                // compare calculated indices with stored ones
                                if (unequal)
                                    continue decisiontrees;
                            } else {
                                // store indices
                                mstIndices.put(bucket, ...)
                            }
                        }

                        // a perfect decision tree has been found
                        int structureId = structureId();
                        lookups.get(vertices).put(structureId, new DecisionTreeMSTLookup(tree, mstIndices));
                        continue edgecombinations;
                    }
                }
                throw new RuntimeException("No MST found!");
            }
        }
        return new PrecomputedMSTCollection(maxVertices, lookups);
    }

    public <E extends DirectedEdge<E> & Comparable<? super E>> EdgeList<E> findMST(int vertices, List<E> edges) {
        if (vertices >= maxVertices)
            throw new IllegalArgumentException("No precomputed solutions exist for graph size " + vertices + ".");

        // short-circuit for trivial cases
        if (vertices < 2)
            return new EdgeList<>();

        int structureId = structureId(vertices, edges);
        GraphStructureMSTLookup structure = graphs.get(vertices).get(structureId);

        EdgeList<E> mst = new EdgeList<>();
        for (int index : structure.lookup(edges, E::compareTo))
            mst.append(edges.get(index));
        return mst;
    }

    // structure id k of a graph g:
    // bit (i * vertices) + j of k is set iff there is an edge between vertices i and j in g
    // this only works for graphs with up to 5 vertices :)
    private static <E extends DirectedEdge<E>> int structureId(int vertices, Iterable<E> edges) {
        int id = 0;
        for (E e : edges) {
            if (e.from() == e.to())
                continue;

            id |= 1 << (e.from() * vertices + e.to());
            id |= 1 << (e.to() * vertices + e.from());
        }
        return id;
    }

    // for extensibility in case of emergency
    private static interface GraphStructureMSTLookup {
        <E> List<Integer> lookup(List<E> edges, Comparator<? super E> comparator);
    }

    private static final class DecisionTreeMSTLookup implements GraphStructureMSTLookup {
        // the decision tree for this graph structure
        private final DecisionTree tree;
        // the bucket lookup table
        private final Map<Integer, List<Integer>> mstIndices;

        public DecisionTreeMSTLookup(DecisionTree tree, Map<Integer, List<Integer>> indices) {
            this.tree = tree;
            this.mstIndices = indices;
        }

        // look up the edge indices for the mst for this graph structure
        @Override
        public <E> List<Integer> lookup(List<E> edges, Comparator<? super E> comparator) {
            int bucket = tree.classify(edges, comparator);
            return mstIndices.get(bucket);
        }
    }
}
