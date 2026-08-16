package com.datastructures.trees.btree.classic;

import java.util.ArrayList;
import java.util.List;

/**
 * A B-Tree built from scratch: unlike a binary search tree, every node holds up to {@code 2t -
 * 1} keys (at least {@code t - 1} once it isn't the root) and has up to {@code 2t} children,
 * where {@code t} is the tree's minimum degree. That high branching factor is the entire point
 * — it keeps the tree shallow even for huge key counts, which is why real databases use this
 * shape (see the {@code applied} package in this module) instead of a binary tree: fewer levels
 * means fewer disk-page reads when each node is sized to match one page.
 *
 * <p>Insertion uses the "preemptive split on the way down" strategy: while descending toward
 * the leaf where a key belongs, any full node encountered along the path — including the root —
 * is split *before* the recursion steps into it. That guarantees the parent a full child is
 * about to be split into always has room for the median key the split promotes, so a split
 * never has to "bubble back up" after the fact the way it would with a bottom-up split strategy.
 * All leaves stay at the same depth at all times, which is exactly what makes {@link #height()}
 * meaningful as a single number rather than "the height of the deepest branch".
 */
public final class BTree<K extends Comparable<K>, V> {

    private static final int DEFAULT_MIN_DEGREE = 3;

    private final int minDegree;
    private Node<K, V> root;
    private int size;

    public BTree() {
        this(DEFAULT_MIN_DEGREE);
    }

    /**
     * @param minDegree {@code t}: every non-root node holds between {@code t - 1} and
     *                  {@code 2t - 1} keys, and has up to {@code 2t} children. Higher {@code t}
     *                  means a wider, shallower tree.
     */
    public BTree(int minDegree) {
        if (minDegree < 2) {
            throw new IllegalArgumentException("minDegree must be >= 2, was " + minDegree);
        }
        this.minDegree = minDegree;
        this.root = new Node<>(true);
    }

    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    /**
     * Length of the path from the root to any leaf; every leaf sits at the same depth by
     * construction, so "any leaf" and "the deepest leaf" are the same number. 0 for an empty
     * tree, 1 when the root is itself a leaf.
     */
    public int height() {
        if (isEmpty()) {
            return 0;
        }
        int height = 1;
        Node<K, V> node = root;
        while (!node.leaf) {
            height++;
            node = node.children.get(0);
        }
        return height;
    }

    public void insert(K key, V value) {
        if (root.isFull(minDegree)) {
            Node<K, V> newRoot = new Node<>(false);
            newRoot.children.add(root);
            splitChild(newRoot, 0);
            root = newRoot;
        }
        insertNonFull(root, key, value);
    }

    /**
     * Inserts into {@code node}, which the caller guarantees is not full. If the key already
     * exists somewhere on the path from {@code node} down, its value is overwritten in place
     * instead of inserting a duplicate.
     */
    private void insertNonFull(Node<K, V> node, K key, V value) {
        int i = lowerBound(node.keys, key);
        if (i < node.keys.size() && node.keys.get(i).compareTo(key) == 0) {
            node.values.set(i, value);
            return;
        }
        if (node.leaf) {
            node.keys.add(i, key);
            node.values.add(i, value);
            size++;
            return;
        }
        if (node.children.get(i).isFull(minDegree)) {
            splitChild(node, i);
            // The split just promoted a median key/value into node at index i, shifting what
            // used to be a single child into two. Re-check against that promoted key: it might
            // be the key being inserted (overwrite in place), or it might tell us the target
            // moved from child i to the newly created child i + 1.
            int comparison = key.compareTo(node.keys.get(i));
            if (comparison == 0) {
                node.values.set(i, value);
                return;
            }
            if (comparison > 0) {
                i++;
            }
        }
        insertNonFull(node.children.get(i), key, value);
    }

    /**
     * Splits {@code parent.children[childIndex]}, a full node with {@code 2t - 1} keys, into two
     * nodes of {@code t - 1} keys each, promoting the median key/value up into {@code parent} at
     * {@code childIndex} (shifting parent's existing keys/children right to make room).
     */
    private void splitChild(Node<K, V> parent, int childIndex) {
        Node<K, V> fullChild = parent.children.get(childIndex);
        int t = minDegree;

        Node<K, V> rightHalf = new Node<>(fullChild.leaf);
        rightHalf.keys.addAll(fullChild.keys.subList(t, fullChild.keys.size()));
        rightHalf.values.addAll(fullChild.values.subList(t, fullChild.values.size()));
        if (!fullChild.leaf) {
            rightHalf.children.addAll(fullChild.children.subList(t, fullChild.children.size()));
            fullChild.children.subList(t, fullChild.children.size()).clear();
        }

        K medianKey = fullChild.keys.get(t - 1);
        V medianValue = fullChild.values.get(t - 1);
        fullChild.keys.subList(t - 1, fullChild.keys.size()).clear();
        fullChild.values.subList(t - 1, fullChild.values.size()).clear();

        parent.keys.add(childIndex, medianKey);
        parent.values.add(childIndex, medianValue);
        parent.children.add(childIndex + 1, rightHalf);
    }

    public V get(K key) {
        return get(root, key);
    }

    private V get(Node<K, V> node, K key) {
        int i = lowerBound(node.keys, key);
        if (i < node.keys.size() && node.keys.get(i).compareTo(key) == 0) {
            return node.values.get(i);
        }
        if (node.leaf) {
            return null;
        }
        return get(node.children.get(i), key);
    }

    public boolean contains(K key) {
        return get(key) != null;
    }

    /** First index in {@code keys} (sorted ascending) whose value is {@code >= key}. */
    private int lowerBound(List<K> keys, K key) {
        int i = 0;
        while (i < keys.size() && keys.get(i).compareTo(key) < 0) {
            i++;
        }
        return i;
    }

    private static final class Node<K, V> {
        final boolean leaf;
        final List<K> keys = new ArrayList<>();
        final List<V> values = new ArrayList<>();
        final List<Node<K, V>> children = new ArrayList<>();

        Node(boolean leaf) {
            this.leaf = leaf;
        }

        boolean isFull(int minDegree) {
            return keys.size() == 2 * minDegree - 1;
        }
    }
}
