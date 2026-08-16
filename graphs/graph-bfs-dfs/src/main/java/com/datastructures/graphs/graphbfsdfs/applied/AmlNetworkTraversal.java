package com.datastructures.graphs.graphbfsdfs.applied;

import com.datastructures.graphs.graphbfsdfs.classic.Graph;

import java.util.List;

/**
 * Anti-money-laundering network traversal (compliance tooling for a bank/payments fraud team): models
 * account-to-account transaction relationships as an undirected graph - an edge means money
 * moved directly between the two accounts, regardless of direction - and, given a flagged
 * account, finds every other account reachable from it. That reachable set is the connected
 * cluster of accounts potentially involved in the same scheme: money moved two, three, or more
 * hops away from the flagged account is still traceable back to it, which a query limited to
 * "direct counterparties only" would miss entirely.
 *
 * <p>Either traversal (BFS or DFS) visits the same reachable set; this class uses BFS so
 * {@link #accountsReachableFrom} returns accounts ordered by how many transaction hops separate
 * them from the flagged account - the closest, most directly implicated accounts come first,
 * which is a natural triage order for an investigator working the result.
 */
public final class AmlNetworkTraversal {

    private final Graph<String> transactionNetwork = new Graph<>();

    /** Records that money moved directly between these two accounts, in either direction. */
    public void recordTransaction(String fromAccountId, String toAccountId) {
        transactionNetwork.addEdge(fromAccountId, toAccountId);
    }

    /**
     * Every account reachable from {@code flaggedAccountId} - including itself - ordered by
     * transaction-hop distance, closest first. Accounts outside this network's connected
     * component are never even visited, which is exactly what makes this cheaper than a query
     * over every account in the bank.
     */
    public List<String> accountsReachableFrom(String flaggedAccountId) {
        return transactionNetwork.bfs(flaggedAccountId);
    }
}
