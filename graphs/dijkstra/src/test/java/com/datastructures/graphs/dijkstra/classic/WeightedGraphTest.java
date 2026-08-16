package com.datastructures.graphs.dijkstra.classic;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class WeightedGraphTest {

    @Test
    void singleNodeGraphReachesOnlyItself() {
        WeightedGraph<String> graph = new WeightedGraph<>();
        graph.addNode("A");

        Map<String, Long> distances = graph.shortestPathFrom("A");

        assertThat(distances).containsExactly(Map.entry("A", 0L));
        assertThat(graph.nodeCount()).isEqualTo(1);
    }

    @Test
    void addEdgeRejectsANegativeWeight() {
        WeightedGraph<String> graph = new WeightedGraph<>();

        assertThatThrownBy(() -> graph.addEdge("A", "B", -1))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("-1");
    }

    @Test
    void aNodeWithNoPathToTheSourceIsAbsentFromTheResult() {
        WeightedGraph<String> graph = new WeightedGraph<>();
        graph.addEdge("A", "B", 1);
        graph.addNode("Z"); // isolated: no edge connects it to anything

        Map<String, Long> distances = graph.shortestPathFrom("A");

        assertThat(distances).containsOnlyKeys("A", "B");
        assertThat(distances).doesNotContainKey("Z");
    }

    /**
     * A graph shaped so Dijkstra must relax B's and D's tentative distance downward more than
     * once (forcing multiple, decreasing queue entries for the same node — i.e. stale entries
     * that get skipped once the node is already settled) before settling on the true shortest
     * distances.
     */
    @Test
    void dijkstraFindsTheTrueShortestDistancesThroughRelaxationAndSkipsStaleQueueEntries() {
        WeightedGraph<String> graph = new WeightedGraph<>();
        graph.addEdge("A", "B", 1);
        graph.addEdge("A", "C", 4);
        graph.addEdge("B", "C", 1);
        graph.addEdge("C", "D", 1);
        graph.addEdge("B", "D", 5);
        graph.addEdge("A", "D", 10);

        Map<String, Long> distances = graph.shortestPathFrom("A");

        assertThat(distances)
                .containsEntry("A", 0L)
                .containsEntry("B", 1L)
                .containsEntry("C", 2L)
                .containsEntry("D", 3L);
    }

    /**
     * B is settled before C (distance 1 < 2), and B has a very expensive edge to C. That
     * relaxation attempt must be rejected because it's worse than C's already-known distance —
     * the "not an improvement" branch of the relaxation check.
     */
    @Test
    void aWorseAlternatePathDoesNotOverwriteAnAlreadyBetterKnownDistance() {
        WeightedGraph<String> graph = new WeightedGraph<>();
        graph.addEdge("A", "B", 1);
        graph.addEdge("A", "C", 2);
        graph.addEdge("B", "C", 100);

        Map<String, Long> distances = graph.shortestPathFrom("A");

        assertThat(distances).containsEntry("C", 2L);
    }
}
