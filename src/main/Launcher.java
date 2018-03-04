package main;

import mst.*;
import util.graph.EdgeList;
import util.graph.MinimumSpanningTreeAlgorithm;
import util.graph.edge.WeightedEdge;
import util.log.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Launcher {

    private static Map<String, MinimumSpanningTreeAlgorithm<WeightedEdge<Double>>> algorithms = new HashMap<>();
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
        Logger.setActive(false);

        List<MinimumSpanningTreeAlgorithm<WeightedEdge<Double>>> algs = new ArrayList<>();
        if (args.length == 0)
            algs.add(PettieRamachandranMST::compute);

        for (String s : args) {
            if (algorithms.containsKey(s)) {
                algs.add(algorithms.get(s));
                continue;
            }
            if ("log".equals(s)) {
                Logger.setActive(true);
                continue;
            }
            printUsage();
            return;
        }

        int vertices = 0;
        EdgeList<WeightedEdge<Double>> edges = new EdgeList<>();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(System.in))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] words = line.split(" ");
                int from = Integer.parseInt(words[0]);
                int to = Integer.parseInt(words[1]);
                double weight = Double.parseDouble(words[2]);
                edges.append(new WeightedEdge<>(from, to, weight));
                vertices = Math.max(vertices, Math.max(from, to) + 1);
            }
        } catch (IOException e) {
            System.err.println("A fatal error occurred");
            return;
        }

        for (MinimumSpanningTreeAlgorithm<WeightedEdge<Double>> alg : algs) {
            long now = System.currentTimeMillis();
            EdgeList<WeightedEdge<Double>> mst = alg.findMST(vertices, edges);

            for (WeightedEdge<Double> edge : mst) {
                System.out.printf("%s %s  %s\n", edge.from(), edge.to(), edge.weight());
            }
            System.out.printf("Total weight: %s\n", mst.stream().mapToDouble(WeightedEdge::weight).sum());
            System.out.printf("Took %s ms\n\n", System.currentTimeMillis() - now);
        }
    }
}
