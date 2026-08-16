package com.datastructures.hashing.bloomfilter.benchmark;

import com.datastructures.hashing.bloomfilter.classic.BloomFilter;
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
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * The headline claim this module makes falsifiable: {@link BloomFilter#mightContain} costs
 * O(k) — independent of how many items were added — while a naive linear scan over a growing
 * {@code ArrayList<String>} (this repo's stand-in for "check a blocklist the honest way, without
 * a Bloom filter") costs O(n). Same operation (membership check against a mid-list value), same
 * growing sizes, only the data structure differs.
 */
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Warmup(iterations = 2, time = 1)
@Measurement(iterations = 3, time = 1)
@Fork(1)
public class BloomFilterBenchmark {

    @State(Scope.Thread)
    public static class BloomFilterState {
        @Param({"100", "10000", "100000"})
        public int size;

        BloomFilter<String> filter;
        String lookupValue;

        @Setup(Level.Trial)
        public void setUp() {
            filter = new BloomFilter<>(size, 0.01);
            for (int i = 0; i < size; i++) {
                filter.add("blocked-id-" + i);
            }
            lookupValue = "blocked-id-" + (size / 2);
        }
    }

    @State(Scope.Thread)
    public static class ArrayListState {
        @Param({"100", "10000", "100000"})
        public int size;

        List<String> blockedIds;
        String lookupValue;

        @Setup(Level.Trial)
        public void setUp() {
            blockedIds = new ArrayList<>(size);
            for (int i = 0; i < size; i++) {
                blockedIds.add("blocked-id-" + i);
            }
            lookupValue = "blocked-id-" + (size / 2);
        }
    }

    /** O(k): checks exactly hashCount() bits, regardless of how many items were added. */
    @Benchmark
    public boolean mightContainOnBloomFilter(BloomFilterState state) {
        return state.filter.mightContain(state.lookupValue);
    }

    /** O(n): a naive linear scan over every blocked ID added so far. */
    @Benchmark
    public boolean containsOnNaiveArrayListScan(ArrayListState state) {
        return state.blockedIds.contains(state.lookupValue);
    }
}
