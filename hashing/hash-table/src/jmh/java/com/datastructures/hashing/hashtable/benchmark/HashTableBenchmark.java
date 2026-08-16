package com.datastructures.hashing.hashtable.benchmark;

import com.datastructures.hashing.hashtable.classic.HashTable;
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
 * Contrasts a uniformly-hashed key set (average-case O(1) lookup, the normal case) against a
 * key set engineered to collide into a single bucket (worst-case O(n) lookup, a degenerate
 * {@code hashCode()} or an attacker-crafted key set) — the same headline claim the module
 * README makes, made falsifiable.
 */
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Warmup(iterations = 2, time = 1)
@Measurement(iterations = 3, time = 1)
@Fork(1)
public class HashTableBenchmark {

    @State(Scope.Thread)
    public static class UniformKeys {
        @Param({"100", "10000", "100000"})
        public int size;

        HashTable<Integer, Integer> table;

        @Setup(Level.Trial)
        public void setUp() {
            table = new HashTable<>();
            for (int i = 0; i < size; i++) {
                table.put(i, i);
            }
        }
    }

    @State(Scope.Thread)
    public static class CollidingKeys {
        @Param({"100", "10000", "100000"})
        public int size;

        HashTable<CollidingKey, Integer> table;

        @Setup(Level.Trial)
        public void setUp() {
            table = new HashTable<>();
            for (int i = 0; i < size; i++) {
                table.put(new CollidingKey(i), i);
            }
        }
    }

    @Benchmark
    public Integer getWithUniformHashing(UniformKeys state) {
        return state.table.get(state.size / 2);
    }

    @Benchmark
    public Integer getWithEveryKeyCollidingInOneBucket(CollidingKeys state) {
        return state.table.get(new CollidingKey(state.size / 2));
    }

    /** A key whose hashCode is constant, forcing every instance into the same bucket. */
    public static final class CollidingKey {
        private final int id;

        public CollidingKey(int id) {
            this.id = id;
        }

        @Override
        public int hashCode() {
            return 1;
        }

        @Override
        public boolean equals(Object other) {
            return other instanceof CollidingKey that && this.id == that.id;
        }
    }
}
