package com.datastructures.linear.queuedeque.classic;

import org.junit.jupiter.api.Test;

import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ArrayDequeTest {

    @Test
    void startsEmpty() {
        ArrayDeque<String> deque = new ArrayDeque<>();

        assertThat(deque.isEmpty()).isTrue();
        assertThat(deque.size()).isZero();
    }

    @Test
    void addFirstOnAnEmptyDequeBecomesTheOnlyElement() {
        ArrayDeque<String> deque = new ArrayDeque<>();

        deque.addFirst("a");

        assertThat(deque.isEmpty()).isFalse();
        assertThat(deque.size()).isEqualTo(1);
        assertThat(deque.peekFirst()).isEqualTo("a");
        assertThat(deque.peekLast()).isEqualTo("a");
    }

    @Test
    void addLastOnAnEmptyDequeBecomesTheOnlyElement() {
        ArrayDeque<String> deque = new ArrayDeque<>();

        deque.addLast("a");

        assertThat(deque.size()).isEqualTo(1);
        assertThat(deque.peekFirst()).isEqualTo("a");
        assertThat(deque.peekLast()).isEqualTo("a");
    }

    @Test
    void addLastQueuesElementsInFifoOrder() {
        ArrayDeque<String> deque = new ArrayDeque<>();
        deque.addLast("a");
        deque.addLast("b");
        deque.addLast("c");

        assertThat(deque.removeFirst()).isEqualTo("a");
        assertThat(deque.removeFirst()).isEqualTo("b");
        assertThat(deque.removeFirst()).isEqualTo("c");
        assertThat(deque.isEmpty()).isTrue();
    }

    @Test
    void addFirstPrependsSoTheMostRecentlyAddedIsRemovedFirst() {
        ArrayDeque<String> deque = new ArrayDeque<>();
        deque.addFirst("a");
        deque.addFirst("b");
        deque.addFirst("c");

        assertThat(deque.removeFirst()).isEqualTo("c");
        assertThat(deque.removeFirst()).isEqualTo("b");
        assertThat(deque.removeFirst()).isEqualTo("a");
    }

    @Test
    void removeLastUnlinksFromTheBack() {
        ArrayDeque<String> deque = new ArrayDeque<>();
        deque.addLast("a");
        deque.addLast("b");
        deque.addLast("c");

        assertThat(deque.removeLast()).isEqualTo("c");
        assertThat(deque.removeLast()).isEqualTo("b");
        assertThat(deque.removeLast()).isEqualTo("a");
        assertThat(deque.isEmpty()).isTrue();
    }

    @Test
    void removeFirstOnAnEmptyDequeThrows() {
        ArrayDeque<String> deque = new ArrayDeque<>();

        assertThatThrownBy(deque::removeFirst).isInstanceOf(NoSuchElementException.class);
    }

    @Test
    void removeLastOnAnEmptyDequeThrows() {
        ArrayDeque<String> deque = new ArrayDeque<>();

        assertThatThrownBy(deque::removeLast).isInstanceOf(NoSuchElementException.class);
    }

    @Test
    void peekFirstOnAnEmptyDequeReturnsNull() {
        ArrayDeque<String> deque = new ArrayDeque<>();

        assertThat(deque.peekFirst()).isNull();
    }

    @Test
    void peekLastOnAnEmptyDequeReturnsNull() {
        ArrayDeque<String> deque = new ArrayDeque<>();

        assertThat(deque.peekLast()).isNull();
    }

    @Test
    void peekFirstAndPeekLastDoNotRemoveElements() {
        ArrayDeque<String> deque = new ArrayDeque<>();
        deque.addLast("a");
        deque.addLast("b");

        assertThat(deque.peekFirst()).isEqualTo("a");
        assertThat(deque.peekLast()).isEqualTo("b");
        assertThat(deque.size()).isEqualTo(2);
    }

    @Test
    void growsPastInitialCapacityWhileWrappedAroundAndPreservesLogicalOrder() {
        ArrayDeque<Integer> deque = new ArrayDeque<>();
        int initialCapacity = deque.capacity();

        // Fill to exactly the default capacity via a mix of addLast and addFirst, so the
        // logical front (head) sits away from index 0 by the time the buffer is full and the
        // next add has to trigger a resize while wrapped around the end of the backing array.
        deque.addLast(0);
        deque.addLast(1);
        deque.addLast(2);
        deque.addLast(3);
        deque.addFirst(-1);
        deque.addFirst(-2);
        deque.addFirst(-3);
        deque.addFirst(-4);
        assertThat(deque.size()).isEqualTo(initialCapacity);

        // One more add overflows the full, wrapped-around buffer and forces growIfFull() to
        // copy every element out in logical order into a fresh array.
        deque.addLast(4);

        assertThat(deque.capacity()).isGreaterThan(initialCapacity);
        assertThat(deque.size()).isEqualTo(9);
        assertThat(deque.removeFirst()).isEqualTo(-4);
        assertThat(deque.removeFirst()).isEqualTo(-3);
        assertThat(deque.removeFirst()).isEqualTo(-2);
        assertThat(deque.removeFirst()).isEqualTo(-1);
        assertThat(deque.removeFirst()).isEqualTo(0);
        assertThat(deque.removeFirst()).isEqualTo(1);
        assertThat(deque.removeFirst()).isEqualTo(2);
        assertThat(deque.removeFirst()).isEqualTo(3);
        assertThat(deque.removeFirst()).isEqualTo(4);
        assertThat(deque.isEmpty()).isTrue();
    }

    @Test
    void continuesToWorkCorrectlyAfterMultipleResizes() {
        ArrayDeque<Integer> deque = new ArrayDeque<>();
        for (int i = 0; i < 500; i++) {
            if (i % 2 == 0) {
                deque.addLast(i);
            } else {
                deque.addFirst(-i);
            }
        }

        assertThat(deque.size()).isEqualTo(500);
        int drained = 0;
        while (!deque.isEmpty()) {
            deque.removeFirst();
            drained++;
        }
        assertThat(drained).isEqualTo(500);
    }
}
