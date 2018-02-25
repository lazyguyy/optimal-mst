package test;

import mst.*;
import util.graph.EdgeList;
import util.graph.Graphs;
import util.graph.MinimumSpanningTreeAlgorithm;
import util.graph.WeightedEdge;

import java.util.Arrays;

public class MSTTest {

    public static void main(String[] args) {
        WeightedEdge[] edges = {
                new WeightedEdge(0, 1, 10),
                new WeightedEdge(0, 2, 9),
                new WeightedEdge(0, 3, 8),
                new WeightedEdge(0, 4, 7),
                new WeightedEdge(1, 2, 6),
                new WeightedEdge(1, 3, 5),
                new WeightedEdge(1, 4, 4),
                new WeightedEdge(2, 3, 3),
                new WeightedEdge(2, 4, 2),
                new WeightedEdge(3, 4, 1)
        };
        MinimumSpanningTreeAlgorithm algorithm = KruskalMST::compute;
        System.out.println(algorithm.findMST(5, Arrays.asList(edges)));

        WeightedEdge[] edges2 = {
                new WeightedEdge(0, 1, 10),
                new WeightedEdge(0, 1, 9),
                new WeightedEdge(1, 0, 8),
                new WeightedEdge(1, 0, 7),
                new WeightedEdge(1, 2, 6),
                new WeightedEdge(2, 1, 5),
                new WeightedEdge(1, 2, 4),
                new WeightedEdge(1, 0, 3),
                new WeightedEdge(2, 3, 2),
                new WeightedEdge(3, 2, 1)
        };
        System.out.println(Graphs.removeDuplicates(4, new EdgeList(Arrays.asList(edges2))));
    }
}
