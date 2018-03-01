package util.decision;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class Iterators {

    private Iterators() {}

    // returns all (i, j) so that 0 <= i < j < max
    public static Iterable<IntTuple> ascendingIntPairs(int max) {
        return () -> IntStream.range(0, max)
            .boxed()
            .flatMap(j -> IntStream.range(0, j)
                .boxed()
                .map(i -> new IntTuple(i, j)))
            .iterator();
    }

    public static <E> Stream<List<E>> combinations(int length, List<E> base) {
        return combinations(length, base, new ArrayList<>());
    }

    private static <E> Stream<List<E>> combinations(int length, List<E> base, List<E> accum) {
        return length == 0 ? Stream.of(accum) : base.stream()
            .flatMap(e -> {
                List<E> current = new ArrayList<>(accum);
                current.add(e);
                return combinations(length - 1, base, current);
            });
    }

    public static <E> Iterable<List<E>> powerSet(List<E> base) {
        return () -> combinations(base.size(), Arrays.asList(false, true))
            .map(l -> {
                List<E> set = new ArrayList<>();
                for (int i = 0; i < base.size(); i++)
                    if (l.get(i))
                        set.add(base.get(i));
                return set;
            })
            .iterator();
    }

    public static Iterable<List<Integer>> indexPermutations(int max) {
        List<Integer> indices = IntStream.range(0, max).boxed().collect(Collectors.toCollection(ArrayList::new));
        return permutations(indices);
    }

    public static <E> Iterable<List<E>> permutations(List<E> base) {
        return () -> permutations(base, base.size()).iterator();
    }

    private static <E> Stream<List<E>> permutations(List<E> base, int len) {
        return len == 1 ? Stream.of(base) : IntStream.range(0, len)
            .boxed()
            .flatMap(i -> {
                List<E> swapped = new ArrayList<>(base);
                swapped.set(i, base.get(len - 1));
                swapped.set(len - 1, base.get(i));
                return permutations(swapped, len - 1);
            });
    }

    public static class IntTuple {
        public final int i, j;

        public IntTuple(int i, int j) {
            this.i = i;
            this.j = j;
        }
    }
}
