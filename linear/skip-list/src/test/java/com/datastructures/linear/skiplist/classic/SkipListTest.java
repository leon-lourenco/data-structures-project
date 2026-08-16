package com.datastructures.linear.skiplist.classic;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SkipListTest {

    @Test
    void startsEmpty() {
        SkipList<Integer, String> skipList = new SkipList<>();

        assertThat(skipList.isEmpty()).isTrue();
        assertThat(skipList.size()).isZero();
        assertThat(skipList.firstKey()).isNull();
    }

    @Test
    void putThenGetReturnsTheStoredValue() {
        SkipList<Integer, String> skipList = new SkipList<>();

        skipList.put(50, "fifty");

        assertThat(skipList.get(50)).isEqualTo("fifty");
        assertThat(skipList.contains(50)).isTrue();
        assertThat(skipList.isEmpty()).isFalse();
        assertThat(skipList.size()).isEqualTo(1);
    }

    @Test
    void getOnAMissingKeyReturnsNull() {
        SkipList<Integer, String> skipList = new SkipList<>();
        skipList.put(50, "fifty");

        assertThat(skipList.get(99)).isNull();
        assertThat(skipList.contains(99)).isFalse();
    }

    @Test
    void puttingAnExistingKeyOverwritesItsValueWithoutGrowingSize() {
        SkipList<Integer, String> skipList = new SkipList<>();
        skipList.put(50, "original");

        skipList.put(50, "replaced");

        assertThat(skipList.get(50)).isEqualTo("replaced");
        assertThat(skipList.size()).isEqualTo(1);
    }

    @Test
    void removeExistingKeyReturnsTrueAndTheKeyBecomesAbsent() {
        SkipList<Integer, String> skipList = new SkipList<>();
        skipList.put(50, "fifty");

        boolean removed = skipList.remove(50);

        assertThat(removed).isTrue();
        assertThat(skipList.contains(50)).isFalse();
        assertThat(skipList.get(50)).isNull();
        assertThat(skipList.isEmpty()).isTrue();
    }

    @Test
    void removingAMissingKeyReturnsFalseAndLeavesTheListUnchanged() {
        SkipList<Integer, String> skipList = new SkipList<>();
        skipList.put(50, "fifty");

        boolean removed = skipList.remove(99);

        assertThat(removed).isFalse();
        assertThat(skipList.size()).isEqualTo(1);
        assertThat(skipList.get(50)).isEqualTo("fifty");
    }

    @Test
    void removingFromAnEmptyListReturnsFalse() {
        SkipList<Integer, String> skipList = new SkipList<>();

        assertThat(skipList.remove(1)).isFalse();
    }

    @Test
    void removingAKeyThatFallsBetweenTwoExistingKeysReturnsFalse() {
        SkipList<Integer, String> skipList = new SkipList<>();
        skipList.put(10, "ten");
        skipList.put(30, "thirty");

        boolean removed = skipList.remove(20);

        assertThat(removed).isFalse();
        assertThat(skipList.size()).isEqualTo(2);
        assertThat(skipList.get(10)).isEqualTo("ten");
        assertThat(skipList.get(30)).isEqualTo("thirty");
    }

    @Test
    void firstKeyReturnsTheSmallestKeyRegardlessOfInsertionOrder() {
        SkipList<Integer, String> skipList = new SkipList<>();
        skipList.put(50, "fifty");
        skipList.put(10, "ten");
        skipList.put(80, "eighty");
        skipList.put(30, "thirty");

        assertThat(skipList.firstKey()).isEqualTo(10);
    }

    @Test
    void firstKeyTracksTheNewMinimumAfterTheOldMinimumIsRemoved() {
        SkipList<Integer, String> skipList = new SkipList<>();
        skipList.put(10, "ten");
        skipList.put(20, "twenty");

        skipList.remove(10);

        assertThat(skipList.firstKey()).isEqualTo(20);
    }

    @Test
    void putRejectsNullKeys() {
        SkipList<Integer, String> skipList = new SkipList<>();

        assertThatThrownBy(() -> skipList.put(null, "x")).isInstanceOf(NullPointerException.class);
    }

    /**
     * Inserts 500 keys in shuffled order and reads every one of them back. With p=0.5 across
     * 500 independent coin flips, both outcomes of "does this node's level grow past 1" are hit
     * with overwhelming probability many times over, without needing a seeded Random or any
     * assertion on the exact level structure — only functional correctness is asserted, which is
     * what the module actually promises.
     */
    @Test
    void manyRandomInsertsAreAllRetrievableAndSizeMatchesTheInsertCount() {
        SkipList<Integer, Integer> skipList = new SkipList<>();
        List<Integer> keys = new ArrayList<>();
        for (int i = 0; i < 500; i++) {
            keys.add(i);
        }
        Collections.shuffle(keys);

        for (int key : keys) {
            skipList.put(key, key * 10);
        }

        assertThat(skipList.size()).isEqualTo(500);
        for (int i = 0; i < 500; i++) {
            assertThat(skipList.get(i)).isEqualTo(i * 10);
            assertThat(skipList.contains(i)).isTrue();
        }
        assertThat(skipList.firstKey()).isEqualTo(0);
    }

    /**
     * Removes all 500 previously-inserted keys in a different shuffled order. This exercises
     * every remove-time branch across many nodes: unlinking at levels the removed node
     * participates in, leaving levels it doesn't participate in untouched, and shrinking the
     * list's overall level back down as the tallest nodes are removed.
     */
    @Test
    void removingEveryKeyAfterManyInsertsLeavesAnEmptySkipList() {
        SkipList<Integer, Integer> skipList = new SkipList<>();
        List<Integer> keys = new ArrayList<>();
        for (int i = 0; i < 500; i++) {
            keys.add(i);
            skipList.put(i, i);
        }
        Collections.shuffle(keys);

        for (int key : keys) {
            boolean removed = skipList.remove(key);
            assertThat(removed).isTrue();
        }

        assertThat(skipList.isEmpty()).isTrue();
        assertThat(skipList.size()).isZero();
        assertThat(skipList.firstKey()).isNull();
        for (int i = 0; i < 500; i++) {
            assertThat(skipList.contains(i)).isFalse();
        }
    }
}
