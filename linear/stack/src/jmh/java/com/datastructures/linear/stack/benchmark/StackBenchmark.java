package com.datastructures.linear.stack.benchmark;

import com.datastructures.linear.stack.classic.Stack;
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

/** Confirms push is amortized O(1) (like the dynamic array it's built on) and peek is flat O(1). */
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Warmup(iterations = 2, time = 1)
@Measurement(iterations = 3, time = 1)
@Fork(1)
public class StackBenchmark {

    @State(Scope.Thread)
    public static class SizeParam {
        @Param({"100", "10000", "1000000"})
        public int size;
    }

    @State(Scope.Thread)
    public static class Populated {
        @Param({"100", "10000", "1000000"})
        public int size;

        Stack<Integer> stack;

        @Setup(Level.Trial)
        public void setUp() {
            stack = new Stack<>();
            for (int i = 0; i < size; i++) {
                stack.push(i);
            }
        }
    }

    @Benchmark
    public Stack<Integer> push(SizeParam sizeParam) {
        Stack<Integer> stack = new Stack<>();
        for (int i = 0; i < sizeParam.size; i++) {
            stack.push(i);
        }
        return stack;
    }

    @Benchmark
    public Integer peek(Populated populated) {
        return populated.stack.peek();
    }
}
