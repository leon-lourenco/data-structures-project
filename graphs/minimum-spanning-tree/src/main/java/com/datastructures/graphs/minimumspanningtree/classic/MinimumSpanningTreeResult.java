package com.datastructures.graphs.minimumspanningtree.classic;

import java.util.List;

/**
 * The outcome of running Kruskal's algorithm: the chosen edges, their total weight, and whether
 * every input node ended up connected. {@code spansAllNodes} is {@code false} when the input
 * graph was itself disconnected — Kruskal still returns the cheapest possible forest in that
 * case, it just can't produce a single tree out of nodes with no path between them.
 */
public record MinimumSpanningTreeResult<T>(List<WeightedEdge<T>> edges, long totalWeight, boolean spansAllNodes) {
}
