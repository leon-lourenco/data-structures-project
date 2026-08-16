package com.datastructures.linear.dynamicarray.applied;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * One row of an incoming insurance-premium batch file, as it would arrive from an upstream
 * extraction job before being chunked out to parallel processing workers.
 */
public record PolicyBatchRecord(String policyId, BigDecimal premiumAmount, Instant receivedAt) {
}
