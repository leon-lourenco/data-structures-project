package com.datastructures.hashing.hashtable.classic;

import org.junit.jupiter.api.Test;

import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class HashTableTest {

    @Test
    void startsEmpty() {
        HashTable<String, Integer> table = new HashTable<>();

        assertThat(table.isEmpty()).isTrue();
        assertThat(table.size()).isZero();
    }

    @Test
    void putThenGetReturnsTheStoredValue() {
        HashTable<String, Integer> table = new HashTable<>();

        table.put("a", 1);

        assertThat(table.get("a")).isEqualTo(1);
        assertThat(table.size()).isEqualTo(1);
        assertThat(table.isEmpty()).isFalse();
    }

    @Test
    void getOnAMissingKeyReturnsNull() {
        HashTable<String, Integer> table = new HashTable<>();

        assertThat(table.get("missing")).isNull();
        assertThat(table.containsKey("missing")).isFalse();
    }

    @Test
    void puttingAnExistingKeyOverwritesTheValueAndReturnsThePrevious() {
        HashTable<String, Integer> table = new HashTable<>();
        table.put("a", 1);

        Integer previous = table.put("a", 2);

        assertThat(previous).isEqualTo(1);
        assertThat(table.get("a")).isEqualTo(2);
        assertThat(table.size()).isEqualTo(1);
    }

    @Test
    void removeDeletesTheEntryAndReturnsItsValue() {
        HashTable<String, Integer> table = new HashTable<>();
        table.put("a", 1);

        Integer removed = table.remove("a");

        assertThat(removed).isEqualTo(1);
        assertThat(table.containsKey("a")).isFalse();
        assertThat(table.size()).isZero();
    }

    @Test
    void removingAMissingKeyThrows() {
        HashTable<String, Integer> table = new HashTable<>();

        assertThatThrownBy(() -> table.remove("missing")).isInstanceOf(NoSuchElementException.class);
    }

    @Test
    void keysThatCollideOnTheSameBucketAreAllRetrievableIndependently() {
        HashTable<CollidingKey, String> table = new HashTable<>();
        CollidingKey first = new CollidingKey("first");
        CollidingKey second = new CollidingKey("second");
        CollidingKey third = new CollidingKey("third");

        table.put(first, "1");
        table.put(second, "2");
        table.put(third, "3");

        assertThat(table.get(first)).isEqualTo("1");
        assertThat(table.get(second)).isEqualTo("2");
        assertThat(table.get(third)).isEqualTo("3");
        assertThat(table.size()).isEqualTo(3);
    }

    @Test
    void removingOneCollidingKeyDoesNotAffectItsBucketmates() {
        HashTable<CollidingKey, String> table = new HashTable<>();
        CollidingKey first = new CollidingKey("first");
        CollidingKey second = new CollidingKey("second");
        table.put(first, "1");
        table.put(second, "2");

        table.remove(first);

        assertThat(table.containsKey(first)).isFalse();
        assertThat(table.get(second)).isEqualTo("2");
    }

    @Test
    void growingPastTheLoadFactorResizesAndKeepsEveryEntryRetrievable() {
        HashTable<Integer, Integer> table = new HashTable<>();
        int initialBucketCount = table.bucketCount();

        for (int i = 0; i < 1000; i++) {
            table.put(i, i * 10);
        }

        assertThat(table.bucketCount()).isGreaterThan(initialBucketCount);
        assertThat(table.size()).isEqualTo(1000);
        for (int i = 0; i < 1000; i++) {
            assertThat(table.get(i)).isEqualTo(i * 10);
        }
    }

    @Test
    void putRejectsNullKeys() {
        HashTable<String, Integer> table = new HashTable<>();

        assertThatThrownBy(() -> table.put(null, 1)).isInstanceOf(NullPointerException.class);
    }

    /** A key whose hashCode is fixed regardless of content, to force every instance into the same bucket. */
    private static final class CollidingKey {
        private final String label;

        CollidingKey(String label) {
            this.label = label;
        }

        @Override
        public int hashCode() {
            return 42;
        }

        @Override
        public boolean equals(Object other) {
            return other instanceof CollidingKey that && this.label.equals(that.label);
        }
    }
}
