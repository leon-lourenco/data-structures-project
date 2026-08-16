package com.datastructures.linear.linkedlist.classic;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * A doubly linked list built from scratch — no {@code java.util.LinkedList}. Every node holds
 * both a {@code prev} and a {@code next} pointer, which is what makes {@link #insertAfter} and
 * {@link #remove(Node)} O(1): unlike a resizable array, splicing a node in or out never
 * shifts anything else, because nothing else needs to move.
 *
 * <p>The trade a linked list makes for that is indexed access: {@link #get(int)} has no way to
 * jump straight to position {@code i} the way an array does, so it has to walk from the head
 * one link at a time — O(n). The benchmark in this module measures exactly that: O(1)
 * insertion regardless of list size, against O(n) indexed access that gets slower as the list
 * grows.
 */
public final class LinkedList<T> implements Iterable<T> {

    /** A node in the list. Fields are exposed so callers can hold a reference for O(1) splicing. */
    public static final class Node<T> {
        private T value;
        private Node<T> prev;
        private Node<T> next;

        private Node(T value) {
            this.value = value;
        }

        public T value() {
            return value;
        }

        public Node<T> next() {
            return next;
        }

        public Node<T> prev() {
            return prev;
        }
    }

    private Node<T> head;
    private Node<T> tail;
    private int size;

    public Node<T> headNode() {
        return head;
    }

    public Node<T> tailNode() {
        return tail;
    }

    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public Node<T> addFirst(T value) {
        Node<T> node = new Node<>(value);
        if (head == null) {
            head = tail = node;
        } else {
            node.next = head;
            head.prev = node;
            head = node;
        }
        size++;
        return node;
    }

    public Node<T> addLast(T value) {
        Node<T> node = new Node<>(value);
        if (tail == null) {
            head = tail = node;
        } else {
            node.prev = tail;
            tail.next = node;
            tail = node;
        }
        size++;
        return node;
    }

    /** Splices a new node immediately after {@code anchor} — O(1), no shifting. */
    public Node<T> insertAfter(Node<T> anchor, T value) {
        if (anchor == tail) {
            return addLast(value);
        }
        Node<T> node = new Node<>(value);
        Node<T> anchorNext = anchor.next;
        node.prev = anchor;
        node.next = anchorNext;
        anchor.next = node;
        anchorNext.prev = node;
        size++;
        return node;
    }

    public T removeFirst() {
        if (head == null) {
            throw new NoSuchElementException("list is empty");
        }
        return remove(head);
    }

    public T removeLast() {
        if (tail == null) {
            throw new NoSuchElementException("list is empty");
        }
        return remove(tail);
    }

    /** Unlinks {@code node} — O(1), no shifting. */
    public T remove(Node<T> node) {
        if (node.prev != null) {
            node.prev.next = node.next;
        } else {
            head = node.next;
        }
        if (node.next != null) {
            node.next.prev = node.prev;
        } else {
            tail = node.prev;
        }
        size--;
        return node.value;
    }

    /** O(n): walks from the head, since a linked list has no way to jump to an index directly. */
    public T get(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("index " + index + " out of bounds for size " + size);
        }
        Node<T> current = head;
        for (int i = 0; i < index; i++) {
            current = current.next;
        }
        return current.value;
    }

    @Override
    public Iterator<T> iterator() {
        return new Iterator<>() {
            private Node<T> cursor = head;

            @Override
            public boolean hasNext() {
                return cursor != null;
            }

            @Override
            public T next() {
                if (!hasNext()) {
                    throw new NoSuchElementException();
                }
                T value = cursor.value;
                cursor = cursor.next;
                return value;
            }
        };
    }
}
