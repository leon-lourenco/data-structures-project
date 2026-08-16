package com.datastructures.trees.binarysearchtree.classic;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * An unbalanced binary search tree built from scratch: every node's key is greater than every
 * key in its left subtree and smaller than every key in its right subtree, which is what makes
 * {@link #get}, {@link #floorEntry}, and in-order traversal all work without ever touching a
 * subtree that can't contain the answer.
 *
 * <p>Nothing here rebalances. Insert order controls the shape: random insertion order tends
 * toward roughly {@code O(log n)} height, but a sorted (or reverse-sorted) insertion order
 * degenerates the tree into a linked list, with {@code O(n)} height and {@code O(n)} lookups.
 * The benchmark in this module measures exactly that gap; the AVL/Red-Black module this repo
 * will add later exists specifically to remove it by rebalancing on every insert.
 */
public final class BinarySearchTree<K extends Comparable<K>, V> {

    private Node<K, V> root;
    private int size;

    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    /** Length of the longest root-to-leaf path; 0 for an empty tree, 1 for a single node. */
    public int height() {
        return height(root);
    }

    private int height(Node<K, V> node) {
        if (node == null) {
            return 0;
        }
        return 1 + Math.max(height(node.left), height(node.right));
    }

    public void insert(K key, V value) {
        root = insert(root, key, value);
    }

    private Node<K, V> insert(Node<K, V> node, K key, V value) {
        if (node == null) {
            size++;
            return new Node<>(key, value);
        }
        int comparison = key.compareTo(node.key);
        if (comparison < 0) {
            node.left = insert(node.left, key, value);
        } else if (comparison > 0) {
            node.right = insert(node.right, key, value);
        } else {
            node.value = value;
        }
        return node;
    }

    public V get(K key) {
        Node<K, V> node = findNode(root, key);
        return node == null ? null : node.value;
    }

    public boolean contains(K key) {
        return findNode(root, key) != null;
    }

    private Node<K, V> findNode(Node<K, V> node, K key) {
        while (node != null) {
            int comparison = key.compareTo(node.key);
            if (comparison < 0) {
                node = node.left;
            } else if (comparison > 0) {
                node = node.right;
            } else {
                return node;
            }
        }
        return null;
    }

    /**
     * The entry with the largest key that is still {@code <= key}, or {@code null} if every
     * stored key is greater than {@code key}. A hash table can't answer this in less than a
     * full scan; a BST answers it in O(height) by walking down and remembering the best
     * candidate seen so far.
     */
    public Map.Entry<K, V> floorEntry(K key) {
        Node<K, V> node = root;
        Node<K, V> best = null;
        while (node != null) {
            int comparison = key.compareTo(node.key);
            if (comparison == 0) {
                return Map.entry(node.key, node.value);
            }
            if (comparison < 0) {
                node = node.left;
            } else {
                best = node;
                node = node.right;
            }
        }
        return best == null ? null : Map.entry(best.key, best.value);
    }

    public void delete(K key) {
        root = delete(root, key);
    }

    private Node<K, V> delete(Node<K, V> node, K key) {
        if (node == null) {
            return null;
        }
        int comparison = key.compareTo(node.key);
        if (comparison < 0) {
            node.left = delete(node.left, key);
            return node;
        }
        if (comparison > 0) {
            node.right = delete(node.right, key);
            return node;
        }

        size--;
        if (node.left == null) {
            return node.right;
        }
        if (node.right == null) {
            return node.left;
        }
        // Two children: splice in the in-order successor (smallest key in the right subtree)
        // so the BST property still holds, then delete that successor from where it was.
        Node<K, V> successor = min(node.right);
        node.key = successor.key;
        node.value = successor.value;
        size++; // delete(...) below will decrement once more for the successor's removal.
        node.right = delete(node.right, successor.key);
        return node;
    }

    private Node<K, V> min(Node<K, V> node) {
        while (node.left != null) {
            node = node.left;
        }
        return node;
    }

    /** Every key in ascending order — the traversal a BST gives you almost for free. */
    public List<K> inOrderKeys() {
        List<K> keys = new ArrayList<>(size);
        inOrder(root, keys);
        return keys;
    }

    private void inOrder(Node<K, V> node, List<K> keys) {
        if (node == null) {
            return;
        }
        inOrder(node.left, keys);
        keys.add(node.key);
        inOrder(node.right, keys);
    }

    private static final class Node<K, V> {
        K key;
        V value;
        Node<K, V> left;
        Node<K, V> right;

        Node(K key, V value) {
            this.key = key;
            this.value = value;
        }
    }
}
