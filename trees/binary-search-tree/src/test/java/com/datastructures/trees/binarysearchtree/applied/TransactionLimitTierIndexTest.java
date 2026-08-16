package com.datastructures.trees.binarysearchtree.applied;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class TransactionLimitTierIndexTest {

    private final TransactionLimitTierIndex index = new TransactionLimitTierIndex();

    @BeforeEach
    void registerBacenTiers() {
        index.registerTier(new TransactionLimitTier(BigDecimal.ZERO, "daytime-standard", BigDecimal.ZERO));
        index.registerTier(new TransactionLimitTier(BigDecimal.valueOf(1000), "daytime-elevated", BigDecimal.valueOf(24)));
        index.registerTier(new TransactionLimitTier(BigDecimal.valueOf(10000), "daytime-high-value", BigDecimal.valueOf(72)));
    }

    @Test
    void amountExactlyOnABoundaryResolvesToThatTier() {
        Optional<TransactionLimitTier> tier = index.tierFor(BigDecimal.valueOf(1000));

        assertThat(tier).isPresent();
        assertThat(tier.get().tierName()).isEqualTo("daytime-elevated");
    }

    @Test
    void amountBetweenTwoBoundariesResolvesToTheLowerTier() {
        Optional<TransactionLimitTier> tier = index.tierFor(BigDecimal.valueOf(5000));

        assertThat(tier).isPresent();
        assertThat(tier.get().tierName()).isEqualTo("daytime-elevated");
    }

    @Test
    void amountAboveTheHighestBoundaryResolvesToTheHighestTier() {
        Optional<TransactionLimitTier> tier = index.tierFor(BigDecimal.valueOf(50000));

        assertThat(tier).isPresent();
        assertThat(tier.get().tierName()).isEqualTo("daytime-high-value");
    }

    @Test
    void amountBelowEveryRegisteredThresholdIsAbsentWhenNoZeroFloorTierExists() {
        TransactionLimitTierIndex indexWithoutZeroFloor = new TransactionLimitTierIndex();
        indexWithoutZeroFloor.registerTier(new TransactionLimitTier(BigDecimal.valueOf(1000), "daytime-elevated", BigDecimal.valueOf(24)));

        Optional<TransactionLimitTier> tier = indexWithoutZeroFloor.tierFor(BigDecimal.valueOf(500));

        assertThat(tier).isEmpty();
    }
}
