package com.datastructures.trees.btree.classic;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class BTreeTest {

    @Test
    void startsEmpty() {
        BTree<Integer, String> tree = new BTree<>();

        assertThat(tree.isEmpty()).isTrue();
        assertThat(tree.size()).isZero();
        assertThat(tree.height()).isZero();
    }

    @Test
    void constructorRejectsMinDegreeBelowTwo() {
        assertThatThrownBy(() -> new BTree<Integer, String>(1))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("minDegree");
    }

    @Test
    void insertThenGetReturnsTheStoredValue() {
        BTree<Integer, String> tree = new BTree<>(2);

        tree.insert(10, "ten");

        assertThat(tree.get(10)).isEqualTo("ten");
        assertThat(tree.contains(10)).isTrue();
        assertThat(tree.isEmpty()).isFalse();
        assertThat(tree.size()).isEqualTo(1);
        assertThat(tree.height()).isEqualTo(1);
    }

    @Test
    void getOnAMissingKeyReturnsNull() {
        BTree<Integer, String> tree = new BTree<>(2);
        tree.insert(10, "ten");

        assertThat(tree.get(99)).isNull();
        assertThat(tree.contains(99)).isFalse();
    }

    @Test
    void getOnAnEmptyTreeReturnsNull() {
        BTree<Integer, String> tree = new BTree<>(2);

        assertThat(tree.get(1)).isNull();
    }

    @Test
    void insertingAnExistingLeafKeyOverwritesItsValueWithoutGrowingSize() {
        BTree<Integer, String> tree = new BTree<>(2);
        tree.insert(10, "original");

        tree.insert(10, "replaced");

        assertThat(tree.get(10)).isEqualTo("replaced");
        assertThat(tree.size()).isEqualTo(1);
    }

    @Test
    void fillingANodeToCapacityDoesNotYetSplitIt() {
        // t = 2: a node holds up to 2t - 1 = 3 keys before it's full.
        BTree<Integer, String> tree = new BTree<>(2);
        tree.insert(10, "10");
        tree.insert(20, "20");
        tree.insert(30, "30");

        assertThat(tree.height()).isEqualTo(1);
        assertThat(tree.size()).isEqualTo(3);
        assertThat(tree.get(10)).isEqualTo("10");
        assertThat(tree.get(20)).isEqualTo("20");
        assertThat(tree.get(30)).isEqualTo("30");
    }

    @Test
    void insertingIntoAFullRootSplitsItAndGrowsHeight() {
        // t = 2: the 4th insert finds the root full (3 keys) and must preemptively split it.
        BTree<Integer, String> tree = new BTree<>(2);
        tree.insert(10, "10");
        tree.insert(20, "20");
        tree.insert(30, "30");
        tree.insert(40, "40");

        assertThat(tree.height()).isEqualTo(2);
        assertThat(tree.size()).isEqualTo(4);
        for (int key : new int[] {10, 20, 30, 40}) {
            assertThat(tree.get(key)).isEqualTo(String.valueOf(key));
        }
    }

    @Test
    void ascendingInsertionOrderForcesRepeatedSplitsAtMultipleLevels() {
        // t = 2 keeps nodes tiny (max 3 keys), so 200 ascending keys forces splits at several
        // levels, not just at the root.
        BTree<Integer, Integer> tree = new BTree<>(2);
        int count = 200;
        for (int key = 0; key < count; key++) {
            tree.insert(key, key * 10);
        }

        assertThat(tree.size()).isEqualTo(count);
        // A t=2 B-tree of 200 keys is nowhere near the O(n) degenerate height an unbalanced BST
        // would reach for the same sorted insertion order (200) - branching keeps it shallow.
        assertThat(tree.height()).isLessThan(15);
        for (int key = 0; key < count; key++) {
            assertThat(tree.get(key)).isEqualTo(key * 10);
        }
    }

    @Test
    void descendingInsertionOrderAlsoForcesRepeatedSplitsAtMultipleLevels() {
        BTree<Integer, Integer> tree = new BTree<>(2);
        int count = 200;
        for (int key = count - 1; key >= 0; key--) {
            tree.insert(key, key * 10);
        }

        assertThat(tree.size()).isEqualTo(count);
        assertThat(tree.height()).isLessThan(15);
        for (int key = 0; key < count; key++) {
            assertThat(tree.get(key)).isEqualTo(key * 10);
        }
    }

    @Test
    void randomInsertionOrderWithHigherMinDegreeStaysVeryShallow() {
        BTree<Integer, Integer> tree = new BTree<>(16);
        List<Integer> keys = new ArrayList<>();
        for (int i = 0; i < 10_000; i++) {
            keys.add(i);
        }
        Collections.shuffle(keys, new Random(7));
        for (int key : keys) {
            tree.insert(key, key);
        }

        assertThat(tree.size()).isEqualTo(10_000);
        assertThat(tree.height()).isLessThanOrEqualTo(4);
        for (int key : keys) {
            assertThat(tree.get(key)).isEqualTo(key);
        }
    }

    @Test
    void reinsertingAnExistingKeyThatIsCurrentlyTheMedianOfAFullNodeOverwritesItsValue() {
        // t = 2 (max 3 keys/node). Carefully built sequence so that, by the time 33 is
        // reinserted, it sits as the *middle* key of a full node reached only after a
        // preemptive split fires during the descent for this exact insert - this is what
        // exercises the "the split's promoted key equals the key we're inserting" branch.
        BTree<Integer, String> tree = new BTree<>(2);
        int[] buildSequence = {10, 20, 30, 40, 50, 60, 25, 28, 35, 33};
        for (int key : buildSequence) {
            tree.insert(key, "v" + key);
        }

        tree.insert(33, "v33-updated");

        assertThat(tree.get(33)).isEqualTo("v33-updated");
        assertThat(tree.size()).isEqualTo(buildSequence.length);
        // Every other key from the build sequence must have survived untouched.
        for (int key : buildSequence) {
            if (key != 33) {
                assertThat(tree.get(key)).isEqualTo("v" + key);
            }
        }
    }

    @Test
    void reinsertingAnExistingInternalKeyOverwritesItsValueWithoutDescending() {
        // Build a tree with t = 2, forcing at least one internal (non-leaf) key, then reinsert
        // that exact internal key - covers the equality check firing at a non-leaf node.
        BTree<Integer, String> tree = new BTree<>(2);
        tree.insert(10, "10");
        tree.insert(20, "20");
        tree.insert(30, "30");
        tree.insert(40, "40"); // splits the root; 20 becomes the new root's only key.

        tree.insert(20, "20-updated");

        assertThat(tree.get(20)).isEqualTo("20-updated");
        assertThat(tree.size()).isEqualTo(4);
    }

    @Test
    void heightGrowsAsMoreKeysAreInsertedWithASmallBranchingFactor() {
        BTree<Integer, Integer> tree = new BTree<>(2);
        assertThat(tree.height()).isZero();

        tree.insert(1, 1);
        assertThat(tree.height()).isEqualTo(1);

        for (int key = 2; key <= 3; key++) {
            tree.insert(key, key);
        }
        assertThat(tree.height()).isEqualTo(1);

        tree.insert(4, 4);
        assertThat(tree.height()).isEqualTo(2);
    }
}
