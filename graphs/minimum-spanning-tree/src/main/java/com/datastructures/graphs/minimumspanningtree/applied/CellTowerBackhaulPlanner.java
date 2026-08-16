package com.datastructures.graphs.minimumspanningtree.applied;

import com.datastructures.graphs.minimumspanningtree.classic.KruskalMinimumSpanningTree;
import com.datastructures.graphs.minimumspanningtree.classic.MinimumSpanningTreeResult;
import com.datastructures.graphs.minimumspanningtree.classic.WeightedEdge;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Minimum-cost backhaul network planning (telecom): connecting every cell tower in a build-out
 * back to the core network doesn't need every possible tower-to-tower link built — it needs the
 * cheapest set of links that connects all of them into one network. Each candidate link's weight
 * is its backhaul cost (fiber trenching distance, microwave-link equipment cost, lease terms —
 * whatever dominates for that pair), and Kruskal's algorithm finds the minimum-cost set without
 * having to evaluate every possible network topology by brute force.
 */
public final class CellTowerBackhaulPlanner {

    private final Set<String> towers = new HashSet<>();
    private final List<WeightedEdge<String>> candidateLinks = new ArrayList<>();

    /** Registers a tower with no candidate links yet (e.g. a site awaiting a backhaul survey). */
    public void registerTower(String towerId) {
        towers.add(towerId);
    }

    /** Registers a possible backhaul link between two towers and its build/lease cost. */
    public void registerCandidateLink(String towerA, String towerB, long backhaulCost) {
        towers.add(towerA);
        towers.add(towerB);
        candidateLinks.add(new WeightedEdge<>(towerA, towerB, backhaulCost));
    }

    /** The cheapest set of links that connects every registered tower into one network. */
    public MinimumSpanningTreeResult<String> planCheapestBackhaul() {
        return new KruskalMinimumSpanningTree<String>().computeMst(towers, candidateLinks);
    }
}
