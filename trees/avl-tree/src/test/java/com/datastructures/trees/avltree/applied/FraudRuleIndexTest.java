package com.datastructures.trees.avltree.applied;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class FraudRuleIndexTest {

    private final FraudRuleIndex index = new FraudRuleIndex();

    @BeforeEach
    void registerRulesInAscendingThresholdOrder() {
        // Ascending registration order is deliberate: it's the realistic ops workflow (tiers
        // rolled out low-to-high) and exactly the order that would degenerate a plain BST.
        index.registerRule(new FraudRule(300, "low-risk-flag", "LOG_ONLY"));
        index.registerRule(new FraudRule(700, "elevated-risk-review", "FLAG_FOR_REVIEW"));
        index.registerRule(new FraudRule(900, "high-risk-block", "BLOCK"));
    }

    @Test
    void ruleAtARegisteredThresholdIsFound() {
        Optional<FraudRule> rule = index.ruleAt(700);

        assertThat(rule).isPresent();
        assertThat(rule.get().action()).isEqualTo("FLAG_FOR_REVIEW");
        assertThat(index.hasRuleAt(700)).isTrue();
    }

    @Test
    void thereIsNoRuleAtAnUnregisteredThreshold() {
        Optional<FraudRule> rule = index.ruleAt(750);

        assertThat(rule).isEmpty();
        assertThat(index.hasRuleAt(750)).isFalse();
    }

    @Test
    void registeringAtAnExistingThresholdReplacesTheRule() {
        index.registerRule(new FraudRule(700, "elevated-risk-review-v2", "STEP_UP_AUTH"));

        Optional<FraudRule> rule = index.ruleAt(700);

        assertThat(rule).isPresent();
        assertThat(rule.get().ruleId()).isEqualTo("elevated-risk-review-v2");
        assertThat(index.size()).isEqualTo(3);
    }

    @Test
    void sizeReflectsTheNumberOfDistinctRegisteredThresholds() {
        assertThat(index.size()).isEqualTo(3);
    }
}
