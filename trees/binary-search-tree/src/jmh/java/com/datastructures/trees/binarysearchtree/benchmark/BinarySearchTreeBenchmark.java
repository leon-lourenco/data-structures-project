package com.datastructures.trees.binarysearchtree.benchmark;

import com.datastructures.trees.binarysearchtree.classic.BinarySearchTree;
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
 * The single most important measurement in this module: lookup cost against a tree built from
 * random-order insertion (height stays close to log2(n)) versus the exact same key set inserted
 * in sorted order (the tree degenerates into a linked list, height == n). Same data, same
 * operation — the only variable is insertion order, and that alone should show up as a very
 * different get() cost at the larger sizes.
 */
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Warmup(iterations = 2, time = 1)
@Measurement(iterations = 3, time = 1)
@Fork(1)
public class BinarySearchTreeBenchmark {

    @State(Scope.Thread)
    public static class RandomOrderTree {
        @Param({"100", "1000", "10000"})
        public int size;

        BinarySearchTree<Integer, Integer> tree;
        int lookupKey;

        @Setup(Level.Trial)
        public void setUp() {
            List<Integer> keys = new ArrayList<>(size);
            for (int i = 0; i < size; i++) {
                keys.add(i);
            }
            Collections.shuffle(keys, new Random(42));

            tree = new BinarySearchTree<>();
            for (int key : keys) {
                tree.insert(key, key);
            }
            lookupKey = size / 2;
        }
    }

    @State(Scope.Thread)
    public static class SortedOrderTree {
        @Param({"100", "1000", "10000"})
        public int size;

        BinarySearchTree<Integer, Integer> tree;
        int lookupKey;

        @Setup(Level.Trial)
        public void setUp() {
            tree = new BinarySearchTree<>();
            for (int key = 0; key < size; key++) {
                tree.insert(key, key);
            }
            lookupKey = size / 2;
        }
    }

    @Benchmark
    public Integer getFromRandomlyBuiltTree(RandomOrderTree state) {
        return state.tree.get(state.lookupKey);
    }

    @Benchmark
    public Integer getFromSortedInsertedTree(SortedOrderTree state) {
        return state.tree.get(state.lookupKey);
    }
}
