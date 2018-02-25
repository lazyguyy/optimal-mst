package mst;

import util.disjointset.DisjointSet;
import util.disjointset.OptimalUnionFind;
import util.graph.EdgeList;
import util.graph.WeightedEdge;

import java.util.ArrayList;
import java.util.Collections;

public class KruskalMST {

    public static EdgeList compute(int vertices, Iterable<WeightedEdge> edges) {

        ArrayList<WeightedEdge> sorted = new ArrayList<>();
        edges.forEach(sorted::add);
        Collections.sort(sorted);

        DisjointSet ds = new OptimalUnionFind(vertices);
        EdgeList result = new EdgeList();

        for (WeightedEdge e : sorted) {
            if (ds.find(e.from) == ds.find(e.to))
                continue;

            result.append(e);
            ds.union(e.from, e.to);
        }

        return result;
    }
}
