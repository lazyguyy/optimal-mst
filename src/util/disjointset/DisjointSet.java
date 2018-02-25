package util.disjointset;

public interface DisjointSet {
    int distinct();
    int size();
    int find(int i);
    void union(int i, int j);
}
