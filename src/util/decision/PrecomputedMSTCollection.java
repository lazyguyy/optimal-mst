package util.decision;

import mst.KruskalMST;
import util.graph.EdgeList;
import util.graph.edge.DirectedEdge;
import util.graph.edge.IndexedEdge;
import util.graph.edge.WeightedEdge;
import util.log.Logger;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

public final class PrecomputedMSTCollection implements Serializable {

    private static final long serialVersionUID = 1L;

    // graphs[vertex count][edge structure id]
    private final Map<Integer, Map<Integer, GraphStructureMSTLookup>> graphs;
    private final int maxVertices;

    private PrecomputedMSTCollection(int maxVertices, Map<Integer, Map<Integer, GraphStructureMSTLookup>> graphs) {
        this.graphs = graphs;
        this.maxVertices = maxVertices;
    }

    public int getMaxVertices() {
        return maxVertices;
    }

    public static PrecomputedMSTCollection computeUpTo(int maxVertices) {

        Logger.logf("Computing decision trees for up to %s vertices.", maxVertices);

        Map<Integer, Map<Integer, GraphStructureMSTLookup>> lookups = new HashMap<>();

        // iterate over all vertex counts
        for (int vertices = 2; vertices < maxVertices + 1; vertices++) {
            lookups.put(vertices, new HashMap<>());

            List<WeightedEdge<Integer>> possibleEdges = new ArrayList<>();
            Iterators.ascendingIntPairs(vertices, (i, j) -> new WeightedEdge<>(i, j, 0)).forEach(possibleEdges::add);

            // generate every combination of edges
            edgecombinations:
            for (List<WeightedEdge<Integer>> edges : Iterators.powerSet(possibleEdges)) {
            	if (edges.size() <= 1)
            		continue;

            	Logger.logf("Generating decision trees for graphs with %s edges and %s vertices.", edges.size(), vertices);
                // iterate over all decision tree depths
                for (int depth = 0; depth < vertices * vertices; depth++) {

                    decisiontrees:
                    // iterate over all decision trees
                    for (DecisionTree tree : DecisionTree.enumerateTrees(depth, edges.size())) {

                        Map<Integer, List<Integer>> mstIndices = new HashMap<>();

                        for (List<Integer> permutation : Iterators.indexPermutations(edges.size())) {
                            int bucket = tree.classify(permutation);
                            // calculate mst indices here
                            
                            List<IndexedEdge<Integer, WeightedEdge<Integer>>> permutedEdges = new ArrayList<>();
                            // Create graph with permuted edge weights
                            for (int index = 0; index < edges.size(); ++index) {
                            	permutedEdges.add(new IndexedEdge<>(index,
                                        edges.get(index).reweighted(permutation.get(index))));
                            }
                            
                            EdgeList<IndexedEdge<Integer, WeightedEdge<Integer>>> mst = KruskalMST.compute(vertices, permutedEdges);

                            List<Integer> edgeIndices = new ArrayList<>();
                            for (IndexedEdge<Integer, WeightedEdge<Integer>> edge : mst) {
                            	edgeIndices.add(edge.index);
                            }
                            Collections.sort(edgeIndices);
                            
                            if (mstIndices.containsKey(bucket)) {
                                // compare calculated indices with stored ones
                                List<Integer> otherIndices = mstIndices.get(bucket);

                                if (otherIndices.size() == edgeIndices.size())
                                    continue decisiontrees;

                                for (int i = 0; i < edgeIndices.size(); ++i)
                                    if (!otherIndices.get(i).equals(edgeIndices.get(i)))
                                        continue decisiontrees;

                            } else {
                                // store indices
                                mstIndices.put(bucket, edgeIndices);
                            }
                        }

                        // a perfect decision tree has been found
                        Logger.logf("Edges: %s", edges.stream().map(e -> String.format("(%s, %s)", e.from(), e.to())).collect(Collectors.joining(" ")));
                        Logger.logf("MST: %s", mstIndices);
                        Logger.log(tree.toString());
                        int structureId = structureId(vertices, edges);
                        lookups.get(vertices).put(structureId, new DecisionTreeMSTLookup(tree, mstIndices));
                        continue edgecombinations;
                    }
                }
                throw new RuntimeException("No MST found!");
            }
        }
        return new PrecomputedMSTCollection(maxVertices, lookups);
    }

    public <T, E extends DirectedEdge<T, E> & Comparable<? super E>> EdgeList<E> findMST(int vertices, List<E> edges) {
        if (vertices >= maxVertices)
            throw new IllegalArgumentException("No precomputed solutions exist for graph size " + vertices + ".");

        // short-circuit for trivial cases
        if (vertices < 2)
            return new EdgeList<>();
        
        if (edges.size() <= 1)
        	return new EdgeList<>(edges);

        int structureId = structureId(vertices, edges);
        GraphStructureMSTLookup structure = graphs.get(vertices).get(structureId);

        EdgeList<E> mst = new EdgeList<>();
        for (int index : structure.lookup(edges))
            mst.append(edges.get(index));
        return mst;
    }

    // structure id k of a graph g:
    // bit (i * vertices) + j of k is set iff there is an edge between vertices i and j in g
    // todo this only works for graphs with up to 5 vertices :)
    private static <T, E extends DirectedEdge<T, E>> int structureId(int vertices, Iterable<E> edges) {
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
    private interface GraphStructureMSTLookup {
        <E extends Comparable<? super E>> List<Integer> lookup(List<E> edges);
    }

    private static final class DecisionTreeMSTLookup implements GraphStructureMSTLookup, Serializable {
        private static final long serialVersionUID = 1L;

        // the decision tree for this graph structure
        private final DecisionTree tree;
        // the bucket lookup table
        private final Map<Integer, List<Integer>> mstIndices;

        DecisionTreeMSTLookup(DecisionTree tree, Map<Integer, List<Integer>> indices) {
            this.tree = tree;
            this.mstIndices = indices;
        }

        // look up the edge indices for the mst for this graph structure
        @Override
        public <E extends Comparable<? super E>> List<Integer> lookup(List<E> edges) {
            int bucket = tree.classify(edges);
            return mstIndices.get(bucket);
        }
    }
}
