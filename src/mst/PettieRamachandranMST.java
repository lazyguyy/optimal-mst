package mst;

import util.queue.SoftHeap;
import util.graph.Graphs;
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

        return recurse(vertices, wrapper);

    }

    private static EdgeList<WeightedEdge> recurse(int vertices, EdgeList<ContractedEdge> edges) {
        if (edges.size() == 0)
            return new EdgeList<WeightedEdge>();
        int maxsize = (int)Math.ceil(Math.log(Math.log(Math.log(vertices)/Math.log(2))/Math.log(2))/Math.log(2));

        // Calculate the partitions
        PartitionWrapper partitions = partition(vertices, AdjacencyList.of(vertices, edges), maxsize, 0.125);
        
        EdgeList<RenamedEdge<ContractedEdge>> partitionMSFWithRenamedEdges = new EdgeList<>();

        // Calculate all MSFs for these subgraphs of fixed size using optimal decision trees
        for (AdjacencyList<RenamedEdge<ContractedEdge>> partition : partitions.subGraphs) {
            //partitionMSFWithRenamedEdges.meld(DecisionTree.optimalMST(partition));
        }

        EdgeList<ContractedEdge> partitionMSF = new EdgeList<>();

        for (RenamedEdge<ContractedEdge> edge : partitionMSFWithRenamedEdges) {
            partitionMSF.append(edge.original);
        }

        // Contract all partitions and calculate the MSF of the contracted graph
        // with Fredman and Tarjan's algorithm in O(m) time
        Graphs.ContractedWrapper wrapper = Graphs.contract(vertices, partitionMSF, edges);

        // Remove corrupted Edges
        EdgeList<WeightedEdge> denseCaseEdges = new EdgeList<>();
        for (ContractedEdge e : wrapper.edges) {
            if (!partitions.corruptedEdges.contains(e)) {
                denseCaseEdges.append(e.original);
            }
        }

        EdgeList<WeightedEdge> denseCaseMST = FredmanTarjanMST.compute(wrapper.size, denseCaseEdges);

        EdgeList<ContractedEdge> reducedEdges = new EdgeList<>();
        denseCaseMST.stream().map(e -> new ContractedEdge(e)).forEach(reducedEdges::append);
        partitions.corruptedEdges.forEach(reducedEdges::append);
        partitionMSF.forEach(reducedEdges::append);
        
        // Two Steps of Boruvka's algorithm
        EdgeList<WeightedEdge> boruvkaEdges = new EdgeList<>();

        Set<ContractedEdge> forestEdges = Graphs.lightestEdgePerVertex(vertices, reducedEdges);
        Graphs.ContractedWrapper contracted = Graphs.contract(vertices, forestEdges, reducedEdges);

        // extract original edges
        forestEdges.stream().map(e -> e.original).forEach(boruvkaEdges::append);

        forestEdges = Graphs.lightestEdgePerVertex(contracted.size, contracted.edges);
        contracted = Graphs.contract(contracted.size, forestEdges, contracted.edges);

        // extract original edges
        forestEdges.stream().map(e -> e.original).forEach(boruvkaEdges::append);

        EdgeList<WeightedEdge> mst = recurse(contracted.size, contracted.edges);
        mst.meld(boruvkaEdges);
        return mst;       

    }

    private static PartitionWrapper partition(int vertices, AdjacencyList<ContractedEdge> edges, int maxsize, double errorRate) {
        boolean[] dead = new boolean[vertices];
        Set<ContractedEdge> corruptedEdges = new HashSet<>();
        SoftHeap<ContractedEdge> softHeap = SoftHeap.naturallyOrdered(errorRate);
        List<AdjacencyList<RenamedEdge<ContractedEdge>>> partitions = new ArrayList<>();;
        // For each vertex find a partition that they are part of
        for (int current = 0; current < vertices; ++current) {
            if (dead[current])
                continue;
            dead[current] = true;
            for (ContractedEdge edge : edges.get(current)) {
                softHeap.insert(edge);
            }
            Set<Integer> currentPartition = new HashSet<>();
            List<ContractedEdge> partitionEdges = new ArrayList<>();
            currentPartition.add(current);
            // Grow the current partition as long as it is smaller than
            // max size and doesn't contain a dead (visited) vertex
            while (currentPartition.size() < maxsize) {
                ContractedEdge minEdge = softHeap.pop();
                // Extract the minimum Edge leading to a Vertex 
                // which is not part of the current partition
                while (currentPartition.contains(minEdge.to())) {
                    // In case the edge doesn't lead to a new vertex
                    // it is part of the subgraph induced by the 
                    // current partition
                    partitionEdges.add(minEdge);
                    minEdge = softHeap.pop();
                }
                currentPartition.add(minEdge.to());
                if (dead[minEdge.to()]) {
                    break;
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
                    corruptedEdges.add(minEdge);
                } else {
                    partitionEdges.add(minEdge);
                }
            }
            // Just to be sure
            softHeap.clear();
            // Add the subgraph to our list of subgraphs
            partitions.add(Graphs.renameVertices(partitionEdges));
        }
        return new PartitionWrapper(partitions, corruptedEdges);
    }

    private static final class PartitionWrapper {
        final List<AdjacencyList<RenamedEdge<ContractedEdge>>> subGraphs;
        final Set<ContractedEdge> corruptedEdges;

        public PartitionWrapper(List<AdjacencyList<RenamedEdge<ContractedEdge>>> subGraphs, Set<ContractedEdge> corruptedEdges) {
            this.subGraphs = subGraphs;
            this.corruptedEdges = corruptedEdges;
        }
    }

}
