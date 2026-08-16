package com.datastructures.trees.trie.classic;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class TrieTest {

    @Test
    void startsEmpty() {
        Trie trie = new Trie();

        assertThat(trie.isEmpty()).isTrue();
        assertThat(trie.size()).isZero();
    }

    @Test
    void insertThenContainsFindsTheExactKey() {
        Trie trie = new Trie();

        trie.insert("cat");

        assertThat(trie.contains("cat")).isTrue();
        assertThat(trie.size()).isEqualTo(1);
        assertThat(trie.isEmpty()).isFalse();
    }

    @Test
    void containsIsFalseWhenTheKeyWasNeverInserted() {
        Trie trie = new Trie();
        trie.insert("cat");

        assertThat(trie.contains("dog")).isFalse();
    }

    @Test
    void containsIsFalseForAStoredPrefixThatWasNeverInsertedAsItsOwnKey() {
        Trie trie = new Trie();
        trie.insert("caterpillar");

        // "cat" exists as a path through the trie (it's a prefix of "caterpillar"), but it was
        // never itself inserted as a complete key, so contains() must say no.
        assertThat(trie.contains("cat")).isFalse();
        assertThat(trie.startsWith("cat")).isTrue();
    }

    @Test
    void insertingTheSameKeyTwiceDoesNotDoubleCountSize() {
        Trie trie = new Trie();
        trie.insert("cat");

        trie.insert("cat");

        assertThat(trie.size()).isEqualTo(1);
        assertThat(trie.contains("cat")).isTrue();
    }

    @Test
    void insertingKeysThatShareAPrefixReusesTheSharedNodes() {
        Trie trie = new Trie();
        trie.insert("car");

        trie.insert("card"); // shares "car" with the first key, then extends with "d"

        assertThat(trie.contains("car")).isTrue();
        assertThat(trie.contains("card")).isTrue();
        assertThat(trie.size()).isEqualTo(2);
    }

    @Test
    void startsWithIsTrueForAnyStoredPrefixOfAKey() {
        Trie trie = new Trie();
        trie.insert("caterpillar");

        assertThat(trie.startsWith("c")).isTrue();
        assertThat(trie.startsWith("cate")).isTrue();
        assertThat(trie.startsWith("caterpillar")).isTrue();
    }

    @Test
    void startsWithIsFalseWhenNoStoredKeyMatchesThePrefixAtAll() {
        Trie trie = new Trie();
        trie.insert("cat");

        assertThat(trie.startsWith("dog")).isFalse();
    }

    @Test
    void startsWithIsFalseWhenThePrefixDivergesPartwayThroughAStoredKey() {
        Trie trie = new Trie();
        trie.insert("cat");

        // Shares "ca" with the stored key but diverges at the third character.
        assertThat(trie.startsWith("cow")).isFalse();
    }

    @Test
    void emptyStringIsAPrefixOfEverythingAndTheEmptyKeyIsHandledAsAWordItself() {
        Trie trie = new Trie();

        assertThat(trie.startsWith("")).isFalse(); // nothing stored yet at all

        trie.insert("");

        assertThat(trie.contains("")).isTrue();
        assertThat(trie.startsWith("")).isTrue();
        assertThat(trie.size()).isEqualTo(1);

        trie.insert("cat");

        assertThat(trie.startsWith("")).isTrue();
        assertThat(trie.size()).isEqualTo(2);
    }
}
