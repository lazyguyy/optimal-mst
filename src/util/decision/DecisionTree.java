package util.decision;

import java.util.ArrayList;
import java.util.List;

class DecisionTree {

    // internally, we represent a decision tree (quite similar to a binary heap) by an array of nodes
    // each describing a comparison between two edges
    private final Comparison[] comparisons;

    private DecisionTree(Comparison[] comparisons) {
        this.comparisons = comparisons;
    }

    private int indexToBucket(int index) {
        return index - comparisons.length;
    }

    public <E extends Comparable<? super E>> int classify(List<E> elements) {

        int index = 0;
        while (index < comparisons.length) {
            Comparison c = comparisons[index];
            E first = elements.get(c.firstIndex);
            E second = elements.get(c.secondIndex);
            // if first < second
            if (first.compareTo(second) < 0) {
                // go to left child
                index = 2 * index + 1;
            } else {
                // go to right child
                index = 2 * index + 2;
            }
        }
        return indexToBucket(index);
    }

    // represents a comparison between edges
    private static class Comparison {
        final int firstIndex;
        final int secondIndex;

        private Comparison(int firstIndex, int secondIndex) {
            this.firstIndex = firstIndex;
            this.secondIndex = secondIndex;
        }
    }

    private void appendLevel(final int start, final StringBuilder sb, final int level) {

        for (int j = 0; j < level - 1; j++)
            sb.append("  ");
        if (level > 0)
            sb.append("\u2514 ");

        if (start >= comparisons.length) {
            sb.append("Bucket(").append(indexToBucket(start)).append(")\n");
            return;
        }

        sb.append("Comparison(").append(comparisons[start].firstIndex)
                .append(" < ").append(comparisons[start].secondIndex).append(")\n");

        for (int i = 0; i < 2; i++) {
            int index = 2 * start + i + 1;
            appendLevel(index, sb, level + 1);
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        appendLevel(0, sb, 0);
        return sb.toString();
    }

    public static Iterable<DecisionTree> enumerateTrees(int depth, int edges) {
        int length = (1 << (depth + 1)) - 1;

        List<Comparison> comparisons = new ArrayList<>();
        Iterators.ascendingIntPairs(edges, Comparison::new).forEach(comparisons::add);

        // TODO fix
        return new ArrayList<>();
        //return () -> Iterators.combinations(length, comparisons)
        //    .map(l -> new DecisionTree(l.toArray(new Comparison[length])))
        //    .iterator();
    }
}
