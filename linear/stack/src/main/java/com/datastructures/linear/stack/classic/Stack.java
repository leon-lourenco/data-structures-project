package com.datastructures.linear.stack.classic;

import java.util.EmptyStackException;

/**
 * A LIFO stack built from scratch on a raw {@code Object[]} — no {@code java.util.Stack} or
 * {@code java.util.ArrayDeque}. It reuses the same doubling-growth idea as this repo's Dynamic
 * Array module: {@link #push} is amortized O(1), and every other operation only ever touches
 * the top of the array, so nothing has to shift.
 */
public final class Stack<T> {

    private static final int DEFAULT_CAPACITY = 8;

    private Object[] elements;
    private int size;

    public Stack() {
        this.elements = new Object[DEFAULT_CAPACITY];
        this.size = 0;
    }

    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public void push(T value) {
        if (size == elements.length) {
            Object[] resized = new Object[elements.length * 2];
            System.arraycopy(elements, 0, resized, 0, size);
            elements = resized;
        }
        elements[size] = value;
        size++;
    }

    @SuppressWarnings("unchecked")
    public T pop() {
        if (isEmpty()) {
            throw new EmptyStackException();
        }
        size--;
        T value = (T) elements[size];
        elements[size] = null;
        return value;
    }

    @SuppressWarnings("unchecked")
    public T peek() {
        if (isEmpty()) {
            throw new EmptyStackException();
        }
        return (T) elements[size - 1];
    }
}
