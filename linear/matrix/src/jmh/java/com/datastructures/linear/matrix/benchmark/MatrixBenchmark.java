package com.datastructures.linear.matrix.benchmark;

import com.datastructures.linear.matrix.classic.Matrix;
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
 * Empirically checks the module README's cache-locality claim: row-major traversal (the same
 * order the backing flat array is laid out in) should stay meaningfully faster than
 * column-major traversal (the identical number of element accesses, but every step jumps
 * {@code cols} slots instead of 1) as the matrix grows — a gap that widens with size, not a
 * fixed constant offset, is what would indicate a real cache effect rather than measurement
 * noise.
 */
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@Warmup(iterations = 2, time = 1)
@Measurement(iterations = 3, time = 1)
@Fork(1)
public class MatrixBenchmark {

    @State(Scope.Thread)
    public static class SquareMatrix {
        @Param({"100", "500", "1000"})
        public int dimension;

        Matrix<Integer> matrix;

        @Setup(Level.Trial)
        public void setUp() {
            matrix = new Matrix<>(dimension, dimension);
            for (int row = 0; row < dimension; row++) {
                for (int col = 0; col < dimension; col++) {
                    matrix.set(row, col, row * dimension + col);
                }
            }
        }
    }

    /** Visits every cell in the same order the backing array is laid out in (row-major). */
    @Benchmark
    public long sumRowMajor(SquareMatrix state) {
        long sum = 0;
        for (int row = 0; row < state.dimension; row++) {
            for (int col = 0; col < state.dimension; col++) {
                sum += state.matrix.get(row, col);
            }
        }
        return sum;
    }

    /** Visits every cell column by column — same element count, jumps {@code cols} slots per step. */
    @Benchmark
    public long sumColumnMajor(SquareMatrix state) {
        long sum = 0;
        for (int col = 0; col < state.dimension; col++) {
            for (int row = 0; row < state.dimension; row++) {
                sum += state.matrix.get(row, col);
            }
        }
        return sum;
    }
}
