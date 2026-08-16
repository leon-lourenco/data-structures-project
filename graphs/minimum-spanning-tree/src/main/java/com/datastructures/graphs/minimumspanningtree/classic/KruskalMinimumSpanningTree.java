package com.datastructures.graphs.minimumspanningtree.classic;

import com.datastructures.graphs.unionfind.classic.UnionFind;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Kruskal's algorithm: sort every candidate edge ascending by weight, then walk the sorted list
 * greedily, adding an edge only if its two endpoints aren't already connected (adding an edge
 * between two already-connected nodes would create a cycle, which a spanning tree can't have by
 * definition). "Already connected?" is answered by this repo's own optimized union-find
 * (graphs/union-find) — near O(1) amortized per check — so the dominant cost is the initial
 * sort: O(E log E).
 */
public final class KruskalMinimumSpanningTree<T> {

    public MinimumSpanningTreeResult<T> computeMst(Set<T> nodes, List<WeightedEdge<T>> edges) {
        Map<T, Integer> indexOf = new HashMap<>();
        for (T node : nodes) {
            indexOf.put(node, indexOf.size());
        }

        List<WeightedEdge<T>> sortedEdges = new ArrayList<>(edges);
        Collections.sort(sortedEdges);

        UnionFind components = new UnionFind(nodes.size());
        List<WeightedEdge<T>> mstEdges = new ArrayList<>();
        long totalWeight = 0;
        for (WeightedEdge<T> edge : sortedEdges) {
            int from = indexOf.get(edge.from());
            int to = indexOf.get(edge.to());
            if (components.connected(from, to)) {
                continue;
            }
            components.union(from, to);
            mstEdges.add(edge);
            totalWeight += edge.weight();
        }

        boolean spansAllNodes = mstEdges.size() == nodes.size() - 1;
        return new MinimumSpanningTreeResult<>(mstEdges, totalWeight, spansAllNodes);
    }
}
