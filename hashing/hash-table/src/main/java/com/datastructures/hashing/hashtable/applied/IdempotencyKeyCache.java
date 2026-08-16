package com.datastructures.hashing.hashtable.applied;

import com.datastructures.hashing.hashtable.classic.HashTable;

import java.time.Clock;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * In-memory pre-check for a payment gateway's idempotency-key handling: before a PIX
 * transaction hits the database (where a unique constraint on the idempotency key is the real
 * source of truth), this cache gives an O(1) average-case answer to "have I already seen this
 * key?" for the common case of a client retrying the same request seconds apart, without a
 * round trip.
 *
 * <p>The table intentionally has no ordering, so expiring old entries ({@link
 * #evictOlderThan}) is an O(n) full scan — a hash table alone can't do better. A production
 * cache that needed cheap eviction would pair this with a doubly linked list threaded through
 * the entries (the classic LRU-cache combination), which is exactly the trade-off this module
 * exists to make visible.
 */
public final class IdempotencyKeyCache {

    private final HashTable<String, Instant> processedAt = new HashTable<>();
    // HashTable doesn't expose iteration (it's not needed for the put/get story), so the cache
    // tracks its own key list purely to support the O(n) eviction scan below.
    private final List<String> trackedKeys = new ArrayList<>();
    private final Clock clock;

    public IdempotencyKeyCache(Clock clock) {
        this.clock = clock;
    }

    public boolean isDuplicate(String idempotencyKey) {
        return processedAt.containsKey(idempotencyKey);
    }

    public void markProcessed(String idempotencyKey) {
        if (!processedAt.containsKey(idempotencyKey)) {
            trackedKeys.add(idempotencyKey);
        }
        processedAt.put(idempotencyKey, clock.instant());
    }

    public int size() {
        return processedAt.size();
    }

    /** Full scan: removes every key last marked processed before {@code cutoff}. */
    public void evictOlderThan(Instant cutoff) {
        List<String> expired = new ArrayList<>();
        for (String key : trackedKeys) {
            // trackedKeys and processedAt are always kept in sync (see markProcessed), so
            // every key here is guaranteed to still have an entry.
            if (processedAt.get(key).isBefore(cutoff)) {
                expired.add(key);
            }
        }
        for (String key : expired) {
            processedAt.remove(key);
            trackedKeys.remove(key);
        }
    }
}
