package test;

import mst.*;
import util.graph.MinimumSpanningTreeAlgorithm;
import util.graph.edge.WeightedEdge;
import java.util.Arrays;
import java.util.List;

public class MSTTest {

    public static void main(String[] args) {
        List<WeightedEdge<Integer>> edges = Arrays.asList(
                new WeightedEdge<>(0, 1, 5),
                new WeightedEdge<>(1, 2, 2),
                new WeightedEdge<>(2, 3, 6),
                new WeightedEdge<>(3, 4, 3),
                new WeightedEdge<>(4, 5, 7),
                new WeightedEdge<>(5, 0, 4)
                );
        List<MinimumSpanningTreeAlgorithm<WeightedEdge<Integer>>> algorithms = Arrays.asList(
                BoruvkaMST::compute,
                PrimMST::compute,
                KruskalMST::compute,
                FredmanTarjanMST::compute,
                PettieRamachandranMST::compute);

        for (MinimumSpanningTreeAlgorithm<WeightedEdge<Integer>> algorithm : algorithms)
            System.out.println(algorithm.findMST(6, edges).stream().mapToInt(WeightedEdge::weight).sum());
        
//        PettieRamachandranMST.PartitionWrapper partitions = PettieRamachandranMST.partition(AdjacencyList.of(100, readGraph()), 5, 0.5);
//        for (Graphs.EdgesWithSize<RenamedEdge<ContractedEdge>> partition : partitions.subGraphs) {
//        	System.out.println(partition.edges);
//        }
    }
}
