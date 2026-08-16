package com.datastructures.graphs.unionfind.applied;

import com.datastructures.graphs.unionfind.classic.UnionFind;

import java.util.HashMap;
import java.util.Map;

/**
 * Incremental fraud-ring clustering (fraud platform): every account and every identifying signal it's
 * ever been observed with (a device fingerprint, a phone number, a shared IP) is a node in the
 * same union-find. Whenever two nodes are observed together — an account used from a device, or
 * two accounts sharing a phone number — they're unioned on the spot, no batch recomputation
 * needed. Answering "are these two accounts part of the same fraud ring?" is then just
 * {@link UnionFind#connected}, near O(1) amortized, even though the two accounts may never have
 * shared a signal directly and are only linked transitively through several intermediate nodes.
 *
 * <p>The union-find below is sized up front (a realistic constraint: a fraud pipeline sizes its
 * clustering structure for an expected daily volume of accounts + signals, the same way it would
 * size any other fixed in-memory structure) rather than growing dynamically, which keeps this
 * class a thin, honest wrapper around the classic structure instead of reintroducing dynamic
 * array-growth logic that this repo's Dynamic Array module already covers.
 */
public final class FraudRingDetector {

    private final Map<String, Integer> indexOf = new HashMap<>();
    private final UnionFind clusters;
    private int nextIndex;

    public FraudRingDetector(int expectedEntityCount) {
        this.clusters = new UnionFind(expectedEntityCount);
    }

    /** Records that {@code entityA} and {@code entityB} were observed sharing an identifying signal. */
    public void linkObservedTogether(String entityA, String entityB) {
        clusters.union(indexOf(entityA), indexOf(entityB));
    }

    /**
     * Whether {@code entityA} and {@code entityB} are in the same fraud cluster — directly
     * linked, transitively linked through other accounts/signals, or the exact same entity.
     * Either identifier being unknown means they can't share a cluster.
     */
    public boolean sameFraudCluster(String entityA, String entityB) {
        Integer indexA = indexOf.get(entityA);
        Integer indexB = indexOf.get(entityB);
        if (indexA == null || indexB == null) {
            return false;
        }
        return clusters.connected(indexA, indexB);
    }

    private int indexOf(String entityId) {
        Integer existing = indexOf.get(entityId);
        if (existing != null) {
            return existing;
        }
        if (nextIndex >= clusters.size()) {
            throw new IllegalStateException(
                    "fraud-ring detector capacity (" + clusters.size() + ") exceeded");
        }
        int assigned = nextIndex++;
        indexOf.put(entityId, assigned);
        return assigned;
    }
}
