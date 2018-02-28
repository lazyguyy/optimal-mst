package util.decision;

public class DecisionTree {

    // internally, we represent a decision tree (quite similar to a binary heap) by an array of nodes
    // each describing a comparison between two edges
    private Comparison[] comparisons;

    public DecisionTree(int depth) {

    }

    int bucket(/*EdgePermutation permutation*/) {

        return 0;
    }


    // represents a comparison between edges
    private class Comparison {
        int firstEdge;
        int secondEdge;
    }
}
