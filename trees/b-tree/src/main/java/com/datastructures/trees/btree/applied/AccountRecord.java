package com.datastructures.trees.btree.applied;

import java.math.BigDecimal;

/** One indexed account record in the mainframe-modernization account index (legacy bank). */
public record AccountRecord(long accountNumber, String holderName, BigDecimal balance) {
}
