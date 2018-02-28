package util.decision;

import java.util.Comparator;

public class DecisionTree {

    // internally, we represent a decision tree (quite similar to a binary heap) by an array of nodes
    // each describing a comparison between two edges
    private Comparison[] comparisons;

    private DecisionTree(int depth, Comparison[] comparisons) {

    }

    public <E> int bucket(E[] elements, Comparator<? super E> comparator) {

        int index = 0;
        while (index < comparisons.length) {
            Comparison c = comparisons[index];
            E first = elements[c.firstIndex];
            E second = elements[c.secondIndex];
            // if first < second
            if (comparator.compare(first, second) < 0) {
                // go to left child
                index = 2 * index + 1;
            } else {
                // go to right child
                index = 2 * index + 2;
            }
        }
        return index - comparisons.length;
    }


    // represents a comparison between edges
    private class Comparison {
        int firstIndex;
        int secondIndex;
    }
}
