package com.datastructures.linear.skiplist.benchmark;

import com.datastructures.linear.skiplist.classic.SkipList;
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
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * Empirically checks the module's headline claim: {@code get} cost should grow sub-linearly
 * (log-like) with {@code size}, not stay flat like a hash table's O(1) average case and not
 * scale linearly like a full scan. Same style as {@code trees.binarysearchtree}'s benchmark: a
 * single lookup against an already-populated structure of each size.
 */
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Warmup(iterations = 2, time = 1)
@Measurement(iterations = 3, time = 1)
@Fork(1)
public class SkipListBenchmark {

    @State(Scope.Thread)
    public static class Populated {
        @Param({"100", "10000", "100000"})
        public int size;

        SkipList<Integer, Integer> skipList;
        int lookupKey;

        @Setup(Level.Trial)
        public void setUp() {
            List<Integer> keys = new ArrayList<>(size);
            for (int i = 0; i < size; i++) {
                keys.add(i);
            }
            Collections.shuffle(keys, new Random(42));

            skipList = new SkipList<>();
            for (int key : keys) {
                skipList.put(key, key);
            }
            lookupKey = size / 2;
        }
    }

    @Benchmark
    public Integer get(Populated state) {
        return state.skipList.get(state.lookupKey);
    }
}
