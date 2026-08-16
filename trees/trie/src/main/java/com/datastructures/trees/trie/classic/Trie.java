package com.datastructures.trees.trie.classic;

import java.util.HashMap;
import java.util.Map;

/**
 * A prefix tree over {@code String} keys, built from scratch: each node holds its children in a
 * {@code Map<Character, Node>} (not a fixed-size 26/128-entry array, since keys here aren't
 * restricted to one alphabet), and one boolean per node marking whether a complete key ends
 * there. Every operation — {@link #insert}, {@link #contains}, {@link #startsWith} — walks one
 * character at a time from the root, so its cost is {@code O(m)} where {@code m} is the length
 * of the key or prefix being processed. Notably, that cost is completely independent of how
 * many other keys are already stored: 100 keys or 100,000, a lookup for the same prefix costs
 * exactly the same, which is the property this module's benchmark measures directly.
 */
public final class Trie {

    private final Node root = new Node();
    private int size;

    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public void insert(String key) {
        Node node = root;
        for (int i = 0; i < key.length(); i++) {
            char c = key.charAt(i);
            node = node.children.computeIfAbsent(c, ignored -> new Node());
        }
        if (!node.isWordEnd) {
            node.isWordEnd = true;
            size++;
        }
    }

    /** True only if {@code key} was itself inserted — a stored prefix of a longer key doesn't count. */
    public boolean contains(String key) {
        Node node = findNode(key);
        return node != null && node.isWordEnd;
    }

    /** True if any inserted key starts with {@code prefix} (including a key equal to it). */
    public boolean startsWith(String prefix) {
        // The root node exists unconditionally (it's a field, not something insert() creates),
        // so findNode("") would otherwise return it even when nothing has ever been inserted.
        // Guarding on emptiness keeps startsWith("") vacuously false for an empty trie, matching
        // "is there any stored key with this prefix" when there are no stored keys at all.
        if (isEmpty()) {
            return false;
        }
        return findNode(prefix) != null;
    }

    private Node findNode(String key) {
        Node node = root;
        for (int i = 0; i < key.length(); i++) {
            char c = key.charAt(i);
            node = node.children.get(c);
            if (node == null) {
                return null;
            }
        }
        return node;
    }

    private static final class Node {
        final Map<Character, Node> children = new HashMap<>();
        boolean isWordEnd;
    }
}
