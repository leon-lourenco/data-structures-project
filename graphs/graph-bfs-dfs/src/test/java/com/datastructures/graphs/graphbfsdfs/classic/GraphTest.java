package com.datastructures.graphs.graphbfsdfs.classic;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class GraphTest {

    @Test
    void startsEmpty() {
        Graph<String> graph = new Graph<>();

        assertThat(graph.isEmpty()).isTrue();
        assertThat(graph.size()).isZero();
    }

    @Test
    void addVertexAddsAnIsolatedVertexWithNoEdges() {
        Graph<String> graph = new Graph<>();

        graph.addVertex("A");

        assertThat(graph.isEmpty()).isFalse();
        assertThat(graph.size()).isEqualTo(1);
        assertThat(graph.bfs("A")).containsExactly("A");
        assertThat(graph.dfs("A")).containsExactly("A");
    }

    @Test
    void addingAVertexTwiceIsANoOpAndDoesNotResetItsEdges() {
        Graph<String> graph = new Graph<>();
        graph.addEdge("A", "B");

        graph.addVertex("A");

        assertThat(graph.size()).isEqualTo(2);
        assertThat(graph.bfs("A")).containsExactlyInAnyOrder("A", "B");
    }

    @Test
    void addEdgeLinksBothVerticesInBothDirections() {
        Graph<String> graph = new Graph<>();

        graph.addEdge("A", "B");

        assertThat(graph.size()).isEqualTo(2);
        assertThat(graph.bfs("A")).containsExactly("A", "B");
        assertThat(graph.bfs("B")).containsExactly("B", "A");
    }

    @Test
    void bfsOnAnUnknownStartVertexThrows() {
        Graph<String> graph = new Graph<>();
        graph.addEdge("A", "B");

        assertThatThrownBy(() -> graph.bfs("Z"))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessageContaining("Z");
    }

    @Test
    void dfsOnAnUnknownStartVertexThrows() {
        Graph<String> graph = new Graph<>();
        graph.addEdge("A", "B");

        assertThatThrownBy(() -> graph.dfs("Z"))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessageContaining("Z");
    }

    /**
     * A       B
     * |\     /|
     * | \   / |
     * |  \ /  |
     * |   X   |
     * |  / \  |
     * | /   \ |
     * C ----- D    E - F (disconnected component)
     *
     * Built with a cycle (A-B-C-D-A plus both diagonals) so that both bfs and dfs are forced to
     * discard an already-visited neighbor at least once - the exact branch that only fires when
     * a vertex has more than one path leading back to it.
     */
    private Graph<String> cyclicGraphWithDisconnectedComponent() {
        Graph<String> graph = new Graph<>();
        graph.addEdge("A", "B");
        graph.addEdge("A", "C");
        graph.addEdge("A", "D");
        graph.addEdge("B", "C");
        graph.addEdge("B", "D");
        graph.addEdge("C", "D");
        graph.addEdge("E", "F");
        return graph;
    }

    @Test
    void bfsVisitsEveryVertexInTheConnectedComponentExactlyOnceInLayerOrder() {
        Graph<String> graph = cyclicGraphWithDisconnectedComponent();

        List<String> visitOrder = graph.bfs("A");

        assertThat(visitOrder).containsExactly("A", "B", "C", "D");
    }

    @Test
    void bfsNeverReachesADisconnectedComponent() {
        Graph<String> graph = cyclicGraphWithDisconnectedComponent();

        List<String> visitOrder = graph.bfs("A");

        assertThat(visitOrder).doesNotContain("E", "F");
    }

    @Test
    void dfsVisitsEveryVertexInTheConnectedComponentExactlyOnceInDepthFirstOrder() {
        Graph<String> graph = cyclicGraphWithDisconnectedComponent();

        List<String> visitOrder = graph.dfs("A");

        assertThat(visitOrder).containsExactly("A", "B", "C", "D");
    }

    @Test
    void dfsNeverReachesADisconnectedComponent() {
        Graph<String> graph = cyclicGraphWithDisconnectedComponent();

        List<String> visitOrder = graph.dfs("A");

        assertThat(visitOrder).doesNotContain("E", "F");
    }

    @Test
    void bfsFromTheOtherDisconnectedComponentOnlyReachesItsOwnVertices() {
        Graph<String> graph = cyclicGraphWithDisconnectedComponent();

        assertThat(graph.bfs("E")).containsExactly("E", "F");
    }

    @Test
    void dfsOnALinearChainVisitsInDepthFirstOrderNotBreadthFirstOrder() {
        Graph<String> graph = new Graph<>();
        graph.addEdge("A", "B");
        graph.addEdge("A", "C");
        graph.addEdge("B", "D");

        List<String> visitOrder = graph.dfs("A");

        // A recursive DFS from A would go A -> B -> D (dead end, backtrack) -> C.
        assertThat(visitOrder).containsExactly("A", "B", "D", "C");
    }
}
