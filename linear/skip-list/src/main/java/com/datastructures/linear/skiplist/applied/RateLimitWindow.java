package com.datastructures.linear.skiplist.applied;

import com.datastructures.linear.skiplist.classic.SkipList;

/**
 * An ordered index for a sliding rate-limiter window: every accepted request is recorded under
 * its timestamp (epoch millis), and the window is periodically trimmed by dropping every
 * timestamp older than a cutoff.
 *
 * <p>This is the direct contrast with this repo's {@code hashing.hashtable.applied
 * .IdempotencyKeyCache#evictOlderThan}: that cache is backed by a hash table, which has no
 * ordering, so expiring old entries there is an honest full O(n) scan over every tracked key.
 * Here, {@link #evictOlderThan} instead walks the {@link SkipList}'s own ascending key order —
 * {@link SkipList#firstKey()} is O(1) (the smallest key is always the sentinel's level-0
 * successor) and each {@link SkipList#remove} is O(log n) — so evicting {@code k} expired
 * timestamps costs O(k log n), not O(n) over every timestamp still in the window. The skip
 * list's ordering is what makes that possible; a hash table structurally can't offer it.
 */
public final class RateLimitWindow {

    private final SkipList<Long, Integer> requestCountsByTimestamp = new SkipList<>();

    /** Records one request at {@code timestampMillis}, incrementing that timestamp's count. */
    public void recordRequest(long timestampMillis) {
        Integer existingCount = requestCountsByTimestamp.get(timestampMillis);
        int updatedCount = (existingCount == null ? 0 : existingCount) + 1;
        requestCountsByTimestamp.put(timestampMillis, updatedCount);
    }

    public int requestCountAt(long timestampMillis) {
        Integer count = requestCountsByTimestamp.get(timestampMillis);
        return count == null ? 0 : count;
    }

    /** Number of distinct timestamps currently tracked in the window. */
    public int size() {
        return requestCountsByTimestamp.size();
    }

    /**
     * Drops every timestamp older than {@code cutoffMillis} by repeatedly reading and removing
     * the smallest remaining key — O(k log n) for k evicted timestamps, since the skip list
     * keeps its keys ordered instead of requiring a full scan.
     */
    public void evictOlderThan(long cutoffMillis) {
        Long smallestTimestamp = requestCountsByTimestamp.firstKey();
        while (smallestTimestamp != null && smallestTimestamp < cutoffMillis) {
            requestCountsByTimestamp.remove(smallestTimestamp);
            smallestTimestamp = requestCountsByTimestamp.firstKey();
        }
    }
}
