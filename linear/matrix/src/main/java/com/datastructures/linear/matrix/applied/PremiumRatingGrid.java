package com.datastructures.linear.matrix.applied;

import com.datastructures.linear.matrix.classic.Matrix;

/**
 * An insurance premium-rating grid: rows are age brackets, columns are risk zones, and each
 * cell holds the rate multiplier underwriting applies for that combination. Actuarial rating
 * tables are already published in exactly this row/column shape, so backing this with a
 * {@link Matrix} instead of a chain of range checks or a linearly-scanned rule list turns
 * "resolve the multiplier for this quote" into a single O(1) indexed lookup instead of
 * evaluating conditions one by one until one matches.
 */
public final class PremiumRatingGrid {

    private final Matrix<Double> multipliers;

    public PremiumRatingGrid(int ageBracketCount, int riskZoneCount) {
        this.multipliers = new Matrix<>(ageBracketCount, riskZoneCount);
    }

    public void setMultiplier(int ageBracket, int riskZone, double multiplier) {
        if (multiplier <= 0) {
            throw new IllegalArgumentException("multiplier must be > 0");
        }
        multipliers.set(ageBracket, riskZone, multiplier);
    }

    public double multiplierFor(int ageBracket, int riskZone) {
        Double multiplier = multipliers.get(ageBracket, riskZone);
        if (multiplier == null) {
            throw new IllegalStateException(
                    "no multiplier registered for age bracket " + ageBracket + ", risk zone " + riskZone);
        }
        return multiplier;
    }
}
