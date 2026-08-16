package com.datastructures.graphs.dijkstra.benchmark;

import com.datastructures.graphs.dijkstra.classic.WeightedGraph;
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

import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * Checks the module README's headline claim: Dijkstra's shortest-path computation costs
 * O((V+E) log V). Graph size is scaled up while edge density (edges per node) is held roughly
 * constant, so V and E grow together and the shape of the cost growth is what's being measured.
 */
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Warmup(iterations = 2, time = 1)
@Measurement(iterations = 3, time = 1)
@Fork(1)
public class DijkstraBenchmark {

    private static final int EDGES_PER_NODE = 4;

    @State(Scope.Thread)
    public static class RandomConnectedGraph {
        @Param({"100", "1000", "10000"})
        public int nodeCount;

        WeightedGraph<Integer> graph;

        @Setup(Level.Trial)
        public void setUp() {
            graph = new WeightedGraph<>();
            Random random = new Random(42);

            for (int i = 0; i < nodeCount; i++) {
                graph.addNode(i);
            }
            // A spanning path guarantees every node is reachable from node 0.
            for (int i = 1; i < nodeCount; i++) {
                graph.addEdge(i - 1, i, 1 + random.nextInt(100));
            }
            // Extra random edges bring density up to roughly EDGES_PER_NODE edges per node.
            int extraEdges = Math.max(0, nodeCount * EDGES_PER_NODE - (nodeCount - 1));
            for (int i = 0; i < extraEdges; i++) {
                int a = random.nextInt(nodeCount);
                int b = random.nextInt(nodeCount);
                if (a != b) {
                    graph.addEdge(a, b, 1 + random.nextInt(100));
                }
            }
        }
    }

    @Benchmark
    public Map<Integer, Long> shortestPathFrom(RandomConnectedGraph state) {
        return state.graph.shortestPathFrom(0);
    }
}
