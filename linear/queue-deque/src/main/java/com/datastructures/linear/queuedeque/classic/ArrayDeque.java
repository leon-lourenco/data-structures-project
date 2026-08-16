package com.datastructures.linear.queuedeque.classic;

import java.util.NoSuchElementException;

/**
 * A double-ended queue built from scratch on top of a raw {@code Object[]} used as a
 * <b>circular buffer</b> — no {@code java.util.ArrayDeque}. Two cursors, {@code head} (the
 * logical front) and {@code size}, are enough to derive every other position: the logical tail
 * always sits at {@code (head + size - 1) mod capacity}. Adding to either end just writes one
 * slot and moves a cursor with modular arithmetic — no shifting, unlike a plain array where
 * removing from the front means moving everything else left.
 *
 * <p>Growth doubles capacity, same as this repo's Dynamic Array and Stack modules, but a resize
 * here has one extra step those don't: the live elements aren't necessarily laid out
 * contiguously from index 0 (a full buffer that's had elements added to both ends can have its
 * logical front anywhere, wrapping around the end of the array). {@link #growIfFull()} walks
 * the buffer in logical order starting at {@code head} and copies it into a fresh array
 * starting at index 0, then resets {@code head} to 0 — so every index math elsewhere can keep
 * assuming a simple, un-wrapped layout until the next resize.
 */
public final class ArrayDeque<T> {

    private static final int DEFAULT_CAPACITY = 8;

    private Object[] elements;
    private int head;
    private int size;

    public ArrayDeque() {
        this.elements = new Object[DEFAULT_CAPACITY];
        this.head = 0;
        this.size = 0;
    }

    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public int capacity() {
        return elements.length;
    }

    public void addFirst(T value) {
        growIfFull();
        head = (head - 1 + elements.length) % elements.length;
        elements[head] = value;
        size++;
    }

    public void addLast(T value) {
        growIfFull();
        int tail = (head + size) % elements.length;
        elements[tail] = value;
        size++;
    }

    @SuppressWarnings("unchecked")
    public T removeFirst() {
        if (isEmpty()) {
            throw new NoSuchElementException("deque is empty");
        }
        T value = (T) elements[head];
        elements[head] = null;
        head = (head + 1) % elements.length;
        size--;
        return value;
    }

    @SuppressWarnings("unchecked")
    public T removeLast() {
        if (isEmpty()) {
            throw new NoSuchElementException("deque is empty");
        }
        int tailIndex = (head + size - 1) % elements.length;
        T value = (T) elements[tailIndex];
        elements[tailIndex] = null;
        size--;
        return value;
    }

    /** The front element without removing it, or {@code null} if the deque is empty. */
    @SuppressWarnings("unchecked")
    public T peekFirst() {
        if (isEmpty()) {
            return null;
        }
        return (T) elements[head];
    }

    /** The back element without removing it, or {@code null} if the deque is empty. */
    @SuppressWarnings("unchecked")
    public T peekLast() {
        if (isEmpty()) {
            return null;
        }
        int tailIndex = (head + size - 1) % elements.length;
        return (T) elements[tailIndex];
    }

    /**
     * Doubles capacity once the buffer is full. Elements are copied out in logical order
     * (starting from {@code head}, wrapping as needed) into a fresh array starting at index 0,
     * which is what lets every other method ignore wraparound entirely between resizes.
     */
    private void growIfFull() {
        if (size < elements.length) {
            return;
        }
        int oldCapacity = elements.length;
        Object[] resized = new Object[oldCapacity * 2];
        for (int i = 0; i < size; i++) {
            resized[i] = elements[(head + i) % oldCapacity];
        }
        elements = resized;
        head = 0;
    }
}
