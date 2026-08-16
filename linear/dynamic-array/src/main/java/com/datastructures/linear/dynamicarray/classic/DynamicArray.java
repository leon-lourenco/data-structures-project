package com.datastructures.linear.dynamicarray.classic;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * A resizable array built from scratch on top of a raw {@code Object[]} — no
 * {@code java.util.ArrayList} underneath. Growth doubles capacity (amortized O(1) append);
 * shrinking halves capacity once occupancy drops to a quarter of it, so a fill-then-drain
 * workload doesn't thrash between resizes at a single boundary.
 */
public final class DynamicArray<T> implements Iterable<T> {

    private static final int DEFAULT_CAPACITY = 8;

    private Object[] elements;
    private int size;

    public DynamicArray() {
        this(DEFAULT_CAPACITY);
    }

    public DynamicArray(int initialCapacity) {
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

    public int capacity() {
        return elements.length;
    }

    public void add(T element) {
        ensureCapacityFor(size + 1);
        elements[size] = element;
        size++;
    }

    @SuppressWarnings("unchecked")
    public T get(int index) {
        checkIndex(index);
        return (T) elements[index];
    }

    @SuppressWarnings("unchecked")
    public T set(int index, T element) {
        checkIndex(index);
        T previous = (T) elements[index];
        elements[index] = element;
        return previous;
    }

    @SuppressWarnings("unchecked")
    public T remove(int index) {
        checkIndex(index);
        T removed = (T) elements[index];
        int elementsToShift = size - index - 1;
        if (elementsToShift > 0) {
            System.arraycopy(elements, index + 1, elements, index, elementsToShift);
        }
        size--;
        elements[size] = null;
        shrinkIfSparse();
        return removed;
    }

    private void ensureCapacityFor(int requiredSize) {
        if (requiredSize <= elements.length) {
            return;
        }
        int newCapacity = elements.length * 2;
        Object[] resized = new Object[newCapacity];
        System.arraycopy(elements, 0, resized, 0, size);
        elements = resized;
    }

    private void shrinkIfSparse() {
        if (elements.length <= DEFAULT_CAPACITY) {
            return;
        }
        if (size > elements.length / 4) {
            return;
        }
        int newCapacity = Math.max(DEFAULT_CAPACITY, elements.length / 2);
        Object[] resized = new Object[newCapacity];
        System.arraycopy(elements, 0, resized, 0, size);
        elements = resized;
    }

    private void checkIndex(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("index " + index + " out of bounds for size " + size);
        }
    }

    @Override
    public Iterator<T> iterator() {
        return new Iterator<>() {
            private int cursor = 0;

            @Override
            public boolean hasNext() {
                return cursor < size;
            }

            @Override
            @SuppressWarnings("unchecked")
            public T next() {
                if (!hasNext()) {
                    throw new NoSuchElementException();
                }
                return (T) elements[cursor++];
            }
        };
    }
}
