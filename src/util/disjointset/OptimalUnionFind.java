package util.disjointset;

/**
 * 
 * Provides an implementation of the DisjointSet interface.
 * All operations run in near-constant-time (bounded by the inverse Ackermann function)
 */
public class OptimalUnionFind implements DisjointSet {

    // positive value: parent index
    // negative value: subtree size
    private final int[] info;
    private int distinct;

    /**
     * Creates a new OptimalUnionFind data structure of the specified size
     * @param size the size of the OptimalUnionFind data structure
     */
    public OptimalUnionFind(final int size) {
        this.distinct = size;
        info = new int[size];
        for (int i = 0; i < size; i++)
            info[i] = -1;
    }

    @Override
    public int count() {
        return distinct;
    }

    @Override
    public int size() {
        return info.length;
    }

    @Override
    public int find(int i) {
        int root = i;
        while (info[root] >= 0)
            root = info[root];

        // path compression
        int next = info[i];
        while (next >= 0) {
            info[i] = root;
            i = next;
            next = info[i];
        }
        return root;
    }

    @Override
    public void union(final int i, final int j) {
        final int iroot = find(i), jroot = find(j);
        if (iroot == jroot) return;
        if (info[iroot] < info[jroot]) {
            info[jroot] = info[iroot] + info[jroot];
            info[jroot] = iroot;
        } else {
            info[jroot] = info[iroot] + info[jroot];
            info[iroot] = jroot;
        }
        this.distinct--;
    }
}
