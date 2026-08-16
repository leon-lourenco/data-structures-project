package com.datastructures.trees.binarysearchtree.applied;

import com.datastructures.trees.binarysearchtree.classic.BinarySearchTree;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Optional;

/**
 * Resolves which BACEN transaction-limit tier applies to a given PIX amount, without a query
 * to whatever service owns the regulatory table. This is the operation a hash table structurally
 * can't offer: tiers are defined by threshold ("R$1,000 and above"), so answering "which tier
 * applies to R$1,347.50?" needs an ordered floor lookup (largest threshold {@code <=} amount),
 * not an exact-match lookup. A {@link BinarySearchTree} gives that in O(height) by construction;
 * a hash table would need a full scan to find it.
 */
public final class TransactionLimitTierIndex {

    private final BinarySearchTree<BigDecimal, TransactionLimitTier> tiersByThreshold = new BinarySearchTree<>();

    public void registerTier(TransactionLimitTier tier) {
        tiersByThreshold.insert(tier.thresholdAmount(), tier);
    }

    /** The applicable tier for {@code amount}, or empty if it falls below every registered threshold. */
    public Optional<TransactionLimitTier> tierFor(BigDecimal amount) {
        Map.Entry<BigDecimal, TransactionLimitTier> floor = tiersByThreshold.floorEntry(amount);
        return Optional.ofNullable(floor).map(Map.Entry::getValue);
    }
}
