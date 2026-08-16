package com.datastructures.hashing.hashtable.classic;

import java.util.NoSuchElementException;
import java.util.Objects;

/**
 * A hash table built from scratch with separate chaining — no {@code java.util.HashMap}
 * underneath. Buckets are hand-rolled singly linked chains of {@link Entry} nodes, sized as a
 * power of two so the bucket index can be computed with a mask instead of a modulo. Keys are
 * spread with the same {@code hashCode() ^ (h >>> 16)} trick {@code java.util.HashMap} uses,
 * which folds the high bits into the low bits so a table sized as a power of two (which only
 * ever looks at the low bits of the hash) doesn't collapse hashes that only differ up high
 * into the same bucket.
 *
 * <p>The table resizes (doubles bucket count, rehashes every entry) once the load factor
 * would exceed {@link #LOAD_FACTOR_THRESHOLD} — average O(1) put/get holds only as long as
 * chains stay short, and resizing is what keeps them short as the table grows.
 */
public final class HashTable<K, V> {

    private static final int DEFAULT_BUCKET_COUNT = 16;
    private static final double LOAD_FACTOR_THRESHOLD = 0.75;

    private Entry<K, V>[] buckets;
    private int size;

    @SuppressWarnings("unchecked")
    public HashTable() {
        this.buckets = new Entry[DEFAULT_BUCKET_COUNT];
        this.size = 0;
    }

    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public int bucketCount() {
        return buckets.length;
    }

    public V put(K key, V value) {
        Objects.requireNonNull(key, "key must not be null");
        int index = bucketIndex(key, buckets.length);
        for (Entry<K, V> entry = buckets[index]; entry != null; entry = entry.next) {
            if (entry.key.equals(key)) {
                V previous = entry.value;
                entry.value = value;
                return previous;
            }
        }
        buckets[index] = new Entry<>(key, value, buckets[index]);
        size++;
        resizeIfOverLoaded();
        return null;
    }

    public V get(K key) {
        Objects.requireNonNull(key, "key must not be null");
        int index = bucketIndex(key, buckets.length);
        for (Entry<K, V> entry = buckets[index]; entry != null; entry = entry.next) {
            if (entry.key.equals(key)) {
                return entry.value;
            }
        }
        return null;
    }

    public boolean containsKey(K key) {
        Objects.requireNonNull(key, "key must not be null");
        int index = bucketIndex(key, buckets.length);
        for (Entry<K, V> entry = buckets[index]; entry != null; entry = entry.next) {
            if (entry.key.equals(key)) {
                return true;
            }
        }
        return false;
    }

    public V remove(K key) {
        Objects.requireNonNull(key, "key must not be null");
        int index = bucketIndex(key, buckets.length);
        Entry<K, V> previous = null;
        for (Entry<K, V> entry = buckets[index]; entry != null; entry = entry.next) {
            if (entry.key.equals(key)) {
                if (previous == null) {
                    buckets[index] = entry.next;
                } else {
                    previous.next = entry.next;
                }
                size--;
                return entry.value;
            }
            previous = entry;
        }
        throw new NoSuchElementException("no entry for key " + key);
    }

    private void resizeIfOverLoaded() {
        double loadFactor = (double) size / buckets.length;
        if (loadFactor <= LOAD_FACTOR_THRESHOLD) {
            return;
        }
        @SuppressWarnings("unchecked")
        Entry<K, V>[] resized = new Entry[buckets.length * 2];
        for (Entry<K, V> head : buckets) {
            for (Entry<K, V> entry = head; entry != null; ) {
                Entry<K, V> next = entry.next;
                int newIndex = bucketIndex(entry.key, resized.length);
                entry.next = resized[newIndex];
                resized[newIndex] = entry;
                entry = next;
            }
        }
        buckets = resized;
    }

    private int bucketIndex(K key, int bucketCount) {
        int h = key.hashCode();
        int spread = h ^ (h >>> 16);
        return spread & (bucketCount - 1);
    }

    private static final class Entry<K, V> {
        final K key;
        V value;
        Entry<K, V> next;

        Entry(K key, V value, Entry<K, V> next) {
            this.key = key;
            this.value = value;
            this.next = next;
        }
    }
}
