package com.datastructures.trees.binarysearchtree.applied;

import java.math.BigDecimal;

/** One BACEN-defined PIX transaction-limit bracket: applies to any amount >= its threshold. */
public record TransactionLimitTier(BigDecimal thresholdAmount, String tierName, BigDecimal requiredHoldPeriodHours) {
}
