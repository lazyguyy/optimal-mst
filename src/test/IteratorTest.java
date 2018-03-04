package test;

import util.decision.Iterators;
import util.graph.edge.WeightedEdge;

import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;

public class IteratorTest {

    public static void main(String[] args) {

        Iterators.ascendingIntPairs(5, (i, j) -> new WeightedEdge<>(i, j, 0)).forEach(System.out::println);

        /*System.out.println(Iterators.combinations(3, Arrays.asList(1, 2))
                .map(Object::toString)

                .collect(Collectors.joining(" ")));
*/
        List<Integer> base = Arrays.asList(1, 2, 3);
        List<List<Integer>> ps = new ArrayList<>();
        Iterators.powerSet(base).forEach(ps::add);
        System.out.println(ps);

        List<List<Integer>> perm = new ArrayList<>();
        Iterators.indexPermutations(3).forEach(perm::add);
        System.out.println(perm);
        
        //PrecomputedMSTCollection.computeUpTo(4);
    }
}
