package com.datastructures.graphs.graphbfsdfs.benchmark;

import com.datastructures.graphs.graphbfsdfs.classic.Graph;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;

import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * Checks the module's headline claim - both traversals are O(V + E) - by growing a graph's
 * vertex and edge count together at a roughly constant density (a fixed average degree per
 * vertex) and measuring full-traversal cost from a single start vertex. If the cost really is
 * O(V + E), total traversal time should grow roughly linearly with vertex count here, since edge
 * count is a constant multiple of vertex count at fixed density.
 */
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Warmup(iterations = 2, time = 1)
@Measurement(iterations = 3, time = 1)
@Fork(1)
public class GraphBenchmark {

    /** Average number of edges added per vertex - kept constant so density doesn't grow with size. */
    private static final int EDGES_PER_VERTEX = 4;

    @State(Scope.Thread)
    public static class GraphState {
        @Param({"1000", "10000", "100000"})
        public int size;

        Graph<Integer> graph;
        int startVertex;

        @Setup(Level.Trial)
        public void setUp() {
            graph = new Graph<>();
            Random random = new Random(42);
            for (int vertex = 0; vertex < size; vertex++) {
                graph.addVertex(vertex);
            }
            for (int vertex = 0; vertex < size; vertex++) {
                for (int e = 0; e < EDGES_PER_VERTEX; e++) {
                    int other = random.nextInt(size);
                    if (other != vertex) {
                        graph.addEdge(vertex, other);
                    }
                }
            }
            startVertex = 0;
            System.out.println("[reachable] size=" + size + " bfsVisited=" + graph.bfs(startVertex).size()
                    + " dfsVisited=" + graph.dfs(startVertex).size());
        }
    }

    @Benchmark
    public List<Integer> bfsTraversal(GraphState state) {
        return state.graph.bfs(state.startVertex);
    }

    @Benchmark
    public List<Integer> dfsTraversal(GraphState state) {
        return state.graph.dfs(state.startVertex);
    }
}
