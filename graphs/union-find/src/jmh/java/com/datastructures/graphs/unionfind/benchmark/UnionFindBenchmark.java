package com.datastructures.graphs.unionfind.benchmark;

import com.datastructures.graphs.unionfind.classic.NaiveUnionFind;
import com.datastructures.graphs.unionfind.classic.UnionFind;
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

import java.util.concurrent.TimeUnit;

/**
 * The headline measurement of this module: {@code find()} cost on the naive structure versus
 * the optimized one, both put through the exact same adversarial union sequence —
 * union(0,1), union(1,2), union(2,3), ... — which is the worst case for a union-find with no
 * union-by-rank, since every union blindly attaches onto the growing end of a single chain.
 * {@link NaiveUnionFind} has no path compression, so it re-walks that full chain on every single
 * {@code find} call, forever. {@link UnionFind} has both path compression and union by rank, so
 * even under this exact same adversarial input its tree stays shallow (union by rank alone keeps
 * it flat here), and {@code find} stays cheap regardless of how large the chain gets.
 */
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Warmup(iterations = 2, time = 1)
@Measurement(iterations = 3, time = 1)
@Fork(1)
public class UnionFindBenchmark {

    @State(Scope.Thread)
    public static class NaiveWorstCaseChain {
        @Param({"100", "1000", "10000"})
        public int size;

        NaiveUnionFind uf;
        int probe;

        @Setup(Level.Trial)
        public void setUp() {
            uf = new NaiveUnionFind(size);
            for (int i = 0; i < size - 1; i++) {
                uf.union(i, i + 1);
            }
            probe = size / 2;
        }
    }

    @State(Scope.Thread)
    public static class OptimizedWorstCaseChain {
        @Param({"100", "1000", "10000"})
        public int size;

        UnionFind uf;
        int probe;

        @Setup(Level.Trial)
        public void setUp() {
            uf = new UnionFind(size);
            for (int i = 0; i < size - 1; i++) {
                uf.union(i, i + 1);
            }
            probe = size / 2;
        }
    }

    @Benchmark
    public int findOnNaiveChain(NaiveWorstCaseChain state) {
        return state.uf.find(state.probe);
    }

    @Benchmark
    public int findOnOptimizedChain(OptimizedWorstCaseChain state) {
        return state.uf.find(state.probe);
    }
}
