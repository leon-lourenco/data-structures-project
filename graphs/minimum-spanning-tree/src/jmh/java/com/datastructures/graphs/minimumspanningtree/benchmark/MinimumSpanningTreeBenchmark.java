package com.datastructures.graphs.minimumspanningtree.benchmark;

import com.datastructures.graphs.minimumspanningtree.classic.KruskalMinimumSpanningTree;
import com.datastructures.graphs.minimumspanningtree.classic.MinimumSpanningTreeResult;
import com.datastructures.graphs.minimumspanningtree.classic.WeightedEdge;
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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * Checks the module README's headline claim: Kruskal's algorithm is dominated by its initial
 * sort, O(E log E) — the union-find cycle check on each edge is near O(1) amortized, so it's the
 * edge count, not the node count, that should drive the cost. The node pool size grows with the
 * edge count only so the graph doesn't get absurdly dense; what's varied on purpose is E.
 */
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Warmup(iterations = 2, time = 1)
@Measurement(iterations = 3, time = 1)
@Fork(1)
public class MinimumSpanningTreeBenchmark {

    @State(Scope.Thread)
    public static class RandomEdgeSet {
        @Param({"1000", "10000", "100000"})
        public int edgeCount;

        Set<String> nodes;
        List<WeightedEdge<String>> edges;

        @Setup(Level.Trial)
        public void setUp() {
            int nodeCount = Math.max(2, edgeCount / 10);
            nodes = new HashSet<>();
            for (int i = 0; i < nodeCount; i++) {
                nodes.add("tower-" + i);
            }

            Random random = new Random(42);
            edges = new ArrayList<>(edgeCount);
            for (int i = 0; i < edgeCount; i++) {
                String from = "tower-" + random.nextInt(nodeCount);
                String to = "tower-" + random.nextInt(nodeCount);
                edges.add(new WeightedEdge<>(from, to, 1 + random.nextInt(10_000)));
            }
        }
    }

    @Benchmark
    public MinimumSpanningTreeResult<String> computeMst(RandomEdgeSet state) {
        return new KruskalMinimumSpanningTree<String>().computeMst(state.nodes, state.edges);
    }
}
