package com.datastructures.trees.btree.applied;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class AccountIndexSimulationTest {

    @Test
    void startsEmpty() {
        AccountIndexSimulation index = new AccountIndexSimulation();

        assertThat(index.size()).isZero();
        assertThat(index.height()).isZero();
    }

    @Test
    void indexedAccountIsFoundByAccountNumber() {
        AccountIndexSimulation index = new AccountIndexSimulation();
        AccountRecord record = new AccountRecord(1001L, "Leonardo Gomes", BigDecimal.valueOf(5000));

        index.index(record);

        assertThat(index.lookup(1001L)).isEqualTo(record);
        assertThat(index.size()).isEqualTo(1);
    }

    @Test
    void lookupOnAnUnindexedAccountNumberReturnsNull() {
        AccountIndexSimulation index = new AccountIndexSimulation();
        index.index(new AccountRecord(1001L, "Leonardo Gomes", BigDecimal.valueOf(5000)));

        assertThat(index.lookup(9999L)).isNull();
    }

    @Test
    void indexingManyAccountsWithAHighBranchingFactorStaysVeryShallow() {
        AccountIndexSimulation index = new AccountIndexSimulation(); // default minDegree = 32

        for (long accountNumber = 1; accountNumber <= 100_000; accountNumber++) {
            index.index(new AccountRecord(accountNumber, "holder-" + accountNumber, BigDecimal.ZERO));
        }

        assertThat(index.size()).isEqualTo(100_000);
        // log_32(100,000) ~= 3.4, so a handful of levels comfortably covers 100k accounts -
        // this is the concrete "far fewer disk-page reads" claim from this class's Javadoc.
        assertThat(index.height()).isLessThanOrEqualTo(4);
        assertThat(index.lookup(1L).holderName()).isEqualTo("holder-1");
        assertThat(index.lookup(100_000L).holderName()).isEqualTo("holder-100000");
    }

    @Test
    void aLowerMinDegreeProducesATallerIndexForTheSameAccountCount() {
        AccountIndexSimulation wideIndex = new AccountIndexSimulation(32);
        AccountIndexSimulation narrowIndex = new AccountIndexSimulation(2);

        for (long accountNumber = 1; accountNumber <= 1_000; accountNumber++) {
            AccountRecord record = new AccountRecord(accountNumber, "holder-" + accountNumber, BigDecimal.ZERO);
            wideIndex.index(record);
            narrowIndex.index(record);
        }

        assertThat(narrowIndex.height()).isGreaterThan(wideIndex.height());
    }
}
