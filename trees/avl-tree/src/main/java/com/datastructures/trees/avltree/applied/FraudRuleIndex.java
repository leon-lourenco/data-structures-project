package com.datastructures.trees.avltree.applied;

import com.datastructures.trees.avltree.classic.AvlTree;

import java.util.Optional;

/**
 * An ordered index of fraud-detection rules, keyed by the exact risk score at which each rule
 * fires. Compliance and fraud-ops teams tend to register rules in ascending threshold order as
 * new tiers get rolled out ("add a rule at 700, then one at 750, then one at 800..."), which is
 * exactly the sorted insertion pattern that degrades this repo's plain {@code BinarySearchTree}
 * to O(n) height. An {@link AvlTree} gives a guaranteed O(log n) lookup regardless of the order
 * rules were registered in, which matters here because rule evaluation sits on the hot path of
 * every scored transaction.
 */
public final class FraudRuleIndex {

    private final AvlTree<Integer, FraudRule> rulesByThreshold = new AvlTree<>();

    public void registerRule(FraudRule rule) {
        rulesByThreshold.insert(rule.riskScoreThreshold(), rule);
    }

    /** The rule registered at exactly this risk score, if any. */
    public Optional<FraudRule> ruleAt(int riskScoreThreshold) {
        return Optional.ofNullable(rulesByThreshold.get(riskScoreThreshold));
    }

    public boolean hasRuleAt(int riskScoreThreshold) {
        return rulesByThreshold.contains(riskScoreThreshold);
    }

    public int size() {
        return rulesByThreshold.size();
    }
}
