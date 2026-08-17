package com.datastructures.trees.btree.applied;

import java.math.BigDecimal;

/** One indexed account record in a legacy bank's mainframe-modernization account index. */
public record AccountRecord(long accountNumber, String holderName, BigDecimal balance) {
}
