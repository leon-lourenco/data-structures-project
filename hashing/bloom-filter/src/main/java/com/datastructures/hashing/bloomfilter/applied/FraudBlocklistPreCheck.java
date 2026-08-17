package com.datastructures.hashing.bloomfilter.applied;

import com.datastructures.hashing.bloomfilter.classic.BloomFilter;

/**
 * A fraud/account-blocklist pre-check: wraps a {@link BloomFilter} of known
 * fraudulent CPFs/account IDs so a caller can skip a real database/service round trip for the
 * common case of a clean ID, without ever risking a wrongly-skipped check on a genuinely blocked
 * one.
 *
 * <p><b>The asymmetry this class exists to make safe:</b> {@link #mightBeBlocked} can only ever
 * answer in one of two ways. {@code false} is a guaranteed-correct "definitely not on the
 * blocklist" — a Bloom filter never false-negatives, so the caller can skip the real check
 * entirely and save the round trip. {@code true} means "possibly on the blocklist" — it could be
 * a false positive, so the caller MUST still confirm against the real source of truth (the
 * fraud/blocklist service or database) before acting on it, e.g. before actually declining a
 * transaction. This class only ever saves work on the negative path; it is never itself the
 * authority on whether an ID is blocked.
 */
public final class FraudBlocklistPreCheck {

    private final BloomFilter<String> blockedIds;

    public FraudBlocklistPreCheck(int expectedBlockedIds, double falsePositiveRate) {
        this.blockedIds = new BloomFilter<>(expectedBlockedIds, falsePositiveRate);
    }

    /** Registers {@code id} as known-fraudulent in the pre-check filter. */
    public void block(String id) {
        blockedIds.add(id);
    }

    /**
     * {@code false}: {@code id} is definitely not on the blocklist — safe to skip the real
     * DB/service round trip entirely. {@code true}: {@code id} might be on the blocklist (this
     * could be a false positive) — the caller must still confirm against the real source of
     * truth before treating it as actually blocked.
     */
    public boolean mightBeBlocked(String id) {
        return blockedIds.mightContain(id);
    }
}
