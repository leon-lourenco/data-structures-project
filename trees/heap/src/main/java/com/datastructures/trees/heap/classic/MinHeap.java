package com.datastructures.trees.heap.classic;

import java.util.NoSuchElementException;

/**
 * A binary min-heap built from scratch on a raw {@code Object[]} — no
 * {@code java.util.PriorityQueue}. The array stores a complete binary tree implicitly: the
 * element at index {@code i} has children at {@code 2i+1} and {@code 2i+2} and a parent at
 * {@code (i-1)/2}, so no pointers are needed at all. The only invariant maintained is that
 * every node is {@code <=} both of its children, which is what makes {@link #peek()} O(1) (the
 * minimum is always the root) and both {@link #offer} and {@link #poll} O(log n): each only
 * ever has to fix the invariant along a single root-to-leaf path, never the whole tree.
 */
public final class MinHeap<T extends Comparable<T>> {

    private static final int DEFAULT_CAPACITY = 16;

    private Object[] elements;
    private int size;

    public MinHeap() {
        this(DEFAULT_CAPACITY);
    }

    public MinHeap(int initialCapacity) {
        if (initialCapacity < 1) {
            throw new IllegalArgumentException("initialCapacity must be >= 1");
        }
        this.elements = new Object[initialCapacity];
        this.size = 0;
    }

    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    /** Inserts a value and restores the heap invariant by sifting it up towards the root. */
    public void offer(T value) {
        if (size == elements.length) {
            grow();
        }
        elements[size] = value;
        siftUp(size);
        size++;
    }

    /** The minimum element, without removing it. O(1) since the minimum is always the root. */
    @SuppressWarnings("unchecked")
    public T peek() {
        if (isEmpty()) {
            throw new NoSuchElementException("heap is empty");
        }
        return (T) elements[0];
    }

    /**
     * Removes and returns the minimum element: moves the last element into the now-empty root
     * slot, then sifts it down until the heap invariant holds again.
     */
    @SuppressWarnings("unchecked")
    public T poll() {
        if (isEmpty()) {
            throw new NoSuchElementException("heap is empty");
        }
        T min = (T) elements[0];
        size--;
        elements[0] = elements[size];
        elements[size] = null;
        if (size > 0) {
            siftDown(0);
        }
        return min;
    }

    private void grow() {
        Object[] resized = new Object[elements.length * 2];
        System.arraycopy(elements, 0, resized, 0, size);
        elements = resized;
    }

    private void siftUp(int index) {
        while (index > 0) {
            int parent = (index - 1) / 2;
            if (compare(index, parent) >= 0) {
                break;
            }
            swap(index, parent);
            index = parent;
        }
    }

    private void siftDown(int index) {
        while (true) {
            int left = 2 * index + 1;
            int right = 2 * index + 2;
            int smallest = index;
            if (left < size && compare(left, smallest) < 0) {
                smallest = left;
            }
            if (right < size && compare(right, smallest) < 0) {
                smallest = right;
            }
            if (smallest == index) {
                break;
            }
            swap(index, smallest);
            index = smallest;
        }
    }

    @SuppressWarnings("unchecked")
    private int compare(int i, int j) {
        return ((T) elements[i]).compareTo((T) elements[j]);
    }

    private void swap(int i, int j) {
        Object temp = elements[i];
        elements[i] = elements[j];
        elements[j] = temp;
    }
}
