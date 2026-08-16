package com.datastructures.linear.linkedlist.applied;

import org.junit.jupiter.api.Test;

import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ClaimWorkflowTest {

    @Test
    void startsWithTheDefaultFourStageSequence() {
        ClaimWorkflow workflow = new ClaimWorkflow();

        assertThat(workflow.stageNames())
                .containsExactly("intake", "document-verification", "assessment", "payout");
    }

    @Test
    void insertingAStageAfterAnExistingOneSplicesItInWithoutDisturbingTheRest() {
        ClaimWorkflow workflow = new ClaimWorkflow();

        workflow.insertStageAfter("document-verification", "manual-review");

        assertThat(workflow.stageNames())
                .containsExactly("intake", "document-verification", "manual-review", "assessment", "payout");
    }

    @Test
    void insertingAfterTheLastStageAppendsIt() {
        ClaimWorkflow workflow = new ClaimWorkflow();

        workflow.insertStageAfter("payout", "post-payout-audit");

        assertThat(workflow.stageNames())
                .containsExactly("intake", "document-verification", "assessment", "payout", "post-payout-audit");
    }

    @Test
    void insertingAfterAnUnknownStageThrows() {
        ClaimWorkflow workflow = new ClaimWorkflow();

        assertThatThrownBy(() -> workflow.insertStageAfter("nonexistent", "x"))
                .isInstanceOf(NoSuchElementException.class);
    }
}
