package util.decision;

import java.util.*;
import java.util.function.BiFunction;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public final class Iterators {

    private Iterators() {}

    // returns all (i, j) so that 0 <= i < j < max
    public static <T> Iterable<T> ascendingIntPairs(final int max, final BiFunction<Integer, Integer, T> producer) {
        return () -> new PairIterator<>(max, producer);
    }

    private static final class PairIterator<T> implements Iterator<T> {
        private int i;
        private int j;
        private final int max;
        private final BiFunction<Integer, Integer, T> producer;

        PairIterator(final int max, final BiFunction<Integer, Integer, T> producer) {
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

    public static <E> Iterable<List<E>> combinations(final int length, final List<E> base) {
        return () -> new CombinationsIterator<>(length, new ArrayList<>(base));
    }

    private static final class CombinationsIterator<T> implements Iterator<List<T>> {

        private boolean done = false;
        private final int[] counter;
        private final List<T> base;

        CombinationsIterator(final int length, final List<T> base) {
            this.base = base;
            this.counter = new int[length];
            if (base.size() == 0)
                done = true;
        }

        @Override
        public boolean hasNext() {
            return !done;
        }

        @Override
        public List<T> next() {
            if (!hasNext())
                throw new NoSuchElementException("No more elements.");

            List<T> set = new ArrayList<>();
            for (int c : counter)
                set.add(base.get(c));

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

    public static <E> Iterable<List<E>> powerSet(final List<E> base) {
        return () -> new PowerSetIterator<>(base);
    }

    private static final class PowerSetIterator<T> implements Iterator<List<T>> {

        private Iterator<List<Boolean>> combinations;
        private List<T> base;

        PowerSetIterator(final List<T> base) {
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

    public static Iterable<List<Integer>> indexPermutations(final int max) {
        final List<Integer> indices = IntStream.range(0, max).boxed().collect(Collectors.toCollection(ArrayList::new));
        return permutations(indices);
    }

    public static <T> Iterable<List<T>> permutations(final List<T> base) {
        // there is one permutation of a list of size 0
        if (base.size() == 0)
            return Collections.singletonList(Collections.emptyList());
        return () -> new PermutationIterator<>(new ArrayList<>(base));
    }

    private static final class PermutationIterator<T> implements Iterator<List<T>> {
        // factorial-base counter
        private int currentDigit;
        private final int[] counter;
        private final List<T> base;

        PermutationIterator(List<T> base) {
            this.base = base;
            counter = new int[base.size()];
            // start with identity permutation
            currentDigit = counter.length - 1;
            while (currentDigit >= 0) {
                counter[currentDigit] = currentDigit;
                currentDigit--;
            }
        }

        @Override
        public boolean hasNext() {
            return currentDigit != counter.length - 1;
        }

        @Override
        public List<T> next() {
            if (!hasNext())
                throw new NoSuchElementException("No more elements.");

            List<T> result = new ArrayList<>(base);

            // undo last swaps
            int lastSwap;
            do {
                // all permutations have been generated
                if (currentDigit == counter.length - 1)
                    return result;

                currentDigit++;
                lastSwap = counter[currentDigit];
                swap(lastSwap, currentDigit);
            } while (lastSwap == 0);

            // advance swap
            int nextSwap = lastSwap - 1;
            swap(nextSwap, currentDigit);
            counter[currentDigit--] = nextSwap;

            // default rest
            while (currentDigit >= 0) {
                counter[currentDigit] = currentDigit;
                currentDigit--;
            }
            return result;
        }

        private void swap(final int i, final int j) {
            T temp = base.get(i);
            base.set(i, base.get(j));
            base.set(j, temp);
        }
    }
}
