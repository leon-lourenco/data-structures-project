package com.datastructures.trees.heap.benchmark;

import com.datastructures.trees.heap.classic.MinHeap;
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
import org.openjdk.jmh.annotations.TearDown;
import org.openjdk.jmh.annotations.Warmup;

import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * Measures the cost of a single {@code offer}/{@code poll} against a heap holding {@code size}
 * elements. Both operations mutate the heap, so a naive "prefill once, call the same op
 * thousands of times" approach doesn't work the way it does for a non-mutating lookup (like
 * this repo's Binary Search Tree {@code get()} benchmark): a fixed-size heap would either grow
 * unboundedly (offer) or drain to empty within microseconds (poll) partway through a single
 * measurement iteration.
 *
 * <p>The fix isn't to rebuild a fresh {@code size}-element heap before every single invocation
 * either — an earlier version of this benchmark did exactly that via
 * {@code @Setup(Level.Invocation)}, and it produced heavily size-dependent, high-variance
 * numbers that didn't match either operation's real O(log n) cost: rebuilding {@code size}
 * elements from scratch before every timed call re-triggers the JIT/GC/cache-locality cost of
 * populating a large array over and over, and that cost (not the single op being measured)
 * ends up dominating and scaling with {@code size}.
 *
 * <p>Instead, the heap is built exactly once per trial and stays right around {@code size}
 * elements for the entire trial: every timed invocation's offer/poll is paired with a cheap
 * compensating poll/offer in an {@code @TearDown(Level.Invocation)} method, which — like
 * {@code @Setup(Level.Invocation)} — runs outside the measured time. This is the standard JMH
 * pattern for benchmarking a single call on a mutating, size-sensitive structure (the same shape
 * JMH's own bundled samples use for queue offer/poll pairs).
 */
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Warmup(iterations = 2, time = 1)
@Measurement(iterations = 3, time = 1)
@Fork(1)
public class HeapBenchmark {

    @State(Scope.Thread)
    public static class SteadyStateHeapForOffer {
        @Param({"100", "10000", "100000"})
        public int size;

        MinHeap<Integer> heap;
        Random random;

        @Setup(Level.Trial)
        public void setUp() {
            random = new Random(42);
            heap = new MinHeap<>(size + 1);
            for (int i = 0; i < size; i++) {
                heap.offer(random.nextInt());
            }
        }

        /** Undoes the timed offer so the next invocation starts from the same size again. */
        @TearDown(Level.Invocation)
        public void removeWhatWasJustOffered() {
            heap.poll();
        }
    }

    @State(Scope.Thread)
    public static class SteadyStateHeapForPoll {
        @Param({"100", "10000", "100000"})
        public int size;

        MinHeap<Integer> heap;
        Random random;

        @Setup(Level.Trial)
        public void setUp() {
            random = new Random(42);
            heap = new MinHeap<>(size);
            for (int i = 0; i < size; i++) {
                heap.offer(random.nextInt());
            }
        }

        /** Undoes the timed poll so the next invocation starts from the same size again. */
        @TearDown(Level.Invocation)
        public void offerBackAReplacement() {
            heap.offer(random.nextInt());
        }
    }

    /** Cost of inserting one more element into a steady {@code size}-element heap. */
    @Benchmark
    public void offerIntoASteadyStateHeap(SteadyStateHeapForOffer state) {
        state.heap.offer(state.random.nextInt());
    }

    /** Cost of removing the minimum from a steady {@code size}-element heap. */
    @Benchmark
    public Integer pollFromASteadyStateHeap(SteadyStateHeapForPoll state) {
        return state.heap.poll();
    }
}
