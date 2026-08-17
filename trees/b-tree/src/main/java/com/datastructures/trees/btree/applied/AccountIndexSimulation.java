package com.datastructures.trees.btree.applied;

import com.datastructures.trees.btree.classic.BTree;

/**
 * Indexes account records by account number the way a real RDBMS index would during a legacy
 * bank's mainframe-to-microservices modernization: a table with millions of accounts needs "find the
 * account with number X" to stay fast without loading every row.
 *
 * <p>A relational database doesn't index with a binary tree — it uses a B-tree (or a close
 * variant) specifically because of the branching factor. Each B-tree node in a real index is
 * sized to match one disk page, and a page can hold hundreds of keys, not one or two like a
 * binary tree node. That means the tree only needs a handful of levels to index millions of
 * rows, and each level crossed while searching is (in the worst case) one disk-page read — the
 * single most expensive operation in the whole lookup. A binary tree indexing the same millions
 * of rows would need roughly {@code log2(n)} levels instead of {@code log_t(n)}; for a branching
 * factor of 32 that's the difference between needing ~4-5 page reads and ~20 for a million rows.
 * That gap, not asymptotic notation, is the actual reason production databases reach for a
 * B-tree here instead of the binary tree this repo's {@code trees/binary-search-tree} module
 * builds — see this module's benchmark for real height numbers from both structures indexing
 * the same key set.
 */
public final class AccountIndexSimulation {

    /** Matches the branching factor used in this module's height-vs-binary-tree benchmark. */
    public static final int DEFAULT_MIN_DEGREE = 32;

    private final BTree<Long, AccountRecord> indexByAccountNumber;

    public AccountIndexSimulation() {
        this(DEFAULT_MIN_DEGREE);
    }

    public AccountIndexSimulation(int minDegree) {
        this.indexByAccountNumber = new BTree<>(minDegree);
    }

    public void index(AccountRecord record) {
        indexByAccountNumber.insert(record.accountNumber(), record);
    }

    /** The indexed record for {@code accountNumber}, or {@code null} if no such account is indexed. */
    public AccountRecord lookup(long accountNumber) {
        return indexByAccountNumber.get(accountNumber);
    }

    public int size() {
        return indexByAccountNumber.size();
    }

    /** Levels the index has to descend for any lookup — the number this whole module is about. */
    public int height() {
        return indexByAccountNumber.height();
    }
}
