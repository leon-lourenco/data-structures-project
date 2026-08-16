package com.datastructures.trees.avltree.classic;

/**
 * A self-balancing binary search tree built from scratch: every insert restores
 * {@code |balance factor| <= 1} at every node on the path back to the root, via the standard
 * four rotation cases (left-left, right-right, left-right, right-left). Unlike this repo's
 * plain {@code BinarySearchTree}, insertion order can never degenerate this tree's height past
 * roughly {@code O(log n)} — that guarantee is the entire reason this module exists, and the
 * benchmark measures it directly against the same sorted-insertion sequence that degenerates
 * the plain BST's height to {@code n}.
 *
 * <p>Delete is intentionally not implemented: rebalancing on delete needs the same four
 * rotation cases plus extra bookkeeping for the two-children splice case, which is a lot of
 * additional code for no new teaching value over what insert's rebalancing already
 * demonstrates. Insert, lookup, and the rebalancing invariant are the point of this module.
 */
public final class AvlTree<K extends Comparable<K>, V> {

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
        return node == null ? 0 : node.height;
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
            return node;
        }
        updateHeight(node);
        return rebalance(node);
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
     * Restores the AVL invariant at {@code node}, which was just made 1 taller on one side by
     * the insert that recursed through it. A node can only ever be off by exactly 2 at this
     * point (insert only ever grows one subtree by 1 at a time), so a single rotation decision
     * is always enough.
     */
    private Node<K, V> rebalance(Node<K, V> node) {
        int balance = balanceFactor(node);
        if (balance > 1) {
            if (balanceFactor(node.left) < 0) {
                node.left = rotateLeft(node.left); // left-right case
            }
            return rotateRight(node); // left-left case
        }
        if (balance < -1) {
            if (balanceFactor(node.right) > 0) {
                node.right = rotateRight(node.right); // right-left case
            }
            return rotateLeft(node); // right-right case
        }
        return node;
    }

    private int balanceFactor(Node<K, V> node) {
        return height(node.left) - height(node.right);
    }

    private void updateHeight(Node<K, V> node) {
        node.height = 1 + Math.max(height(node.left), height(node.right));
    }

    private Node<K, V> rotateRight(Node<K, V> y) {
        Node<K, V> x = y.left;
        Node<K, V> transferred = x.right;
        x.right = y;
        y.left = transferred;
        updateHeight(y);
        updateHeight(x);
        return x;
    }

    private Node<K, V> rotateLeft(Node<K, V> x) {
        Node<K, V> y = x.right;
        Node<K, V> transferred = y.left;
        y.left = x;
        x.right = transferred;
        updateHeight(x);
        updateHeight(y);
        return y;
    }

    private static final class Node<K, V> {
        K key;
        V value;
        Node<K, V> left;
        Node<K, V> right;
        int height = 1;

        Node(K key, V value) {
            this.key = key;
            this.value = value;
        }
    }
}
