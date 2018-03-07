package util.decision;

import mst.KruskalMST;
import util.graph.EdgeList;
import util.graph.Graphs;
import util.graph.edge.DirectedEdge;
import util.graph.edge.IndexedEdge;
import util.graph.edge.WeightedEdge;
import util.log.Logger;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Represents a decision tree that can compute the MST of graphs up to a given size using an optimal number of comparisons.
 */
public final class PrecomputedMSTCollection implements Serializable {

    private static final long serialVersionUID = 1L;

    // graphs[vertex count][edge structure id]
    private final Map<Integer, Map<GraphStructure, GraphStructureMSTLookup>> graphs;
    private final int maxVertices;

    private PrecomputedMSTCollection(int maxVertices, Map<Integer, Map<GraphStructure, GraphStructureMSTLookup>> graphs) {
        this.graphs = graphs;
        this.maxVertices = maxVertices;
    }

    /**
     * Returns the max number of vertices of a graph up to which the optimal mst decision trees have been computed.
     * @return the max number of vertices of a graph up to which the optimal mst decision trees have been computed
     */
    public int getMaxVertices() {
        return maxVertices;
    }

    /**
     * Computes all optimal mst decision trees for graphs with up to maxVertices vertices
     * @param maxVertices the number of vertices that a graph of which we want to compute the mst in the optimal number of
     * comparisons may have
     * @return an object of this class that contains all the decision trees
     */
    public static PrecomputedMSTCollection computeUpTo(int maxVertices) {

        Logger.logf("Computing decision trees for graphs with up to %s vertices.", maxVertices);

        Map<Integer, Map<GraphStructure, GraphStructureMSTLookup>> lookups = new HashMap<>();

        // iterate over all vertex counts
        for (int vertices = 2; vertices < maxVertices + 1; vertices++) {
            lookups.put(vertices, new HashMap<>());

            List<WeightedEdge<Integer>> possibleEdges = new ArrayList<>();
            Iterators.ascendingIntPairs(vertices, (i, j) -> new WeightedEdge<>(i, j, 0)).forEach(possibleEdges::add);

            int counter = 0;
            long max = 1L << possibleEdges.size();

            // generate every combination of edges
            edgecombinations:
            for (List<WeightedEdge<Integer>> edges : Iterators.powerSet(possibleEdges)) {
                counter++;

                if (edges.size() <= 1)
            		continue;

                Logger.logf("Generating decision tree for edge structure %s/%s (%s vertices, %s edges total).",
                        counter, max, vertices, edges.size());

                // iterate over all decision tree depths
                for (int depth = 0; depth < vertices * vertices; depth++) {
                    Logger.logf("  Currently exploring depth: %s", depth);

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
                        GraphStructure structureId = GraphStructure.of(vertices, edges);
                        lookups.get(vertices).put(structureId, new DecisionTreeMSTLookup(tree, mstIndices));
                        continue edgecombinations;
                    }
                }
                // this should never happen
                throw new RuntimeException("No MST found!");
            }
        }
        return new PrecomputedMSTCollection(maxVertices, lookups);
    }

    public <T, E extends DirectedEdge<T, E> & Comparable<? super E>> EdgeList<E> findMST(int vertices, List<E> edges) {
        if (vertices > maxVertices)
            throw new IllegalArgumentException("No precomputed solutions exist for graph size " + vertices + ".");

        // short-circuit for trivial cases
        if (vertices < 2)
            return new EdgeList<>();

        if (edges.size() <= 1)
        	return new EdgeList<>(edges);

        GraphStructure structureId = GraphStructure.of(vertices, edges);
        GraphStructureMSTLookup structure = graphs.get(vertices).get(structureId);

        EdgeList<E> mst = new EdgeList<>();
        for (int index : structure.lookup(edges))
            mst.append(edges.get(index));
        return mst;
    }

    // encapsulates which edges a graph contains
    // hashable in O(1) due to precomputation
    // comparable in O(m) because edges are sorted
    // can be used as key in a HashMap for lookup in expected time O(c * m) = O(m)
    private static final class GraphStructure implements Serializable {
        private static final long serialVersionUID = 1L;

        private final int[] froms;
        private final int[] tos;
        private final int hash;

        public static <T, E extends DirectedEdge<T, E>> GraphStructure of(int vertices, Iterable<E> edges) {
            EdgeList<E> sorted = Graphs.sortEdges(vertices, edges);
            int[] froms = new int[sorted.size()];
            int[] tos = new int[sorted.size()];
            int index = 0;
            for (E e : sorted) {
                froms[index] = e.from();
                tos[index] = e.to();
                index++;
            }
            int hash = Objects.hash(Arrays.hashCode(froms), Arrays.hashCode(tos));
            return new GraphStructure(hash, froms, tos);
        }

        GraphStructure(final int hash, final int[] froms, final int[] tos) {
            this.hash = hash;
            this.froms = froms;
            this.tos = tos;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            GraphStructure that = (GraphStructure) o;
            return Arrays.equals(froms, that.froms) && Arrays.equals(tos, that.tos);
        }

        @Override
        public int hashCode() {
            return hash;
        }
    }

    // for extensibility
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
