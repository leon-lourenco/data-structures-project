package com.datastructures.linear.dynamicarray.benchmark;

import com.datastructures.linear.dynamicarray.classic.DynamicArray;
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
 * Empirically checks the two headline claims in the module README: append is amortized O(1)
 * (average cost per element stays flat as {@code size} grows) and indexed get is O(1)
 * (constant regardless of {@code size}).
 */
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Warmup(iterations = 2, time = 1)
@Measurement(iterations = 3, time = 1)
@Fork(1)
public class DynamicArrayBenchmark {

    @State(Scope.Thread)
    public static class SizeParam {
        @Param({"100", "10000", "1000000"})
        public int size;
    }

    @State(Scope.Thread)
    public static class Populated {
        @Param({"100", "10000", "1000000"})
        public int size;

        DynamicArray<Integer> array;

        @Setup(Level.Trial)
        public void setUp() {
            array = new DynamicArray<>();
            for (int i = 0; i < size; i++) {
                array.add(i);
            }
        }
    }

    /** Cost per appended element, amortized over {@code size} appends into a fresh array. */
    @Benchmark
    public DynamicArray<Integer> append(SizeParam sizeParam) {
        DynamicArray<Integer> array = new DynamicArray<>();
        for (int i = 0; i < sizeParam.size; i++) {
            array.add(i);
        }
        return array;
    }

    /** Cost of a single indexed read against an already-populated array of {@code size}. */
    @Benchmark
    public int get(Populated populated) {
        return populated.array.get(populated.size / 2);
    }
}
