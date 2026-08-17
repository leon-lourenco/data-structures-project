package com.datastructures.linear.matrix.applied;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PremiumRatingGridTest {

    @Test
    void setMultiplierThenMultiplierForReturnsIt() {
        PremiumRatingGrid grid = new PremiumRatingGrid(3, 4);

        grid.setMultiplier(1, 2, 1.35);

        assertThat(grid.multiplierFor(1, 2)).isEqualTo(1.35);
    }

    @Test
    void differentCellsHoldIndependentMultipliers() {
        PremiumRatingGrid grid = new PremiumRatingGrid(2, 2);

        grid.setMultiplier(0, 0, 1.0);
        grid.setMultiplier(0, 1, 1.2);
        grid.setMultiplier(1, 0, 1.5);
        grid.setMultiplier(1, 1, 2.0);

        assertThat(grid.multiplierFor(0, 0)).isEqualTo(1.0);
        assertThat(grid.multiplierFor(0, 1)).isEqualTo(1.2);
        assertThat(grid.multiplierFor(1, 0)).isEqualTo(1.5);
        assertThat(grid.multiplierFor(1, 1)).isEqualTo(2.0);
    }

    @Test
    void multiplierForOnAnUnsetCellThrows() {
        PremiumRatingGrid grid = new PremiumRatingGrid(2, 2);

        assertThatThrownBy(() -> grid.multiplierFor(0, 0)).isInstanceOf(IllegalStateException.class);
    }

    @Test
    void setMultiplierRejectsAZeroOrNegativeMultiplier() {
        PremiumRatingGrid grid = new PremiumRatingGrid(2, 2);

        assertThatThrownBy(() -> grid.setMultiplier(0, 0, 0.0)).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> grid.setMultiplier(0, 0, -1.0)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void multiplierForOnAnOutOfRangeCellPropagatesTheUnderlyingBoundsCheck() {
        PremiumRatingGrid grid = new PremiumRatingGrid(2, 2);

        assertThatThrownBy(() -> grid.multiplierFor(5, 0)).isInstanceOf(IndexOutOfBoundsException.class);
    }
}
