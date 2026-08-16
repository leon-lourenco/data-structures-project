package com.datastructures.graphs.graphbfsdfs.applied;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class AmlNetworkTraversalTest {

    @Test
    void flaggedAccountReachesEveryAccountInItsTransactionCluster() {
        AmlNetworkTraversal aml = new AmlNetworkTraversal();
        // ACC-001 (flagged) -> ACC-002 -> ACC-003, plus a direct ACC-001 -> ACC-003 shortcut.
        aml.recordTransaction("ACC-001", "ACC-002");
        aml.recordTransaction("ACC-002", "ACC-003");
        aml.recordTransaction("ACC-001", "ACC-003");

        List<String> reachable = aml.accountsReachableFrom("ACC-001");

        assertThat(reachable).containsExactlyInAnyOrder("ACC-001", "ACC-002", "ACC-003");
    }

    @Test
    void closestAccountsComeFirstInTheReachableOrder() {
        AmlNetworkTraversal aml = new AmlNetworkTraversal();
        aml.recordTransaction("ACC-001", "ACC-002");
        aml.recordTransaction("ACC-002", "ACC-003");

        List<String> reachable = aml.accountsReachableFrom("ACC-001");

        // ACC-002 is one hop away, ACC-003 is two - BFS must surface the closer one first.
        assertThat(reachable).containsExactly("ACC-001", "ACC-002", "ACC-003");
    }

    @Test
    void accountsOutsideTheFlaggedNetworkAreNeverIncluded() {
        AmlNetworkTraversal aml = new AmlNetworkTraversal();
        aml.recordTransaction("ACC-001", "ACC-002");
        // Unrelated pair of accounts transacting only with each other.
        aml.recordTransaction("ACC-100", "ACC-101");

        List<String> reachable = aml.accountsReachableFrom("ACC-001");

        assertThat(reachable).doesNotContain("ACC-100", "ACC-101");
    }
}
