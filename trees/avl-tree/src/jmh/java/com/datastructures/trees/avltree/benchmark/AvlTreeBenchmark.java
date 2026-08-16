package com.datastructures.trees.avltree.benchmark;

import com.datastructures.trees.avltree.classic.AvlTree;
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
 * The headline measurement of this module: {@code get()} cost against this repo's plain
 * {@link BinarySearchTree} versus this module's {@link AvlTree}, both built from the exact same
 * sorted key sequence that {@code BinarySearchTreeTest} proves degenerates the plain BST's
 * height to {@code n} (a straight chain). The AVL tree rebalances on every insert, so its
 * {@code get()} cost should stay flat/log-like regardless of insertion order — random or
 * sorted, it makes no difference to an AVL tree, which is precisely the guarantee a plain BST
 * cannot offer.
 */
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Warmup(iterations = 2, time = 1)
@Measurement(iterations = 3, time = 1)
@Fork(1)
public class AvlTreeBenchmark {

    @State(Scope.Thread)
    public static class BstSortedOrderTree {
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

    @State(Scope.Thread)
    public static class BstRandomOrderTree {
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
    public static class AvlSortedOrderTree {
        @Param({"100", "1000", "10000"})
        public int size;

        AvlTree<Integer, Integer> tree;
        int lookupKey;

        @Setup(Level.Trial)
        public void setUp() {
            tree = new AvlTree<>();
            for (int key = 0; key < size; key++) {
                tree.insert(key, key);
            }
            lookupKey = size / 2;
        }
    }

    @State(Scope.Thread)
    public static class AvlRandomOrderTree {
        @Param({"100", "1000", "10000"})
        public int size;

        AvlTree<Integer, Integer> tree;
        int lookupKey;

        @Setup(Level.Trial)
        public void setUp() {
            List<Integer> keys = new ArrayList<>(size);
            for (int i = 0; i < size; i++) {
                keys.add(i);
            }
            Collections.shuffle(keys, new Random(42));

            tree = new AvlTree<>();
            for (int key : keys) {
                tree.insert(key, key);
            }
            lookupKey = size / 2;
        }
    }

    @Benchmark
    public Integer getFromSortedInsertedBst(BstSortedOrderTree state) {
        return state.tree.get(state.lookupKey);
    }

    @Benchmark
    public Integer getFromRandomlyBuiltBst(BstRandomOrderTree state) {
        return state.tree.get(state.lookupKey);
    }

    @Benchmark
    public Integer getFromSortedInsertedAvlTree(AvlSortedOrderTree state) {
        return state.tree.get(state.lookupKey);
    }

    @Benchmark
    public Integer getFromRandomlyBuiltAvlTree(AvlRandomOrderTree state) {
        return state.tree.get(state.lookupKey);
    }
}
