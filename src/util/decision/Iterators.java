package util.decision;

import java.util.*;
import java.util.function.BiFunction;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

// todo spam final
public class Iterators {

    private Iterators() {}

    // returns all (i, j) so that 0 <= i < j < max
    public static <T> Iterable<T> ascendingIntPairs(int max, BiFunction<Integer, Integer, T> producer) {
        return () -> new PairIterator<>(max, producer);
    }

    private static final class PairIterator<T> implements Iterator<T> {
        private int i;
        private int j;
        private final int max;
        private final BiFunction<Integer, Integer, T> producer;

        PairIterator(int max, BiFunction<Integer, Integer, T> producer) {
            this.max = max;
            this.i = 0;
            this.j = 1;
            this.producer = producer;
        }

        @Override
        public boolean hasNext() {
            return j < max;
        }

        @Override
        public T next() {
            if (!hasNext())
                throw new NoSuchElementException("No more elements.");
            T value = producer.apply(i, j);
            i++;
            if (i == j) {
                i = 0;
                j++;
            }
            return value;
        }
    }

    // todo handle empty
    public static <E> Iterable<List<E>> combinations(int length, List<E> base) {
        return () -> new CombinationsIterator<>(length, new ArrayList<>(base));
    }

    private static final class CombinationsIterator<T> implements Iterator<List<T>> {

        private boolean done = false;
        private int[] counter;
        private List<T> base;

        CombinationsIterator(int length, List<T> base) {
            this.base = base;
            this.counter = new int[length];
        }

        @Override
        public boolean hasNext() {
            return !done;
        }

        @Override
        public List<T> next() {
            List<T> set = new ArrayList<>();
            for (int index : counter)
                set.add(base.get(index));

            int index = 0;
            while (index < counter.length && counter[index] == base.size() - 1) {
                counter[index] = 0;
                index++;
            }
            if (index != counter.length) {
                counter[index]++;
            } else {
                done = true;
            }
            return set;
        }
    }

    public static <E> Iterable<List<E>> powerSet(List<E> base) {
        return () -> new PowerSetIterator<>(base);
    }

    private static final class PowerSetIterator<T> implements Iterator<List<T>> {

        private Iterator<List<Boolean>> combinations;
        private List<T> base;

        PowerSetIterator(List<T> base) {
            this.base = base;
            this.combinations = combinations(base.size(), Arrays.asList(false, true)).iterator();
        }

        @Override
        public boolean hasNext() {
            return combinations.hasNext();
        }

        @Override
        public List<T> next() {
            if (!hasNext())
                throw new NoSuchElementException("No more elements.");
            List<Boolean> l = combinations.next();
            List<T> set = new ArrayList<>();
            for (int i = 0; i < base.size(); i++)
                if (l.get(i))
                    set.add(base.get(i));
            return set;
        }
    }

    public static Iterable<List<Integer>> indexPermutations(int max) {
        List<Integer> indices = IntStream.range(0, max).boxed().collect(Collectors.toCollection(ArrayList::new));
        return permutations(indices);
    }

    // todo handle empty
    public static <T> Iterable<List<T>> permutations(List<T> base) {
        return () -> new PermutationIterator<>(new ArrayList<>(base));
    }

    private static final class PermutationIterator<T> implements Iterator<List<T>> {
        // factorial-base counter
        // todo reverse this
        private int[] counter;
        private int currentDigit = 0;
        private List<T> base;

        PermutationIterator(List<T> base) {
            this.base = base;
            counter = new int[base.size()];
            // start with identity permutation
            while (currentDigit < base.size()) {
                int index = base.size() - 1 - currentDigit;
                counter[currentDigit++] = index;
            }
        }

        @Override
        public boolean hasNext() {
            return currentDigit != 0;
        }

        @Override
        public List<T> next() {
            if (!hasNext())
                throw new NoSuchElementException("No more elements.");

            List<T> result = new ArrayList<>(base);

            System.out.println(Arrays.toString(counter));

            // undo last swaps
            int lastSwap;
            do {
                // all permutations have been generated
                if (currentDigit == 0)
                    return result;
                currentDigit--;
                lastSwap = counter[currentDigit];
                swap(lastSwap, base.size() - 1 - currentDigit);
            } while (lastSwap == 0);

            // advance swap
            int nextSwap = lastSwap - 1;
            swap(nextSwap, base.size() - 1 - currentDigit);
            counter[currentDigit++] = nextSwap;

            // default rest
            while (currentDigit < base.size()) {
                int index = base.size() - 1 - currentDigit;
                counter[currentDigit++] = index;
            }
            return result;
        }

        private void swap(int i, int j) {
            T temp = base.get(i);
            base.set(i, base.get(j));
            base.set(j, temp);
        }
    }
}
