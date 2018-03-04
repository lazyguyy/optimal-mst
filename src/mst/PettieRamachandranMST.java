package mst;

import util.graph.Graph;
import util.graph.edge.DirectedEdge;
import util.queue.FibonacciHeap;
import util.queue.SoftHeap;
import util.queue.SoftPriorityQueue;
import util.graph.Graphs;
import util.decision.PrecomputedMSTCollection;
import util.graph.AdjacencyList;
import util.graph.edge.ContractedEdge;
import util.graph.edge.RenamedEdge;
import util.graph.EdgeList;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.HashSet;

public final class PettieRamachandranMST {

    public static <E extends DirectedEdge<E> & Comparable<? super E>>
            EdgeList<E> compute(int vertices, Iterable<E> edges) {
        EdgeList<ContractedEdge<E>> wrapper = new EdgeList<>();
        for (E edge : edges) {
            wrapper.append(new ContractedEdge<>(edge));
        }

        int maxsize = maxPartitionSize(vertices);
        PrecomputedMSTCollection decisionTrees = PrecomputedMSTCollection.computeUpTo(maxsize);

        return recurse(vertices, wrapper, decisionTrees);
    }

    private static <E extends DirectedEdge<E> & Comparable<? super E>>
            EdgeList<E> recurse(int vertices, EdgeList<ContractedEdge<E>> edges, PrecomputedMSTCollection decisionTrees) {
        if (edges.size() == 0)
            return new EdgeList<>();
        
        int maxsize = maxPartitionSize(vertices);
        // Calculate the partitions
        PartitionWrapper<E> partitions = partition(AdjacencyList.of(vertices, edges), maxsize, 0.125);
        
        EdgeList<RenamedEdge<ContractedEdge<E>>> partitionMSFWithRenamedEdges = new EdgeList<>();

        // Calculate all MSFs for these subgraphs of fixed size using optimal decision trees
        for (Graph<RenamedEdge<ContractedEdge<E>>> partition : partitions.subGraphs) {
//        	System.out.println(partition.edges);
        	List<RenamedEdge<ContractedEdge<E>>> partitionEdges = new ArrayList<>();
        	partition.edges.forEach(partitionEdges::add);
            partitionMSFWithRenamedEdges.meld(decisionTrees.findMST(partition.vertices, partitionEdges));
        }

        EdgeList<ContractedEdge<E>> partitionMSF = new EdgeList<>();
        partitionMSFWithRenamedEdges.forEach(e -> partitionMSF.append(e.original));

        // Contract all partitions and calculate the MSF of the contracted graph
        // with Fredman and Tarjan's algorithm in O(m) time
        EdgeList<ContractedEdge<ContractedEdge<E>>> wrappedEdges = new EdgeList<>();
        edges.forEach(e -> wrappedEdges.append(new ContractedEdge<>(e)));
        Graph<ContractedEdge<ContractedEdge<E>>> wrapper = Graphs.contract(vertices, partitionMSF, wrappedEdges);

//        System.out.println(wrapper.edges);
        // Remove corrupted Edges
        EdgeList<ContractedEdge<ContractedEdge<E>>> denseCaseEdges = new EdgeList<>();
        for (ContractedEdge<ContractedEdge<E>> e : wrapper.edges) {
            if (!partitions.corruptedEdges.contains(e)) {
                denseCaseEdges.append(e);
            }
        }
//        System.out.println(denseCaseEdges);
        EdgeList<ContractedEdge<ContractedEdge<E>>> denseCaseMST = FredmanTarjanMST.compute(wrapper.vertices, denseCaseEdges);

        EdgeList<ContractedEdge<E>> reducedEdges = new EdgeList<>();
        denseCaseMST.stream().map(e -> e.original).forEach(reducedEdges::append);
        partitions.corruptedEdges.forEach(reducedEdges::append);
        partitionMSF.forEach(reducedEdges::append);
        
        // Two Steps of Boruvka's algorithm
        EdgeList<E> boruvkaEdges = new EdgeList<>();
        Set<ContractedEdge<E>> forestEdges;
        Graph<ContractedEdge<E>> contractTwice = new Graph<>(vertices, reducedEdges);

        for (int boruvkaIterations = 0; boruvkaIterations < 2; boruvkaIterations++) {
            forestEdges = Graphs.lightestEdgePerVertex(contractTwice.vertices, contractTwice.edges);
            contractTwice = Graphs.contract(contractTwice.vertices, forestEdges, contractTwice.edges);

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
    	return Math.log(x) / Math.ceil(2);
    }

    private static <E extends DirectedEdge<E> & Comparable<? super E>>
            PartitionWrapper<E> partition(AdjacencyList<ContractedEdge<E>> edges, int maxsize, double errorRate) {
        boolean[] dead = new boolean[edges.size()];
        for (int i = 0; i < dead.length; ++i) {
        	dead[i] = false;
        }
        Set<ContractedEdge<E>> corruptedEdges = new HashSet<>();
        List<Graph<RenamedEdge<ContractedEdge<E>>>> partitions = new ArrayList<>();
        // For each vertex find a partition that they are part of
        for (int current = 0; current < edges.size(); ++current) {
            if (dead[current])
                continue;
//            System.out.println("Growing partition for vertex " + current);
            dead[current] = true;
            SoftPriorityQueue<ContractedEdge<E>> softHeap = SoftHeap.naturallyOrdered(errorRate);
            edges.get(current).forEach(softHeap::insert);

            Set<Integer> currentPartition = new HashSet<>();
            EdgeList<ContractedEdge<E>> partitionEdges = new EdgeList<>();
            currentPartition.add(current);
            // Grow the current partition as long as it is smaller than
            // max size and doesn't contain a dead (visited) vertex
            while (currentPartition.size() < maxsize) {
//            	System.out.println(currentPartition);
                ContractedEdge<E> minEdge = softHeap.pop();
//                System.out.println(minEdge);
                // Extract the minimum Edge leading to a Vertex 
                // which is not part of the current partition
                while (currentPartition.contains(minEdge.to())) {
                    // In case the edge doesn't lead to a new vertex
                    // it is part of the subgraph induced by the 
                    // current partition
//                	System.out.println("Adding " + minEdge + " to partitionEdges.");
                    partitionEdges.append(minEdge);
                    minEdge = softHeap.pop();
                }
                currentPartition.add(minEdge.to());
                partitionEdges.append(minEdge);
                if (dead[minEdge.to()]) {
                    break;
                }
                for (ContractedEdge<E> edge : edges.get(minEdge.to())) {
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
                ContractedEdge<E> minEdge = softHeap.pop();
                if (!currentPartition.contains(minEdge.to())) { 
                	if(softHeap.corrupted().contains(minEdge)) {
                		corruptedEdges.add(minEdge);
                	}
                } else {
//                	System.out.println("Adding " + minEdge + " to partitionEdges.");
                    partitionEdges.append(minEdge);
                }
            }
            // Add the subgraph to our list of subgraphs
            partitions.add(Graphs.renameVertices(partitionEdges));
        }
        return new PartitionWrapper<>(partitions, corruptedEdges);
    }

    public static final class PartitionWrapper<E extends DirectedEdge<E> & Comparable<? super E>> {
        final List<Graph<RenamedEdge<ContractedEdge<E>>>> subGraphs;
        final Set<ContractedEdge<E>> corruptedEdges;

        PartitionWrapper(List<Graph<RenamedEdge<ContractedEdge<E>>>> subGraphs, Set<ContractedEdge<E>> corruptedEdges) {
            this.subGraphs = subGraphs;
            this.corruptedEdges = corruptedEdges;
        }
    }

}
