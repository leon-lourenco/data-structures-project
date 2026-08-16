package com.datastructures.trees.avltree.applied;

/** One fraud-detection rule that fires at an exact risk-score threshold. */
public record FraudRule(int riskScoreThreshold, String ruleId, String action) {
}
