package com.datastructures.trees.btree.benchmark;

import com.datastructures.trees.binarysearchtree.classic.BinarySearchTree;
import com.datastructures.trees.btree.classic.BTree;
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
 * The point of this module isn't lookup speed, it's height: a B-tree's high branching factor
 * means far fewer levels than a binary search tree for the same key count, which in a real
 * disk-backed database means far fewer page reads per lookup (see the {@code applied} package).
 * Each state's {@code @Setup} prints the real, measured height of the structure it just built
 * from the exact same shuffled key set - that's the headline number for this module's README,
 * not the nanosecond timings below (though those are real too, and show the same story: fewer
 * levels to descend generally means a cheaper {@code get}).
 */
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Warmup(iterations = 2, time = 1)
@Measurement(iterations = 3, time = 1)
@Fork(1)
public class BTreeBenchmark {

    /** Deliberately high: a real disk-backed index sizes each node to roughly one disk page. */
    private static final int BRANCHING_FACTOR = 32;

    @State(Scope.Thread)
    public static class BTreeState {
        @Param({"1000", "10000", "100000"})
        public int size;

        BTree<Integer, Integer> tree;
        int lookupKey;

        @Setup(Level.Trial)
        public void setUp() {
            tree = new BTree<>(BRANCHING_FACTOR);
            for (int key : shuffledKeys(size)) {
                tree.insert(key, key);
            }
            lookupKey = size / 2;
            System.out.println("[height] size=" + size + " b-tree(t=" + BRANCHING_FACTOR + ") height=" + tree.height());
        }
    }

    @State(Scope.Thread)
    public static class BstState {
        @Param({"1000", "10000", "100000"})
        public int size;

        BinarySearchTree<Integer, Integer> tree;
        int lookupKey;

        @Setup(Level.Trial)
        public void setUp() {
            tree = new BinarySearchTree<>();
            for (int key : shuffledKeys(size)) {
                tree.insert(key, key);
            }
            lookupKey = size / 2;
            System.out.println("[height] size=" + size + " binary-search-tree height=" + tree.height());
        }
    }

    private static List<Integer> shuffledKeys(int size) {
        List<Integer> keys = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            keys.add(i);
        }
        Collections.shuffle(keys, new Random(42));
        return keys;
    }

    @Benchmark
    public Integer getFromBTree(BTreeState state) {
        return state.tree.get(state.lookupKey);
    }

    @Benchmark
    public Integer getFromBinarySearchTree(BstState state) {
        return state.tree.get(state.lookupKey);
    }
}
