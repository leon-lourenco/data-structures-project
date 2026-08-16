package com.datastructures.hashing.bloomfilter.classic;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class BloomFilterTest {

    @Test
    void constructorComputesAPositiveBitCountAndHashCountForValidParameters() {
        BloomFilter<String> filter = new BloomFilter<>(1000, 0.01);

        assertThat(filter.bitCount()).isPositive();
        assertThat(filter.hashCount()).isPositive();
    }

    @Test
    void constructorRejectsAnExpectedInsertionsBelowOne() {
        assertThatThrownBy(() -> new BloomFilter<String>(0, 0.01))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void constructorRejectsAFalsePositiveRateAtOrBelowZero() {
        assertThatThrownBy(() -> new BloomFilter<String>(1000, 0.0))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void constructorRejectsAFalsePositiveRateAtOrAboveOne() {
        assertThatThrownBy(() -> new BloomFilter<String>(1000, 1.0))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void everyAddedItemIsAlwaysReportedAsMightContain() {
        BloomFilter<String> filter = new BloomFilter<>(1000, 0.01);
        filter.add("alice");
        filter.add("bob");
        filter.add("carol");

        assertThat(filter.mightContain("alice")).isTrue();
        assertThat(filter.mightContain("bob")).isTrue();
        assertThat(filter.mightContain("carol")).isTrue();
    }

    @Test
    void anItemThatWasNeverAddedIsReportedAsDefinitelyAbsent() {
        // A generously-sized filter (expecting 1000 insertions) with only 3 items actually
        // added has a negligible false-positive probability, so this is a safe deterministic
        // assertion, not a probabilistic one dressed up as deterministic.
        BloomFilter<String> filter = new BloomFilter<>(1000, 0.01);
        filter.add("alice");
        filter.add("bob");
        filter.add("carol");

        assertThat(filter.mightContain("dave")).isFalse();
    }

    @Test
    void addRejectsNullItems() {
        BloomFilter<String> filter = new BloomFilter<>(100, 0.01);

        assertThatThrownBy(() -> filter.add(null)).isInstanceOf(NullPointerException.class);
    }

    @Test
    void aSingleExpectedInsertionIsAcceptedAsAValidLowerBound() {
        BloomFilter<String> filter = new BloomFilter<>(1, 0.5);

        filter.add("only");

        assertThat(filter.mightContain("only")).isTrue();
    }
}
