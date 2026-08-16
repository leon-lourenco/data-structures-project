package com.datastructures.linear.linkedlist.benchmark;

import com.datastructures.linear.linkedlist.classic.LinkedList;
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
 * The mirror image of the dynamic-array benchmark: here insertion at a known point is O(1)
 * regardless of list size, and it's indexed access that degrades to O(n) as the list grows.
 */
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Warmup(iterations = 2, time = 1)
@Measurement(iterations = 3, time = 1)
@Fork(1)
public class LinkedListBenchmark {

    @State(Scope.Thread)
    public static class Populated {
        @Param({"100", "10000", "100000"})
        public int size;

        LinkedList<Integer> list;
        LinkedList.Node<Integer> middleAnchor;

        @Setup(Level.Trial)
        public void setUp() {
            list = new LinkedList<>();
            LinkedList.Node<Integer> anchor = null;
            for (int i = 0; i < size; i++) {
                LinkedList.Node<Integer> node = list.addLast(i);
                if (i == size / 2) {
                    anchor = node;
                }
            }
            middleAnchor = anchor;
        }
    }

    /** Splicing a node in right after a known anchor — O(1) no matter how large the list is. */
    @Benchmark
    public LinkedList.Node<Integer> insertAfterKnownAnchor(Populated state) {
        return state.list.insertAfter(state.middleAnchor, -1);
    }

    /** Reading the middle element by index — O(n): has to walk from the head every time. */
    @Benchmark
    public Integer getMiddleElement(Populated state) {
        return state.list.get(state.size / 2);
    }
}
