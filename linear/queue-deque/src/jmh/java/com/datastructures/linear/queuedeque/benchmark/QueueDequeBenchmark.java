package com.datastructures.linear.queuedeque.benchmark;

import com.datastructures.linear.dynamicarray.classic.DynamicArray;
import com.datastructures.linear.queuedeque.classic.ArrayDeque;
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
 * The headline comparison for this module: removing the front element of a structure holding
 * {@code size} elements. The circular buffer's {@link ArrayDeque#removeFirst()} only ever moves
 * one cursor — O(1) regardless of how many elements are behind it. A plain non-circular array's
 * {@code remove(0)} (this repo's {@link DynamicArray}) has to shift every remaining element left
 * by one — O(n), which shows up here as a cost that grows linearly with {@code size}.
 *
 * <p>Each state builds its structure to exactly {@code size} elements once per trial ({@code
 * @Setup(Level.Trial)}), then every benchmarked invocation removes the front element and
 * immediately re-appends the same value at the tail — a "rotate" that keeps the structure at a
 * constant size across the whole run instead of draining it after {@code size} calls. An earlier
 * version of this benchmark rebuilt a fresh structure on <em>every single invocation</em>
 * ({@code @Setup(Level.Invocation)}); at size=100,000 that flooded the JVM with garbage on every
 * call and buried the flat-vs-linear signal in GC noise — both benchmarks came back "growing
 * with size" because the noise floor itself scaled with size, not because removeFirst stopped
 * being O(1). The re-append is O(1) for the deque (size never exceeds what the buffer was built
 * to hold, so it never re-triggers a resize) and amortized O(1) for the array's tail (same
 * reasoning), so it never distorts either side's asymptotic story — it just keeps the operation
 * under test runnable at a stable {@code size} indefinitely.
 */
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Warmup(iterations = 2, time = 1)
@Measurement(iterations = 3, time = 1)
@Fork(1)
public class QueueDequeBenchmark {

    @State(Scope.Thread)
    public static class DequeState {
        @Param({"100", "10000", "100000"})
        public int size;

        ArrayDeque<Integer> deque;

        @Setup(Level.Trial)
        public void setUp() {
            deque = new ArrayDeque<>();
            for (int i = 0; i < size; i++) {
                deque.addLast(i);
            }
        }
    }

    @State(Scope.Thread)
    public static class DynamicArrayState {
        @Param({"100", "10000", "100000"})
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

    /** O(1): only the head cursor moves, no matter how many elements remain. */
    @Benchmark
    public Integer removeFirstFromCircularDeque(DequeState state) {
        Integer removed = state.deque.removeFirst();
        state.deque.addLast(removed);
        return removed;
    }

    /** O(n): every remaining element has to shift left by one. */
    @Benchmark
    public Integer removeFirstFromDynamicArray(DynamicArrayState state) {
        Integer removed = state.array.remove(0);
        state.array.add(removed);
        return removed;
    }
}
