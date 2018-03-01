package test;

import mst.*;
import util.graph.AdjacencyList;
import util.graph.EdgeList;
import util.graph.MinimumSpanningTreeAlgorithm;
import util.graph.edge.WeightedEdge;
import util.graph.edge.RenamedEdge;
import util.graph.edge.ContractedEdge;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
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
            System.out.println(algorithm.findMST(6, Arrays.asList(edges)).weight());
        
        PettieRamachandranMST.PartitionWrapper partitions = PettieRamachandranMST.partition(AdjacencyList.of(100, readGraph()), 5, 0.5);
        for (AdjacencyList<RenamedEdge<ContractedEdge>> partition : partitions.subGraphs) {
        	System.out.println(partition);
        }
    }
    
    public static EdgeList<ContractedEdge> readGraph() {
    	BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
    	EdgeList<ContractedEdge> edges = new EdgeList<ContractedEdge>();
    	
    	String line;
    	try {
			while ((line = br.readLine()) != null) {
				if (line.equals("q"))
					break;
				String[] words = line.split(" ");
				int from = Integer.parseInt(words[0]);
				int to = Integer.parseInt(words[1]);
				int weight = Integer.parseInt(words[2]);
				edges.append(new ContractedEdge(new WeightedEdge(from, to, weight)));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
    	return edges;
    }
}
