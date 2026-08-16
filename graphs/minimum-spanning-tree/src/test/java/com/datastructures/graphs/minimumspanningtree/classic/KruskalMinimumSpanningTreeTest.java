package com.datastructures.graphs.minimumspanningtree.classic;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class KruskalMinimumSpanningTreeTest {

    private final KruskalMinimumSpanningTree<String> kruskal = new KruskalMinimumSpanningTree<>();

    @Test
    void anEmptyGraphProducesAnEmptyResultThatDoesNotClaimToSpanAnything() {
        MinimumSpanningTreeResult<String> result = kruskal.computeMst(Set.of(), List.of());

        assertThat(result.edges()).isEmpty();
        assertThat(result.totalWeight()).isZero();
        assertThat(result.spansAllNodes()).isFalse();
    }

    @Test
    void aSingleNodeWithNoEdgesTriviallySpansItself() {
        MinimumSpanningTreeResult<String> result = kruskal.computeMst(Set.of("A"), List.of());

        assertThat(result.edges()).isEmpty();
        assertThat(result.totalWeight()).isZero();
        assertThat(result.spansAllNodes()).isTrue();
    }

    @Test
    void kruskalSkipsAnEdgeThatWouldCreateACycleAndKeepsTheCheaperOnes() {
        Set<String> nodes = Set.of("A", "B", "C");
        List<WeightedEdge<String>> edges = List.of(
                new WeightedEdge<>("A", "B", 1),
                new WeightedEdge<>("B", "C", 2),
                new WeightedEdge<>("A", "C", 3) // would close a cycle once A-B and B-C are in
        );

        MinimumSpanningTreeResult<String> result = kruskal.computeMst(nodes, edges);

        assertThat(result.edges()).containsExactly(
                new WeightedEdge<>("A", "B", 1),
                new WeightedEdge<>("B", "C", 2));
        assertThat(result.totalWeight()).isEqualTo(3);
        assertThat(result.spansAllNodes()).isTrue();
    }

    @Test
    void aDisconnectedInputGraphProducesTheCheapestForestInsteadOfATree() {
        Set<String> nodes = Set.of("A", "B", "C", "D");
        List<WeightedEdge<String>> edges = List.of(
                new WeightedEdge<>("A", "B", 5),
                new WeightedEdge<>("C", "D", 7)
                // no edge connects {A,B} to {C,D}: the input graph itself is disconnected
        );

        MinimumSpanningTreeResult<String> result = kruskal.computeMst(nodes, edges);

        assertThat(result.edges()).hasSize(2);
        assertThat(result.totalWeight()).isEqualTo(12);
        assertThat(result.spansAllNodes()).isFalse();
    }
}
