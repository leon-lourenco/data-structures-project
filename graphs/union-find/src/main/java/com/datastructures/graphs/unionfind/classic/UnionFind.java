package com.datastructures.graphs.unionfind.classic;

/**
 * Union-find (disjoint-set) with both of its classic optimizations applied:
 *
 * <ul>
 *   <li><b>Path compression</b> — every {@link #find(int)} call flattens the path it just
 *       walked, repointing every visited node directly at the root it found, so the next lookup
 *       for any of those nodes is a single hop.
 *   <li><b>Union by rank</b> — {@link #union(int, int)} always attaches the shorter tree under
 *       the taller one's root (using an upper-bound-on-height "rank" rather than exact height,
 *       which stays cheap to maintain), instead of attaching arbitrarily.
 * </ul>
 *
 * <p>Combined, these two optimizations bound the amortized cost per operation by the inverse
 * Ackermann function — for any input size that could ever exist in practice, that's effectively
 * a small constant. {@link NaiveUnionFind} in this same package has neither optimization, and
 * this module's benchmark measures exactly how large that gap gets under a worst-case union
 * sequence.
 */
public final class UnionFind {

    private final int[] parent;
    private final int[] rank;

    public UnionFind(int size) {
        parent = new int[size];
        rank = new int[size];
        for (int i = 0; i < size; i++) {
            parent[i] = i;
        }
    }

    public int size() {
        return parent.length;
    }

    /** Finds the root of {@code element}, compressing every visited node onto that root. */
    public int find(int element) {
        int root = element;
        while (parent[root] != root) {
            root = parent[root];
        }
        while (parent[element] != root) {
            int next = parent[element];
            parent[element] = root;
            element = next;
        }
        return root;
    }

    /** Unions the sets containing {@code a} and {@code b}, attaching the shorter tree lower. */
    public void union(int a, int b) {
        int rootA = find(a);
        int rootB = find(b);
        if (rootA == rootB) {
            return;
        }
        if (rank[rootA] < rank[rootB]) {
            parent[rootA] = rootB;
        } else if (rank[rootA] > rank[rootB]) {
            parent[rootB] = rootA;
        } else {
            parent[rootB] = rootA;
            rank[rootA]++;
        }
    }

    public boolean connected(int a, int b) {
        return find(a) == find(b);
    }
}
