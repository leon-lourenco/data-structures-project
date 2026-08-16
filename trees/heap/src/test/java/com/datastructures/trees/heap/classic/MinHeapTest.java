package com.datastructures.trees.heap.classic;

import org.junit.jupiter.api.Test;

import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class MinHeapTest {

    @Test
    void startsEmpty() {
        MinHeap<Integer> heap = new MinHeap<>();

        assertThat(heap.isEmpty()).isTrue();
        assertThat(heap.size()).isZero();
    }

    @Test
    void canBeConstructedWithAnExplicitInitialCapacity() {
        MinHeap<Integer> heap = new MinHeap<>(64);

        assertThat(heap.isEmpty()).isTrue();
        heap.offer(1);
        assertThat(heap.peek()).isEqualTo(1);
    }

    @Test
    void constructingWithANonPositiveInitialCapacityThrows() {
        assertThatThrownBy(() -> new MinHeap<Integer>(0)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void offerThenPeekReturnsTheMinimumWithoutRemovingIt() {
        MinHeap<Integer> heap = new MinHeap<>();
        heap.offer(5);
        heap.offer(3);
        heap.offer(8);

        assertThat(heap.peek()).isEqualTo(3);
        assertThat(heap.peek()).isEqualTo(3); // still there: peek doesn't remove
        assertThat(heap.size()).isEqualTo(3);
        assertThat(heap.isEmpty()).isFalse();
    }

    @Test
    void peekOnAnEmptyHeapThrows() {
        MinHeap<Integer> heap = new MinHeap<>();

        assertThatThrownBy(heap::peek).isInstanceOf(NoSuchElementException.class);
    }

    @Test
    void pollOnAnEmptyHeapThrows() {
        MinHeap<Integer> heap = new MinHeap<>();

        assertThatThrownBy(heap::poll).isInstanceOf(NoSuchElementException.class);
    }

    /**
     * One offer sequence, then a full drain via repeated poll(), engineered so every poll()
     * call exercises a different combination of the sift-down branches — not "insert random
     * stuff and hope." Offering 1, 10, 2, 20, 3 (in that order) builds the array
     * {@code [1, 3, 2, 20, 10]} (worked out by hand: offering 3 last is the only insert whose
     * sift-up actually swaps, bubbling past the 10 at index 1). Draining it one poll() at a
     * time then walks through every branch combination in {@code siftDown}:
     *
     * <ul>
     *   <li>poll #1 (array becomes {@code [10,3,2,20]} before sifting): both children exist,
     *       the right child (2) ends up smaller than the left (3), so {@code smallest} moves to
     *       the left first and then gets overridden by the right — and the swapped-into node
     *       has no children of its own, so the loop's second iteration hits
     *       {@code left < size == false}.</li>
     *   <li>poll #2 (array becomes {@code [20,3,10]} before sifting): both children exist, but
     *       the right child (10) is *not* smaller than the left (3) — {@code smallest} stays on
     *       the left, exercising {@code compare(right, smallest) < 0 == false} with a real
     *       right child present.</li>
     *   <li>poll #3 (array becomes {@code [10,20]} before sifting): only a left child exists
     *       (size 2), and it (20) is *not* smaller than the root (10) — exercises
     *       {@code compare(left, smallest) < 0 == false} together with
     *       {@code right < size == false}.</li>
     *   <li>poll #4 (array becomes {@code [20]} before sifting): no children at all —
     *       {@code left < size == false} with only one element, immediate break.</li>
     *   <li>poll #5: the heap becomes empty after removing the root, exercising {@code poll()}'s
     *       {@code size > 0 == false} branch (siftDown is skipped entirely).</li>
     * </ul>
     */
    @Test
    void pollDrainsEveryElementInAscendingOrderExercisingEverySiftDownBranch() {
        MinHeap<Integer> heap = new MinHeap<>();
        heap.offer(1);
        heap.offer(10);
        heap.offer(2);
        heap.offer(20);
        heap.offer(3);

        assertThat(heap.poll()).isEqualTo(1);
        assertThat(heap.poll()).isEqualTo(2);
        assertThat(heap.poll()).isEqualTo(3);
        assertThat(heap.poll()).isEqualTo(10);
        assertThat(heap.poll()).isEqualTo(20);
        assertThat(heap.isEmpty()).isTrue();
        assertThat(heap.size()).isZero();
    }

    @Test
    void offeringAValueSmallerThanTheCurrentMinimumSiftsItAllTheWayToTheRoot() {
        MinHeap<Integer> heap = new MinHeap<>();
        heap.offer(10);
        heap.offer(20); // larger than its parent: siftUp breaks on the first check, no swap

        heap.offer(5); // smaller than the root: siftUp swaps all the way up

        assertThat(heap.peek()).isEqualTo(5);
    }

    @Test
    void offeringStrictlyIncreasingValuesNeverSwapsDuringSiftUp() {
        MinHeap<Integer> heap = new MinHeap<>();
        for (int i = 1; i <= 5; i++) {
            heap.offer(i); // each new value is >= every existing ancestor: siftUp always breaks immediately
        }

        assertThat(heap.peek()).isEqualTo(1);
        assertThat(heap.size()).isEqualTo(5);
    }

    @Test
    void growingPastTheInitialCapacityKeepsEveryElementCorrect() {
        MinHeap<Integer> heap = new MinHeap<>();
        int[] values = {15, 3, 27, 1, 19, 8, 22, 4, 30, 11, 2, 25, 6, 17, 9, 21, 5, 29, 13, 7};
        for (int value : values) {
            heap.offer(value); // 20 offers > DEFAULT_CAPACITY (16): forces at least one grow()
        }

        assertThat(heap.size()).isEqualTo(values.length);
        int previous = Integer.MIN_VALUE;
        for (int i = 0; i < values.length; i++) {
            int polled = heap.poll();
            assertThat(polled).isGreaterThanOrEqualTo(previous);
            previous = polled;
        }
        assertThat(heap.isEmpty()).isTrue();
    }
}
