package com.datastructures.graphs.dijkstra.applied;

import com.datastructures.graphs.dijkstra.classic.WeightedGraph;

import java.util.Map;
import java.util.Optional;

/**
 * Cheapest-route interbank settlement routing. Moving money from a source account to a
 * destination account rarely happens over a single direct rail — it usually hops through one or
 * more correspondent banks, and each hop (a PIX transfer, a TED, a Boleto settlement) carries
 * its own fee. This router models every known correspondent-bank hop as a graph edge weighted
 * by its fee, and uses Dijkstra's algorithm to find the minimum-total-fee path between any two
 * accounts, instead of hardcoding a single fixed route or always taking the fewest hops.
 */
public final class InterbankSettlementRouter {

    private final WeightedGraph<BankAccount> rails = new WeightedGraph<>();

    /** Registers a settlement rail between two accounts, with a fee in cents (either direction). */
    public void registerRail(BankAccount from, BankAccount to, long feeInCents) {
        rails.addEdge(from, to, feeInCents);
    }

    /**
     * The cheapest total fee (in cents) to move funds from {@code source} to {@code
     * destination}, or empty if no chain of known rails connects them.
     */
    public Optional<Long> cheapestSettlementFee(BankAccount source, BankAccount destination) {
        Map<BankAccount, Long> feesFromSource = rails.shortestPathFrom(source);
        return Optional.ofNullable(feesFromSource.get(destination));
    }
}
