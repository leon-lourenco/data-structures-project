package com.datastructures.graphs.graphbfsdfs.classic;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

/**
 * An undirected, unweighted graph built from scratch on top of an adjacency list
 * ({@code Map<T, List<T>>}) - no {@code java.util.Graph} exists in the JDK to wrap in the first
 * place, but this is hand-rolled the same way every other {@code classic} package in this repo
 * is. {@link #addEdge} links both directions; {@link #bfs} and {@link #dfs} both return the
 * order vertices were first visited in, starting from a given vertex.
 */
public final class Graph<T> {

    private final Map<T, List<T>> adjacency = new LinkedHashMap<>();

    /** Adds {@code vertex} with no edges yet, if it isn't already present. A no-op otherwise. */
    public void addVertex(T vertex) {
        adjacency.computeIfAbsent(vertex, ignored -> new ArrayList<>());
    }

    /** Links {@code a} and {@code b} in both directions, adding either endpoint that's new. */
    public void addEdge(T a, T b) {
        addVertex(a);
        addVertex(b);
        adjacency.get(a).add(b);
        adjacency.get(b).add(a);
    }

    public int size() {
        return adjacency.size();
    }

    public boolean isEmpty() {
        return adjacency.isEmpty();
    }

    /**
     * Visits every vertex reachable from {@code start}, one "layer" of neighbors at a time, and
     * returns the order vertices were first reached in. A vertex is marked visited the moment
     * it's enqueued (not when it's dequeued) - with a plain FIFO queue, marking only at dequeue
     * time would let the same vertex be enqueued more than once via two different neighbors
     * before either enqueue is processed, and it would then wrongly appear twice in the result.
     */
    public List<T> bfs(T start) {
        requireVertex(start);
        List<T> visitOrder = new ArrayList<>();
        Set<T> visited = new HashSet<>();
        Deque<T> queue = new ArrayDeque<>();
        queue.add(start);
        visited.add(start);
        while (!queue.isEmpty()) {
            T current = queue.poll();
            visitOrder.add(current);
            for (T neighbor : adjacency.get(current)) {
                if (visited.add(neighbor)) {
                    queue.add(neighbor);
                }
            }
        }
        return visitOrder;
    }

    /**
     * Visits every vertex reachable from {@code start} by always going as deep as possible
     * before backtracking, and returns the order vertices were first visited in.
     *
     * <p>Implemented iteratively with an explicit {@link Deque} used as a stack, not with
     * recursion: a recursive DFS reads more naturally, but each recursive call consumes a native
     * JVM stack frame, so a sufficiently deep or large graph (a long chain of accounts, say)
     * risks a {@link StackOverflowError}. An explicit heap-allocated stack has no such depth
     * limit tied to the call stack.
     */
    public List<T> dfs(T start) {
        requireVertex(start);
        List<T> visitOrder = new ArrayList<>();
        Set<T> visited = new HashSet<>();
        Deque<T> stack = new ArrayDeque<>();
        stack.push(start);
        while (!stack.isEmpty()) {
            T current = stack.pop();
            if (visited.add(current)) {
                visitOrder.add(current);
                // Push neighbors in reverse so the first-added neighbor is popped (and thus
                // visited) first, matching the order a recursive DFS would visit them in.
                List<T> neighbors = adjacency.get(current);
                for (int i = neighbors.size() - 1; i >= 0; i--) {
                    stack.push(neighbors.get(i));
                }
            }
        }
        return visitOrder;
    }

    private void requireVertex(T vertex) {
        if (!adjacency.containsKey(vertex)) {
            throw new NoSuchElementException("no such vertex: " + vertex);
        }
    }
}
