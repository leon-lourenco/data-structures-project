package com.datastructures.graphs.minimumspanningtree.applied;

import com.datastructures.graphs.minimumspanningtree.classic.MinimumSpanningTreeResult;
import com.datastructures.graphs.minimumspanningtree.classic.WeightedEdge;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CellTowerBackhaulPlannerTest {

    @Test
    void picksTheCheapestSetOfLinksThatConnectsEveryTower() {
        CellTowerBackhaulPlanner planner = new CellTowerBackhaulPlanner();
        planner.registerCandidateLink("tower-north", "tower-central", 400);
        planner.registerCandidateLink("tower-central", "tower-south", 250);
        planner.registerCandidateLink("tower-north", "tower-south", 900); // pricier redundant link

        MinimumSpanningTreeResult<String> plan = planner.planCheapestBackhaul();

        assertThat(plan.edges()).containsExactlyInAnyOrder(
                new WeightedEdge<>("tower-north", "tower-central", 400),
                new WeightedEdge<>("tower-central", "tower-south", 250));
        assertThat(plan.totalWeight()).isEqualTo(650);
        assertThat(plan.spansAllNodes()).isTrue();
    }

    @Test
    void aTowerAwaitingASurveyWithNoCandidateLinksLeavesTheNetworkUnspanned() {
        CellTowerBackhaulPlanner planner = new CellTowerBackhaulPlanner();
        planner.registerCandidateLink("tower-north", "tower-central", 400);
        planner.registerTower("tower-remote"); // no backhaul survey done yet: no candidate links

        MinimumSpanningTreeResult<String> plan = planner.planCheapestBackhaul();

        assertThat(plan.spansAllNodes()).isFalse();
    }
}
