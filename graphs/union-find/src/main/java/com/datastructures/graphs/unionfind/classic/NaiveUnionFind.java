package com.datastructures.graphs.unionfind.classic;

/**
 * The textbook union-find (disjoint-set) structure with neither of its two classic
 * optimizations: no path compression, no union by rank/size. {@link #union(int, int)} always
 * attaches the first argument's root directly under the second argument's root, with no regard
 * for either tree's height — an unlucky (or adversarial) sequence of unions, such as always
 * unioning the next element onto the end of a growing chain, degenerates every tree into a
 * straight line. {@link #find(int)} then costs O(n) in that worst case, since it has to walk the
 * whole chain to reach the root every single time.
 *
 * <p>{@link UnionFind} in this same package is the optimized counterpart — this class exists
 * specifically so the benchmark in this module can measure the gap between them.
 */
public final class NaiveUnionFind {

    private final int[] parent;

    public NaiveUnionFind(int size) {
        parent = new int[size];
        for (int i = 0; i < size; i++) {
            parent[i] = i;
        }
    }

    public int size() {
        return parent.length;
    }

    /** Walks parent pointers up to the root. No path compression: every call re-walks the chain. */
    public int find(int element) {
        int root = element;
        while (parent[root] != root) {
            root = parent[root];
        }
        return root;
    }

    /** Attaches {@code a}'s root under {@code b}'s root. No union by rank/size. */
    public void union(int a, int b) {
        int rootA = find(a);
        int rootB = find(b);
        if (rootA == rootB) {
            return;
        }
        parent[rootA] = rootB;
    }

    public boolean connected(int a, int b) {
        return find(a) == find(b);
    }
}
