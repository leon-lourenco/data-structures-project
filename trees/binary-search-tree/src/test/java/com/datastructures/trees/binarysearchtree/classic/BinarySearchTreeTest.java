package com.datastructures.trees.binarysearchtree.classic;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class BinarySearchTreeTest {

    @Test
    void startsEmpty() {
        BinarySearchTree<Integer, String> tree = new BinarySearchTree<>();

        assertThat(tree.isEmpty()).isTrue();
        assertThat(tree.size()).isZero();
        assertThat(tree.height()).isZero();
    }

    @Test
    void insertThenGetReturnsTheStoredValue() {
        BinarySearchTree<Integer, String> tree = new BinarySearchTree<>();

        tree.insert(50, "root");

        assertThat(tree.get(50)).isEqualTo("root");
        assertThat(tree.contains(50)).isTrue();
        assertThat(tree.isEmpty()).isFalse();
    }

    @Test
    void getOnAMissingKeyReturnsNull() {
        BinarySearchTree<Integer, String> tree = new BinarySearchTree<>();
        tree.insert(50, "root");

        assertThat(tree.get(99)).isNull();
        assertThat(tree.contains(99)).isFalse();
    }

    @Test
    void insertingAnExistingKeyOverwritesItsValue() {
        BinarySearchTree<Integer, String> tree = new BinarySearchTree<>();
        tree.insert(50, "original");

        tree.insert(50, "replaced");

        assertThat(tree.get(50)).isEqualTo("replaced");
        assertThat(tree.size()).isEqualTo(1);
    }

    @Test
    void inOrderKeysAreAlwaysSortedRegardlessOfInsertionOrder() {
        BinarySearchTree<Integer, String> tree = new BinarySearchTree<>();
        int[] insertionOrder = {50, 20, 80, 10, 30, 70, 90};
        for (int key : insertionOrder) {
            tree.insert(key, "v" + key);
        }

        assertThat(tree.inOrderKeys()).containsExactly(10, 20, 30, 50, 70, 80, 90);
    }

    @Test
    void deletingALeafRemovesItAndNothingElse() {
        BinarySearchTree<Integer, String> tree = new BinarySearchTree<>();
        tree.insert(50, "50");
        tree.insert(20, "20");
        tree.insert(80, "80");

        tree.delete(20);

        assertThat(tree.contains(20)).isFalse();
        assertThat(tree.inOrderKeys()).containsExactly(50, 80);
        assertThat(tree.size()).isEqualTo(2);
    }

    @Test
    void deletingANodeWithOneChildSplicesTheChildIntoItsPlace() {
        BinarySearchTree<Integer, String> tree = new BinarySearchTree<>();
        tree.insert(50, "50");
        tree.insert(20, "20");
        tree.insert(10, "10");

        tree.delete(20);

        assertThat(tree.contains(20)).isFalse();
        assertThat(tree.inOrderKeys()).containsExactly(10, 50);
        assertThat(tree.get(10)).isEqualTo("10");
        assertThat(tree.size()).isEqualTo(2);
    }

    @Test
    void deletingANodeWithTwoChildrenSplicesInTheInOrderSuccessor() {
        BinarySearchTree<Integer, String> tree = new BinarySearchTree<>();
        int[] insertionOrder = {50, 20, 80, 10, 30, 70, 90};
        for (int key : insertionOrder) {
            tree.insert(key, "v" + key);
        }

        tree.delete(50);

        assertThat(tree.contains(50)).isFalse();
        assertThat(tree.inOrderKeys()).containsExactly(10, 20, 30, 70, 80, 90);
        assertThat(tree.size()).isEqualTo(6);
        // The successor (70) must have been spliced up, not just copied and left duplicated.
        assertThat(tree.get(70)).isEqualTo("v70");
    }

    @Test
    void deletingAKeyGreaterThanTheRootDescendsRightBeforeMatching() {
        BinarySearchTree<Integer, String> tree = new BinarySearchTree<>();
        int[] insertionOrder = {50, 20, 80, 10, 30, 70, 90};
        for (int key : insertionOrder) {
            tree.insert(key, "v" + key);
        }

        tree.delete(90);

        assertThat(tree.contains(90)).isFalse();
        assertThat(tree.inOrderKeys()).containsExactly(10, 20, 30, 50, 70, 80);
        assertThat(tree.size()).isEqualTo(6);
    }

    @Test
    void deletingFromAnEmptyTreeIsANoOp() {
        BinarySearchTree<Integer, String> tree = new BinarySearchTree<>();

        tree.delete(1);

        assertThat(tree.isEmpty()).isTrue();
    }

    @Test
    void floorEntryReturnsTheExactMatchWhenPresent() {
        BinarySearchTree<Integer, String> tree = new BinarySearchTree<>();
        tree.insert(100, "tier-100");
        tree.insert(500, "tier-500");

        Map.Entry<Integer, String> floor = tree.floorEntry(500);

        assertThat(floor.getKey()).isEqualTo(500);
        assertThat(floor.getValue()).isEqualTo("tier-500");
    }

    @Test
    void floorEntryReturnsTheLargestKeyNotGreaterThanTheQuery() {
        BinarySearchTree<Integer, String> tree = new BinarySearchTree<>();
        tree.insert(100, "tier-100");
        tree.insert(500, "tier-500");
        tree.insert(1000, "tier-1000");

        Map.Entry<Integer, String> floor = tree.floorEntry(750);

        assertThat(floor.getKey()).isEqualTo(500);
    }

    @Test
    void floorEntryReturnsNullWhenTheQueryIsBelowEveryStoredKey() {
        BinarySearchTree<Integer, String> tree = new BinarySearchTree<>();
        tree.insert(100, "tier-100");

        assertThat(tree.floorEntry(50)).isNull();
    }

    @Test
    void sortedInsertionOrderDegeneratesHeightToTheNumberOfNodes() {
        BinarySearchTree<Integer, String> tree = new BinarySearchTree<>();
        for (int key = 0; key < 100; key++) {
            tree.insert(key, "v" + key);
        }

        assertThat(tree.height()).isEqualTo(100);
    }

    @Test
    void randomishInsertionOrderStaysWellBelowTheDegenerateHeight() {
        BinarySearchTree<Integer, String> tree = new BinarySearchTree<>();
        int[] balancedOrder = {50, 25, 75, 12, 37, 62, 87, 6, 18, 31, 43, 56, 68, 81, 93};
        for (int key : balancedOrder) {
            tree.insert(key, "v" + key);
        }

        assertThat(tree.height()).isEqualTo(4);
    }
}
