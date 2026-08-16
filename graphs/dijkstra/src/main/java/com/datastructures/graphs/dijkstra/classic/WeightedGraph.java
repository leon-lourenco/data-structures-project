package com.datastructures.graphs.dijkstra.classic;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;

/**
 * An undirected, non-negative-weight graph stored as an adjacency list — each node maps to the
 * list of {@code (neighbor, weight)} pairs it connects to. This is the graph itself, not a
 * generic-purpose collection: it exists to carry {@link #shortestPathFrom(Object)}, an
 * implementation of Dijkstra's algorithm.
 *
 * <p>Dijkstra's algorithm greedily "settles" the closest not-yet-settled node on every step,
 * relaxing (potentially lowering) the tentative distance of each of its neighbors. Because
 * every edge weight is non-negative, once a node is settled its distance can never improve
 * later — that's the greedy argument that makes the algorithm correct.
 *
 * <p><b>java.util.PriorityQueue note:</b> the frontier below is a plain {@code
 * java.util.PriorityQueue}, a deliberate, documented exception to this repo's usual "no
 * java.util shortcut" rule. The data structure this module showcases is the graph algorithm —
 * Dijkstra's greedy relaxation strategy — not heap mechanics, which is its own separate
 * data structure with its own (future) dedicated module in this repo's roadmap.
 */
public final class WeightedGraph<T> {

    private final Map<T, List<Edge<T>>> adjacency = new HashMap<>();

    /** Registers {@code node} with no edges yet, if it isn't already known. */
    public void addNode(T node) {
        adjacency.computeIfAbsent(node, ignored -> new ArrayList<>());
    }

    /** Adds an undirected edge of the given non-negative {@code weight} between two nodes. */
    public void addEdge(T a, T b, long weight) {
        if (weight < 0) {
            throw new IllegalArgumentException("edge weight must be non-negative, got " + weight);
        }
        addNode(a);
        addNode(b);
        adjacency.get(a).add(new Edge<>(b, weight));
        adjacency.get(b).add(new Edge<>(a, weight));
    }

    public int nodeCount() {
        return adjacency.size();
    }

    /**
     * Dijkstra's algorithm: the shortest-path distance from {@code source} to every node
     * reachable from it. A node absent from the result was never reached. {@code source} itself
     * maps to {@code 0}.
     */
    public Map<T, Long> shortestPathFrom(T source) {
        Map<T, Long> bestDistance = new HashMap<>();
        Set<T> settled = new HashSet<>();
        // Deliberate java.util.PriorityQueue exception — see the class-level note above.
        PriorityQueue<Frontier<T>> frontier = new PriorityQueue<>(Comparator.comparingLong(f -> f.distance));

        bestDistance.put(source, 0L);
        frontier.add(new Frontier<>(source, 0L));

        while (!frontier.isEmpty()) {
            Frontier<T> current = frontier.poll();
            if (!settled.add(current.node)) {
                continue; // stale queue entry for a node already settled at a lower distance
            }
            for (Edge<T> edge : adjacency.getOrDefault(current.node, List.of())) {
                if (settled.contains(edge.neighbor)) {
                    continue;
                }
                long candidateDistance = current.distance + edge.weight;
                Long known = bestDistance.get(edge.neighbor);
                if (known == null || candidateDistance < known) {
                    bestDistance.put(edge.neighbor, candidateDistance);
                    frontier.add(new Frontier<>(edge.neighbor, candidateDistance));
                }
            }
        }
        return bestDistance;
    }

    private record Edge<T>(T neighbor, long weight) {
    }

    private record Frontier<T>(T node, long distance) {
    }
}
