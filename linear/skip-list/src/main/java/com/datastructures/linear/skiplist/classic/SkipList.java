package com.datastructures.linear.skiplist.classic;

import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

/**
 * A probabilistic ordered map built from scratch as a layered linked list — no {@code
 * java.util.TreeMap} or balanced-tree rebalancing underneath. Level 0 is a plain sorted linked
 * list holding every key; each level above it "skips" over roughly half the nodes of the level
 * below, so a search can drop down a level (instead of stepping one node at a time) whenever the
 * next node at the current level would overshoot the target key. A sentinel {@link #head} node
 * holds the entry point into every level via its {@code forward} array.
 *
 * <p>There's no rebalancing step anywhere in this class. Structure emerges from a coin flip
 * ({@link #randomLevel()}, {@code p = 0.5}) made once per inserted node: on average, half the
 * nodes at level {@code i} also participate at level {@code i + 1}, which is what gives search,
 * insert, and delete their expected {@code O(log n)} cost without ever touching a rotation or a
 * balance factor. Every level a node participates in is fixed at insertion time and never
 * changes — the probabilistic guarantee is a property of the whole structure across many
 * inserts, not of any single node.
 */
public final class SkipList<K extends Comparable<K>, V> {

    private static final int MAX_LEVEL = 16;
    private static final double LEVEL_UP_PROBABILITY = 0.5;

    private final Node<K, V> head = new Node<>(null, null, MAX_LEVEL);
    private int level = 1;
    private int size = 0;

    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    /** The smallest key currently stored, or {@code null} if the skip list is empty. O(1). */
    public K firstKey() {
        Node<K, V> first = head.forward[0];
        return first == null ? null : first.key;
    }

    public void put(K key, V value) {
        Objects.requireNonNull(key, "key must not be null");
        @SuppressWarnings("unchecked")
        Node<K, V>[] update = new Node[MAX_LEVEL];
        Node<K, V> current = head;
        for (int i = level - 1; i >= 0; i--) {
            while (current.forward[i] != null && current.forward[i].key.compareTo(key) < 0) {
                current = current.forward[i];
            }
            update[i] = current;
        }
        current = current.forward[0];

        if (current != null && current.key.compareTo(key) == 0) {
            current.value = value;
            return;
        }

        int newLevel = randomLevel();
        if (newLevel > level) {
            for (int i = level; i < newLevel; i++) {
                update[i] = head;
            }
            level = newLevel;
        }

        Node<K, V> newNode = new Node<>(key, value, newLevel);
        for (int i = 0; i < newLevel; i++) {
            newNode.forward[i] = update[i].forward[i];
            update[i].forward[i] = newNode;
        }
        size++;
    }

    public V get(K key) {
        Node<K, V> node = findNode(key);
        return node == null ? null : node.value;
    }

    public boolean contains(K key) {
        return findNode(key) != null;
    }

    public boolean remove(K key) {
        Objects.requireNonNull(key, "key must not be null");
        @SuppressWarnings("unchecked")
        Node<K, V>[] update = new Node[MAX_LEVEL];
        Node<K, V> current = head;
        for (int i = level - 1; i >= 0; i--) {
            while (current.forward[i] != null && current.forward[i].key.compareTo(key) < 0) {
                current = current.forward[i];
            }
            update[i] = current;
        }
        current = current.forward[0];

        if (current == null || current.key.compareTo(key) != 0) {
            return false;
        }

        for (int i = 0; i < level; i++) {
            if (update[i].forward[i] != current) {
                break;
            }
            update[i].forward[i] = current.forward[i];
        }
        while (level > 1 && head.forward[level - 1] == null) {
            level--;
        }
        size--;
        return true;
    }

    private Node<K, V> findNode(K key) {
        Objects.requireNonNull(key, "key must not be null");
        Node<K, V> current = head;
        for (int i = level - 1; i >= 0; i--) {
            while (current.forward[i] != null && current.forward[i].key.compareTo(key) < 0) {
                current = current.forward[i];
            }
        }
        current = current.forward[0];
        if (current != null && current.key.compareTo(key) == 0) {
            return current;
        }
        return null;
    }

    /**
     * Coin-flips a node's participation level: p=0.5 per extra level, capped at {@link
     * #MAX_LEVEL}. The cap is applied as a plain {@link Math#min} clamp after the flips finish
     * rather than as a loop condition, deliberately: reaching {@link #MAX_LEVEL} requires 15
     * consecutive successful coin flips (probability 2^-15), so nothing short of an
     * impractically large insert count would ever exercise a hand-rolled "stop, we hit the cap"
     * loop condition in a test — {@link Math#min} enforces the exact same limit without that
     * unreachable-in-practice branch.
     */
    private int randomLevel() {
        int newLevel = 1;
        while (ThreadLocalRandom.current().nextDouble() < LEVEL_UP_PROBABILITY) {
            newLevel++;
        }
        return Math.min(newLevel, MAX_LEVEL);
    }

    private static final class Node<K, V> {
        final K key;
        V value;
        final Node<K, V>[] forward;

        @SuppressWarnings("unchecked")
        Node(K key, V value, int level) {
            this.key = key;
            this.value = value;
            this.forward = new Node[level];
        }
    }
}
