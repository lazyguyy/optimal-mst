package mst;

import util.disjointset.DisjointSet;
import util.disjointset.OptimalUnionFind;
import util.graph.EdgeList;
import util.graph.edge.DirectedEdge;

import java.util.ArrayList;
import java.util.Collections;

public final class KruskalMST {

    public static <T, E extends DirectedEdge<T, E> & Comparable<? super E>> EdgeList<E> compute(int vertices, Iterable<E> edges) {

        ArrayList<E> sorted = new ArrayList<>();
        edges.forEach(sorted::add);
        Collections.sort(sorted);

        DisjointSet ds = new OptimalUnionFind(vertices);
        EdgeList<E> result = new EdgeList<>();

        for (E e : sorted) {
            if (ds.find(e.from()) == ds.find(e.to()))
                continue;

            result.append(e);
            ds.union(e.from(), e.to());
        }

        return result;
    }
}
