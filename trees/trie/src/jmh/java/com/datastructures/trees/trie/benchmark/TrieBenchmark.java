package com.datastructures.trees.trie.benchmark;

import com.datastructures.trees.trie.classic.Trie;
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
 * The distinctive measurement of this module, and a genuinely different shape than every other
 * benchmark in this repo: key *length* is held fixed (every generated key is exactly 12
 * characters, "PIX" + a 9-digit zero-padded number) while the *number of stored keys* varies
 * across {@code size}. Every other module in this repo gets slower (hash table collisions,
 * unbalanced BST height, heap depth) or stays flat (hash table average case) as a function of
 * how much data is stored. A trie's {@code contains}/{@code startsWith} cost is a function of
 * the key/prefix length alone — walking one node per character — so unlike a hash table or a
 * linear scan, looking a key up doesn't get slower as more keys get added. This benchmark
 * exists to make that claim falsifiable: {@code startsWith}/{@code contains} cost here should
 * stay roughly flat across a 1,000x increase in stored key count.
 */
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Warmup(iterations = 2, time = 1)
@Measurement(iterations = 3, time = 1)
@Fork(1)
public class TrieBenchmark {

    @State(Scope.Thread)
    public static class PrefilledTrie {
        @Param({"100", "10000", "100000"})
        public int size;

        Trie trie;
        String lookupKey;
        String lookupPrefix;

        @Setup(Level.Trial)
        public void setUp() {
            trie = new Trie();
            for (int i = 0; i < size; i++) {
                trie.insert(fixedLengthKeyFor(i));
            }
            lookupKey = fixedLengthKeyFor(size / 2);
            lookupPrefix = lookupKey.substring(0, 8);
        }

        /** Always exactly 12 characters ("PIX" + a 9-digit zero-padded number), regardless of {@code size}. */
        private static String fixedLengthKeyFor(int i) {
            return String.format("PIX%09d", i);
        }
    }

    @Benchmark
    public boolean containsAFixedLengthKey(PrefilledTrie state) {
        return state.trie.contains(state.lookupKey);
    }

    @Benchmark
    public boolean startsWithAFixedLengthPrefix(PrefilledTrie state) {
        return state.trie.startsWith(state.lookupPrefix);
    }
}
