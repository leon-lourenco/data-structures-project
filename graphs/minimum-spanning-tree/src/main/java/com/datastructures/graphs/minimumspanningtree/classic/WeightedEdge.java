package com.datastructures.graphs.minimumspanningtree.classic;

/** An undirected candidate edge between two nodes, ordered by weight for Kruskal's sort step. */
public record WeightedEdge<T>(T from, T to, long weight) implements Comparable<WeightedEdge<T>> {

    @Override
    public int compareTo(WeightedEdge<T> other) {
        return Long.compare(this.weight, other.weight);
    }
}
