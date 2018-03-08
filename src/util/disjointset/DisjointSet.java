package util.disjointset;
/**
 * 
 * Represents a disjoint set data structure.
 */
public interface DisjointSet {
    /**
     * Returns the number of distinct sets.
     * @return the number of distinct sets
     */
    int distinct();
    /**
     * Returns the number of elements.
     * @return the number of elements
     */
    int size();
    /**
     * Returns a representative member of the set that i is part of.
     * This representative member is the same for all members of the same set.
     * @param i specifies the set of which we want to find the representative member
     * @return returns the representative member of the set that i is part of
     */
    int find(int i);
    /**
     * Unions the two sets specified by i and j. All elements in the sets
     * of i and j will be represented by the same element.
     * @param i member of the first set
     * @param j member of the second set
     */
    void union(int i, int j);
}
