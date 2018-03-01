package util.decision;

import util.graph.EdgeList;
import util.graph.edge.DirectedEdge;
import util.graph.edge.IndexedEdge;
import util.graph.edge.WeightedEdge;

import java.util.*;

import mst.KruskalMST;

public class PrecomputedMSTCollection {

    // graphs[vertex count][edge structure id]
    private final Map<Integer, Map<Integer, GraphStructureMSTLookup>> graphs;
    private final int maxVertices;

    private PrecomputedMSTCollection(int maxVertices, Map<Integer, Map<Integer, GraphStructureMSTLookup>> graphs) {
        this.graphs = graphs;
        this.maxVertices = maxVertices;
    }

    public static PrecomputedMSTCollection computeUpTo(int maxVertices) {
    	
//    	System.out.println("computing decision trees for " + maxVertices);

        Map<Integer, Map<Integer, GraphStructureMSTLookup>> lookups = new HashMap<>();

        // iterate over all vertex counts
        for (int vertices = 2; vertices < maxVertices + 1; vertices++) {
            lookups.put(vertices, new HashMap<>());

            List<Iterators.IntTuple> possibleEdges = new ArrayList<>();
            Iterators.ascendingIntPairs(vertices).forEach(possibleEdges::add);

            // generate every combination of edges
            edgecombinations:
            for (List<Iterators.IntTuple> edges : Iterators.powerSet(possibleEdges)) {
            	if (edges.size() <= 1)
            		continue;

//            	System.out.println("Generating decision trees for graphs with " + edges.size() + " edges and " + vertices + " vertices");
                // iterate over all decision tree depths
                for (int depth = 0; depth < vertices * vertices; depth++) {

                    decisiontrees:
                    // iterate over all decision trees
                    for (DecisionTree tree : DecisionTree.enumerateTrees(depth, edges.size())) {

                        Map<Integer, List<Integer>> mstIndices = new HashMap<>();

                        for (List<Integer> permutation : Iterators.indexPermutations(edges.size())) {
                            int bucket = tree.classify(permutation);
                            // calculate mst indices here
                            
                            List<IndexedEdge<WeightedEdge>> permutedEdges = new ArrayList<>();
                            // Create graph with permuted edge weights
                            for (int index = 0; index < edges.size(); ++index) {
                            	permutedEdges.add(new IndexedEdge<WeightedEdge>(index, new WeightedEdge(
                            			edges.get(index).i, edges.get(index).j, permutation.get(index))));
                            }
                            
                            EdgeList<IndexedEdge<WeightedEdge>> mst = KruskalMST.compute(vertices, permutedEdges);

                            List<Integer> edgeIndices = new ArrayList<>();
                            for (IndexedEdge<WeightedEdge> edge : mst) {
                            	edgeIndices.add(edge.index);
                            }
                            Collections.sort(edgeIndices);
                            
                            if (mstIndices.containsKey(bucket)) {
                                // compare calculated indices with stored ones
                            	boolean unequal = true;
                            	List<Integer> otherIndices = mstIndices.get(bucket);
                            	if (otherIndices.size() == edgeIndices.size()) {
                            		unequal = false;
                            		for (int i = 0; i < edgeIndices.size(); ++i) {
                            			if (otherIndices.get(i) != edgeIndices.get(i)) {
                            				unequal = true;
                            				break;
                            			}
                            		}
                            	}
                                if (unequal)
                                    continue decisiontrees;
                            } else {
                                // store indices
                                mstIndices.put(bucket, edgeIndices);
                            }
                        }

                        // a perfect decision tree has been found
                        // TODO: Refactor this

                        int id = 0;
//                        System.out.print("[ ");
                        for (Iterators.IntTuple e : edges) {
                            if (e.i == e.j)
                                continue;
                            id |= 1 << (e.j * vertices + e.i);
                            id |= 1 << (e.i * vertices + e.j);
//                            System.out.print("(" + e.i + ", " + e.j + ") ");
                        }
//                        System.out.println("]");
//                        System.out.println(mstIndices + "\n");
//                        System.out.println(tree);
                        lookups.get(vertices).put(id, new DecisionTreeMSTLookup(tree, mstIndices));
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
        <E extends Comparable<? super E>> List<Integer> lookup(List<E> edges);
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
        public <E extends Comparable<? super E>> List<Integer> lookup(List<E> edges) {
            int bucket = tree.classify(edges);
            return mstIndices.get(bucket);
        }
    }
}
