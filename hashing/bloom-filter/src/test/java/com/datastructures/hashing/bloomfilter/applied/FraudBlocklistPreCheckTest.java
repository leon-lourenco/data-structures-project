package com.datastructures.hashing.bloomfilter.applied;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class FraudBlocklistPreCheckTest {

    @Test
    void anIdThatWasNeverBlockedIsSafeToSkipTheRealCheckFor() {
        FraudBlocklistPreCheck preCheck = new FraudBlocklistPreCheck(1000, 0.01);

        assertThat(preCheck.mightBeBlocked("clean-cpf-123")).isFalse();
    }

    @Test
    void aBlockedIdIsAlwaysFlaggedAsPossiblyBlocked() {
        FraudBlocklistPreCheck preCheck = new FraudBlocklistPreCheck(1000, 0.01);

        preCheck.block("fraud-cpf-999");

        assertThat(preCheck.mightBeBlocked("fraud-cpf-999")).isTrue();
    }

    @Test
    void blockingOneIdDoesNotFlagAnUnrelatedCleanId() {
        FraudBlocklistPreCheck preCheck = new FraudBlocklistPreCheck(1000, 0.01);
        preCheck.block("fraud-cpf-999");

        assertThat(preCheck.mightBeBlocked("clean-cpf-123")).isFalse();
    }
}
