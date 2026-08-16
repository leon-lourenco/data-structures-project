package com.datastructures.trees.avltree.classic;

import com.datastructures.trees.binarysearchtree.classic.BinarySearchTree;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class AvlTreeTest {

    @Test
    void startsEmpty() {
        AvlTree<Integer, String> tree = new AvlTree<>();

        assertThat(tree.isEmpty()).isTrue();
        assertThat(tree.size()).isZero();
        assertThat(tree.height()).isZero();
    }

    @Test
    void insertThenGetReturnsTheStoredValue() {
        AvlTree<Integer, String> tree = new AvlTree<>();

        tree.insert(50, "root");

        assertThat(tree.get(50)).isEqualTo("root");
        assertThat(tree.contains(50)).isTrue();
        assertThat(tree.isEmpty()).isFalse();
        assertThat(tree.height()).isEqualTo(1);
    }

    @Test
    void getOnAMissingKeyReturnsNull() {
        AvlTree<Integer, String> tree = new AvlTree<>();
        tree.insert(50, "root");

        assertThat(tree.get(99)).isNull();
        assertThat(tree.contains(99)).isFalse();
    }

    @Test
    void insertingAnExistingKeyOverwritesItsValueWithoutChangingShapeOrSize() {
        AvlTree<Integer, String> tree = new AvlTree<>();
        tree.insert(50, "original");
        tree.insert(20, "20");
        int heightBefore = tree.height();

        tree.insert(50, "replaced");

        assertThat(tree.get(50)).isEqualTo("replaced");
        assertThat(tree.size()).isEqualTo(2);
        assertThat(tree.height()).isEqualTo(heightBefore);
    }

    // --- The four rotation cases. Each sequence is hand-picked so the third insert is the one
    // that pushes the tree's root out of balance, and the resulting height (2, not 3) is what
    // proves a rotation actually happened rather than the tree just being left lopsided.

    @Test
    void leftLeftInsertionOrderTriggersASingleRightRotation() {
        AvlTree<Integer, String> tree = new AvlTree<>();

        tree.insert(30, "30");
        tree.insert(20, "20");
        tree.insert(10, "10"); // 30 becomes unbalanced left-left; single right rotation at 30

        assertThat(tree.height()).isEqualTo(2);
        assertThat(tree.get(30)).isEqualTo("30");
        assertThat(tree.get(20)).isEqualTo("20");
        assertThat(tree.get(10)).isEqualTo("10");
        assertThat(tree.size()).isEqualTo(3);
    }

    @Test
    void rightRightInsertionOrderTriggersASingleLeftRotation() {
        AvlTree<Integer, String> tree = new AvlTree<>();

        tree.insert(10, "10");
        tree.insert(20, "20");
        tree.insert(30, "30"); // 10 becomes unbalanced right-right; single left rotation at 10

        assertThat(tree.height()).isEqualTo(2);
        assertThat(tree.get(10)).isEqualTo("10");
        assertThat(tree.get(20)).isEqualTo("20");
        assertThat(tree.get(30)).isEqualTo("30");
        assertThat(tree.size()).isEqualTo(3);
    }

    @Test
    void leftRightInsertionOrderTriggersALeftRotationThenARightRotation() {
        AvlTree<Integer, String> tree = new AvlTree<>();

        tree.insert(30, "30");
        tree.insert(10, "10");
        tree.insert(20, "20"); // 30 becomes left-heavy with a right-leaning left child

        assertThat(tree.height()).isEqualTo(2);
        assertThat(tree.get(30)).isEqualTo("30");
        assertThat(tree.get(10)).isEqualTo("10");
        assertThat(tree.get(20)).isEqualTo("20");
        assertThat(tree.size()).isEqualTo(3);
    }

    @Test
    void rightLeftInsertionOrderTriggersARightRotationThenALeftRotation() {
        AvlTree<Integer, String> tree = new AvlTree<>();

        tree.insert(10, "10");
        tree.insert(30, "30");
        tree.insert(20, "20"); // 10 becomes right-heavy with a left-leaning right child

        assertThat(tree.height()).isEqualTo(2);
        assertThat(tree.get(10)).isEqualTo("10");
        assertThat(tree.get(30)).isEqualTo("30");
        assertThat(tree.get(20)).isEqualTo("20");
        assertThat(tree.size()).isEqualTo(3);
    }

    @Test
    void sortedInsertionOrderThatDegeneratesAPlainBstStaysBalancedInAnAvlTree() {
        AvlTree<Integer, String> avlTree = new AvlTree<>();
        BinarySearchTree<Integer, String> plainBst = new BinarySearchTree<>();
        for (int key = 0; key < 100; key++) {
            avlTree.insert(key, "v" + key);
            plainBst.insert(key, "v" + key);
        }

        // Same 100-key sorted sequence that this repo's BinarySearchTreeTest proves degenerates
        // the plain BST's height to exactly 100 (a straight chain). The AVL tree rebalances on
        // every insert, so its height stays at exactly 7 instead — right in line with the
        // theoretical log2(100) ≈ 6.64.
        assertThat(plainBst.height()).isEqualTo(100);
        assertThat(avlTree.height()).isEqualTo(7);
        assertThat(avlTree.size()).isEqualTo(100);
        for (int key = 0; key < 100; key++) {
            assertThat(avlTree.get(key)).isEqualTo("v" + key);
        }
    }

    @Test
    void randomishInsertionOrderStaysWellBelowTheDegenerateHeight() {
        AvlTree<Integer, String> tree = new AvlTree<>();
        int[] order = {50, 25, 75, 12, 37, 62, 87, 6, 18, 31, 43, 56, 68, 81, 93};
        for (int key : order) {
            tree.insert(key, "v" + key);
        }

        assertThat(tree.height()).isLessThanOrEqualTo(4);
        assertThat(tree.size()).isEqualTo(order.length);
    }
}
