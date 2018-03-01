package test;

import mst.*;
import util.decision.PrecomputedMSTCollection;
import util.graph.AdjacencyList;
import util.graph.EdgeList;
import util.graph.Graphs;
import util.graph.MinimumSpanningTreeAlgorithm;
import util.graph.edge.WeightedEdge;
import util.graph.edge.RenamedEdge;
import util.graph.edge.ContractedEdge;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.util.Arrays;

public class MSTTest {

    public static void main(String[] args) {
        WeightedEdge[] edges = {
                new WeightedEdge(0, 1, 5),
                new WeightedEdge(1, 2, 2),
                new WeightedEdge(2, 3, 6),
                new WeightedEdge(3, 4, 3),
                new WeightedEdge(4, 5, 7),
                new WeightedEdge(5, 0, 4),
        };
        MinimumSpanningTreeAlgorithm[] algorithms = {
                BoruvkaMST::compute,
                PrimMST::compute,
                KruskalMST::compute,
                FredmanTarjanMST::compute,
                PettieRamachandranMST::compute,
        };

        for (MinimumSpanningTreeAlgorithm algorithm : algorithms)
            System.out.println(algorithm.findMST(6, Arrays.asList(edges)).weight());
        
//        PettieRamachandranMST.PartitionWrapper partitions = PettieRamachandranMST.partition(AdjacencyList.of(100, readGraph()), 5, 0.5);
//        for (Graphs.EdgesWithSize<RenamedEdge<ContractedEdge>> partition : partitions.subGraphs) {
//        	System.out.println(partition.edges);
//        }
    }
}
