package test;

import util.decision.Iterators;
import util.graph.edge.WeightedEdge;

import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;

public class IteratorTest {

    public static void main(String[] args) {

        Iterators.ascendingIntPairs(3, (i, j) -> new WeightedEdge<>(i, j, 0)).forEach(System.out::println);

        List<Integer> base = Arrays.asList(1, 2, 3);
        List<List<Integer>> ps = new ArrayList<>();
        Iterators.powerSet(base).forEach(ps::add);
        System.out.println(ps);

        List<Integer> base2 = Arrays.asList(1, 20, 4);
        List<List<Integer>> ps2 = new ArrayList<>();
        Iterators.combinations(2, base2).forEach(ps2::add);
        System.out.println(ps2);

        List<List<Integer>> perm = new ArrayList<>();
        Iterators.indexPermutations(4).forEach(perm::add);
        System.out.println(perm);
        
        //PrecomputedMSTCollection.computeUpTo(4);
    }
}
