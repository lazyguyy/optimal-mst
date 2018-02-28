package mst;

import util.queue.SoftHeap;
import util.graph.Graphs;
import util.graph.AdjacencyList;
import util.graph.edge.WeightedEdge;
import util.graph.edge.ContractedEdge;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;

public class PettieRamachandranMST {

    private PartitionWrapper partition(int vertices, AdjacencyList<ContractedEdge> edges, int maxsize, double errorRate) {
        boolean[] dead = new boolean[vertices];
        Set<ContractedEdge> corruptedEdges = new HashSet<>();
        SoftHeap<ContractedEdge> softHeap = SoftHeap.naturallyOrdered(errorRate);
        List<AdjacencyList<ContractedEdge>> partitions = new ArrayList<>();;
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

    private final class PartitionWrapper {
        final List<AdjacencyList<ContractedEdge>> subGraphs;
        final Set<ContractedEdge> corruptedEdges;

        public PartitionWrapper(List<AdjacencyList<ContractedEdge>> subGraphs, Set<ContractedEdge> corruptedEdges) {
            this.subGraphs = subGraphs;
            this.corruptedEdges = corruptedEdges;
        }
    }

}
