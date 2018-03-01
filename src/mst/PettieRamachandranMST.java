package mst;

import util.queue.FibonacciHeap;
import util.queue.SoftHeap;
import util.queue.SoftPriorityQueue;
import util.graph.Graphs;
import util.decision.PrecomputedMSTCollection;
import util.graph.AdjacencyList;
import util.graph.edge.WeightedEdge;
import util.graph.edge.ContractedEdge;
import util.graph.edge.RenamedEdge;
import util.graph.EdgeList;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;

public class PettieRamachandranMST {

	
	
    public static EdgeList<WeightedEdge> compute(int vertices, Iterable<WeightedEdge> edges) {
        EdgeList<ContractedEdge> wrapper = new EdgeList<>();
        for (WeightedEdge edge : edges) {
            wrapper.append(new ContractedEdge(edge));
        }

        int maxsize = maxPartitionSize(vertices);
        PrecomputedMSTCollection decisionTrees = PrecomputedMSTCollection.computeUpTo(maxsize);

        return recurse(vertices, wrapper, decisionTrees);
    }

    private static EdgeList<WeightedEdge> recurse(int vertices, EdgeList<ContractedEdge> edges, PrecomputedMSTCollection decisionTrees) {
        if (edges.size() == 0)
            return new EdgeList<WeightedEdge>();
        
        int maxsize = maxPartitionSize(vertices);
        // Calculate the partitions
        PartitionWrapper partitions = partition(AdjacencyList.of(vertices, edges), maxsize, 0.125);
        
        EdgeList<RenamedEdge<ContractedEdge>> partitionMSFWithRenamedEdges = new EdgeList<>();

        // Calculate all MSFs for these subgraphs of fixed size using optimal decision trees
        for (Graphs.EdgesWithSize<RenamedEdge<ContractedEdge>> partition : partitions.subGraphs) {
//        	System.out.println(partition.edges);
        	List<RenamedEdge<ContractedEdge>> partitionEdges = new ArrayList<>();
        	partition.edges.forEach(partitionEdges::add);
            partitionMSFWithRenamedEdges.meld(decisionTrees.findMST(partition.size, partitionEdges));
        }

        EdgeList<ContractedEdge> partitionMSF = new EdgeList<>();

        for (RenamedEdge<ContractedEdge> edge : partitionMSFWithRenamedEdges) {
            partitionMSF.append(edge.original);
        }

        // Contract all partitions and calculate the MSF of the contracted graph
        // with Fredman and Tarjan's algorithm in O(m) time
        Graphs.EdgesWithSize<ContractedEdge> wrapper = Graphs.contract(vertices, partitionMSF, edges);

//        System.out.println(wrapper.edges);
        // Remove corrupted Edges
        EdgeList<WeightedEdge> denseCaseEdges = new EdgeList<>();
        for (ContractedEdge e : wrapper.edges) {
            if (!partitions.corruptedEdges.contains(e)) {
                denseCaseEdges.append(e.original);
            }
        }
//        System.out.println(denseCaseEdges);
        EdgeList<WeightedEdge> denseCaseMST = FredmanTarjanMST.compute(wrapper.size, denseCaseEdges);

        EdgeList<ContractedEdge> reducedEdges = new EdgeList<>();
        denseCaseMST.stream().map(e -> new ContractedEdge(e)).forEach(reducedEdges::append);
        partitions.corruptedEdges.forEach(reducedEdges::append);
        partitionMSF.forEach(reducedEdges::append);
        
        // Two Steps of Boruvka's algorithm TODO: boruvka in loop
        EdgeList<WeightedEdge> boruvkaEdges = new EdgeList<>();

        Set<ContractedEdge> forestEdges = Graphs.lightestEdgePerVertex(vertices, reducedEdges);
        Graphs.EdgesWithSize<ContractedEdge> contracted = Graphs.contract(vertices, forestEdges, reducedEdges);

        // extract original edges
        forestEdges.stream().map(e -> e.original).forEach(boruvkaEdges::append);

        forestEdges = Graphs.lightestEdgePerVertex(contracted.size, contracted.edges);
        contracted = Graphs.contract(contracted.size, forestEdges, contracted.edges);

        // extract original edges
        forestEdges.stream().map(e -> e.original).forEach(boruvkaEdges::append);

        EdgeList<WeightedEdge> mst = recurse(contracted.size, contracted.edges, decisionTrees);
        mst.meld(boruvkaEdges);
        return mst;       

    }
    
    private static int maxPartitionSize(int vertices) {
    	return Math.max(1, (int) Math.ceil(log2(log2(log2(vertices)))));
    }
    
    private static double log2(double x) {
    	return Math.log(x) / Math.ceil(2);
    }

    public static PartitionWrapper partition(AdjacencyList<ContractedEdge> edges, int maxsize, double errorRate) {
        boolean[] dead = new boolean[edges.size()];
        for (int i = 0; i < dead.length; ++i) {
        	dead[i] = false;
        }
        Set<ContractedEdge> corruptedEdges = new HashSet<>();
        List<Graphs.EdgesWithSize<RenamedEdge<ContractedEdge>>> partitions = new ArrayList<>();;
        // For each vertex find a partition that they are part of
        for (int current = 0; current < edges.size(); ++current) {
            if (dead[current])
                continue;
//            System.out.println("Growing partition for vertex " + current);
            dead[current] = true;
            SoftPriorityQueue<ContractedEdge> softHeap = FibonacciHeap.naturallyOrdered();
            for (ContractedEdge edge : edges.get(current)) {
                softHeap.insert(edge);
            }
            Set<Integer> currentPartition = new HashSet<>();
            EdgeList<ContractedEdge> partitionEdges = new EdgeList<>();
            currentPartition.add(current);
            // Grow the current partition as long as it is smaller than
            // max size and doesn't contain a dead (visited) vertex
            while (currentPartition.size() < maxsize) {
//            	System.out.println(currentPartition);
                ContractedEdge minEdge = softHeap.pop();
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
                for (ContractedEdge edge : edges.get(minEdge.to())) {
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
                ContractedEdge minEdge = softHeap.pop();
                if (!currentPartition.contains(minEdge.to())) { 
                	if(softHeap.corrupted().contains(minEdge)) {
                		corruptedEdges.add(minEdge);
                	}
                } else {
//                	System.out.println("Adding " + minEdge + " to partitionEdges.");
                    partitionEdges.append(minEdge);
                }
            }
            // Just to be sure
            // Add the subgraph to our list of subgraphs
            partitions.add(Graphs.renameVertices(partitionEdges));
        }
        return new PartitionWrapper(partitions, corruptedEdges);
    }

    public static final class PartitionWrapper {
        public final List<Graphs.EdgesWithSize<RenamedEdge<ContractedEdge>>> subGraphs;
        final Set<ContractedEdge> corruptedEdges;

        public PartitionWrapper(List<Graphs.EdgesWithSize<RenamedEdge<ContractedEdge>>> subGraphs, Set<ContractedEdge> corruptedEdges) {
            this.subGraphs = subGraphs;
            this.corruptedEdges = corruptedEdges;
        }
    }

}
