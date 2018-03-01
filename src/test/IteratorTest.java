package test;

import util.decision.Iterators;

import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;
import java.util.stream.Collectors;

public class IteratorTest {

    public static void main(String[] args) {
        System.out.println(Iterators.combinations(3, Arrays.asList(1, 2))
                .map(Object::toString)
                .collect(Collectors.joining(" ")));

        List<Integer> base = Arrays.asList(1, 2, 3);
        List<List<Integer>> ps = new ArrayList<>();
        Iterators.powerSet(base).forEach(ps::add);
        System.out.println(ps);

        List<List<Integer>> perm = new ArrayList<>();
        Iterators.indexPermutations(3).forEach(perm::add);
        System.out.println(perm);
    }
}
