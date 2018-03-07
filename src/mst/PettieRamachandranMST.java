package mst;

import util.decision.PrecomputedMSTCollection;
import util.graph.AdjacencyList;
import util.graph.EdgeList;
import util.graph.Graph;
import util.graph.Graphs;
import util.graph.edge.ContractedEdge;
import util.graph.edge.DirectedEdge;
import util.graph.edge.RenamedEdge;
import util.log.Logger;
import util.queue.SoftHeap;
import util.queue.SoftPriorityQueue;

import java.util.*;

public final class PettieRamachandranMST {

    public static <T extends Comparable<? super T>, E extends DirectedEdge<T, E> & Comparable<? super E>>
            EdgeList<E> compute(int vertices, Iterable<E> edges) {

        EdgeList<ContractedEdge<T, E>> wrapper = new EdgeList<>(edges).map(ContractedEdge::new);

        int maxsize = maxPartitionSize(vertices);
        PrecomputedMSTCollection decisionTrees = PrecomputedMSTCollection.computeUpTo(maxsize);

        return recurse(vertices, wrapper, decisionTrees);
    }

    private static <T extends Comparable<? super T>, E extends DirectedEdge<T, E> & Comparable<? super E>>
            EdgeList<E> recurse(int vertices, EdgeList<ContractedEdge<T, E>> edges, PrecomputedMSTCollection decisionTrees) {

        if (edges.size() == 0)
            return new EdgeList<>();
        
        int maxsize = maxPartitionSize(vertices);
        // Calculate the partitions
        PartitionWrapper<T, E> partitions = partition(AdjacencyList.of(vertices, edges), maxsize, 0.125);
        
        EdgeList<RenamedEdge<T, ContractedEdge<T, E>>> partitionMSFWithRenamedEdges = new EdgeList<>();

        // Calculate all MSFs for these subgraphs of fixed size using optimal decision trees
        for (Graph<RenamedEdge<T, ContractedEdge<T, E>>> partition : partitions.subGraphs) {
            Logger.logf("Parition subgraph: %s", partition.edges);
            partitionMSFWithRenamedEdges.meld(decisionTrees.findMST(partition.vertices, partition.edges.collect(ArrayList::new)));
        }

        EdgeList<ContractedEdge<T, E>> partitionMSF = new EdgeList<>();
        partitionMSFWithRenamedEdges.forEach(e -> partitionMSF.append(e.original));

        // Contract all partitions and calculate the MSF of the contracted graph
        // with Fredman and Tarjan's algorithm in O(m) time
        Graph<ContractedEdge<T, ContractedEdge<T, E>>> contractedPartitions = Graphs.contract(vertices, partitionMSF, edges);

        // Remove corrupted Edges
        EdgeList<ContractedEdge<T, ContractedEdge<T, E>>> denseCaseEdges = new EdgeList<>();
        for (ContractedEdge<T, ContractedEdge<T, E>> e : contractedPartitions.edges) {
            if (!partitions.corruptedEdges.contains(e.original)) {
                denseCaseEdges.append(e);
            }
        }

        Logger.logf("Dense case edges: %s", denseCaseEdges);
        EdgeList<ContractedEdge<T, ContractedEdge<T, E>>> denseCaseMST = FredmanTarjanMST.compute(contractedPartitions.vertices, denseCaseEdges);

        EdgeList<ContractedEdge<T, E>> reducedEdges = new EdgeList<>();
        denseCaseMST.stream().map(e -> e.original).forEach(reducedEdges::append);
        partitions.corruptedEdges.forEach(reducedEdges::append);
        partitionMSF.forEach(reducedEdges::append);
        
        // Two Steps of Boruvka's algorithm
        EdgeList<E> boruvkaEdges = new EdgeList<>();
        Set<ContractedEdge<T, E>> forestEdges;
        Graph<ContractedEdge<T, E>> contractTwice = new Graph<>(vertices, reducedEdges);

        for (int boruvkaIterations = 0; boruvkaIterations < 2; boruvkaIterations++) {
            forestEdges = Graphs.lightestEdgePerVertex(contractTwice.vertices, contractTwice.edges);
            contractTwice = Graphs.flatten(Graphs.contract(contractTwice.vertices, forestEdges, contractTwice.edges));

            // extract original edges
            forestEdges.stream().map(e -> e.original).forEach(boruvkaEdges::append);
        }

        EdgeList<E> mst = recurse(contractTwice.vertices, contractTwice.edges, decisionTrees);
        mst.meld(boruvkaEdges);
        return mst;       

    }
    
    private static int maxPartitionSize(int vertices) {
    	return Math.max(1, (int) Math.ceil(log2(log2(log2(vertices)))));
    }
    
    private static double log2(double x) {
    	return Math.log(x) / Math.log(2);
    }

    private static <T, E extends DirectedEdge<T, E> & Comparable<? super E>>
            PartitionWrapper<T, E> partition(AdjacencyList<ContractedEdge<T, E>> edges, int maxsize, double errorRate) {

        boolean[] dead = new boolean[edges.size()];
        for (int i = 0; i < dead.length; ++i) {
        	dead[i] = false;
        }

        Set<ContractedEdge<T, E>> corruptedEdges = new HashSet<>();
        List<Graph<RenamedEdge<T, ContractedEdge<T, E>>>> partitions = new ArrayList<>();

        // For each vertex find a partition that they are part of
        for (int current = 0; current < edges.size(); ++current) {
            if (dead[current])
                continue;

            Logger.logf("Growing partition for vertex %s", current);
            dead[current] = true;
            SoftPriorityQueue<ContractedEdge<T, E>> softHeap = SoftHeap.naturallyOrdered(errorRate);
            edges.get(current).forEach(softHeap::insert);

            Set<Integer> currentPartition = new HashSet<>();
            EdgeList<ContractedEdge<T, E>> partitionEdges = new EdgeList<>();
            currentPartition.add(current);
            // Grow the current partition as long as it is smaller than
            // max size and doesn't contain a dead (visited) vertex
            while (currentPartition.size() < maxsize) {
            	Logger.logf("Current partition: %s", currentPartition);
                ContractedEdge<T, E> minEdge = softHeap.pop();
                Logger.logf("min edge: %s", minEdge);
                // Extract the minimum Edge leading to a Vertex 
                // which is not part of the current partition
                while (currentPartition.contains(minEdge.to())) {
                    // In case the edge doesn't lead to a new vertex
                    // it is part of the subgraph induced by the 
                    // current partition
                 	Logger.logf("Adding %s to partitionEdges.", minEdge);
                    partitionEdges.append(minEdge);
                    minEdge = softHeap.pop();
                }
                currentPartition.add(minEdge.to());
                partitionEdges.append(minEdge);
                if (dead[minEdge.to()]) {
                    break;
                }
                for (ContractedEdge<T, E> edge : edges.get(minEdge.to())) {
                	if (!currentPartition.contains(edge.to()))
                		softHeap.insert(edge);
                }
                dead[minEdge.to()] = true;
            }
            // Append the remaining edges with exactly one endpoint in
            // the current partition to the list of corrupted edges
            // and add all of the other edges to the list of edges that 
            // are part of the subgraph induced by the current partition
            while (softHeap.size() > 0) {
                ContractedEdge<T, E> minEdge = softHeap.pop();
                if (!currentPartition.contains(minEdge.to())) { 
                	if(softHeap.corrupted().contains(minEdge)) {
                		corruptedEdges.add(minEdge);
                	}
                } else {
                    Logger.logf("Adding %s to partitionEdges.", minEdge);
                    partitionEdges.append(minEdge);
                }
            }
            // Add the subgraph to our list of subgraphs
            partitions.add(Graphs.renameVertices(partitionEdges));
        }
        return new PartitionWrapper<>(partitions, corruptedEdges);
    }

    public static final class PartitionWrapper<T, E extends DirectedEdge<T, E> & Comparable<? super E>> {
        final List<Graph<RenamedEdge<T, ContractedEdge<T, E>>>> subGraphs;
        final Set<ContractedEdge<T, E>> corruptedEdges;

        PartitionWrapper(List<Graph<RenamedEdge<T, ContractedEdge<T, E>>>> subGraphs, Set<ContractedEdge<T, E>> corruptedEdges) {
            this.subGraphs = subGraphs;
            this.corruptedEdges = corruptedEdges;
        }
    }
}
