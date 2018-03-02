package main;

import mst.*;
import util.graph.EdgeList;
import util.graph.MinimumSpanningTreeAlgorithm;
import util.graph.edge.WeightedEdge;
import util.log.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public class Launcher {

    private static Map<String, MinimumSpanningTreeAlgorithm<WeightedEdge>> algorithms = new HashMap<>();
    static {
        algorithms.put("prim", PrimMST::compute);
        algorithms.put("kruskal", KruskalMST::compute);
        algorithms.put("boruvka", BoruvkaMST::compute);
        algorithms.put("ft", FredmanTarjanMST::compute);
        algorithms.put("pr", PettieRamachandranMST::compute);
    }

    private static void printUsage() {
        String algs = String.join(" | ", algorithms.keySet());
        System.err.printf("Allowed arguments: %s\n", algs);
    }

    public static void main(String[] args) {

        // disable / enable logging on root logger
        Logger.setActive(true);

        MinimumSpanningTreeAlgorithm<WeightedEdge> alg = PettieRamachandranMST::compute;
        if (args.length == 1) {
            String first = args[0];
            if (algorithms.containsKey(first)) {
                printUsage();
                return;
            }
            alg = algorithms.get(first);
        } else if (args.length > 1) {
            printUsage();
            return;
        }

        int vertices = 0;
        EdgeList<WeightedEdge> edges = new EdgeList<>();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(System.in))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] words = line.split(" ");
                int from = Integer.parseInt(words[0]);
                int to = Integer.parseInt(words[1]);
                double weight = Double.parseDouble(words[2]);
                edges.append(new WeightedEdge(from, to, weight));
                vertices = Math.max(vertices, Math.max(from, to) + 1);
            }
        } catch (IOException e) {
            System.err.println("A fatal error occurred");
            return;
        }

        EdgeList<WeightedEdge> mst = alg.findMST(vertices, edges);

        for (WeightedEdge edge : mst) {
            System.out.printf("%s %s  %s\n", edge.from(), edge.to(), edge.weight());
        }
    }
}
