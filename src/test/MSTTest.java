package test;

import mst.*;
import util.graph.MinimumSpanningTreeAlgorithm;
import util.graph.edge.WeightedEdge;

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
                FredmanTarjanMST::compute
        };

        for (MinimumSpanningTreeAlgorithm algorithm : algorithms)
            System.out.println(algorithm.findMST(6, Arrays.asList(edges)));
    }
}
