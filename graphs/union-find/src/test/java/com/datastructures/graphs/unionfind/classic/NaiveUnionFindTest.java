package com.datastructures.graphs.unionfind.classic;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class NaiveUnionFindTest {

    @Test
    void everyElementStartsInItsOwnSet() {
        NaiveUnionFind uf = new NaiveUnionFind(4);

        for (int i = 0; i < 4; i++) {
            assertThat(uf.find(i)).isEqualTo(i);
        }
        assertThat(uf.connected(0, 1)).isFalse();
        assertThat(uf.size()).isEqualTo(4);
    }

    @Test
    void unionMergesTwoSets() {
        NaiveUnionFind uf = new NaiveUnionFind(3);

        uf.union(0, 1);

        assertThat(uf.connected(0, 1)).isTrue();
        assertThat(uf.connected(0, 2)).isFalse();
    }

    @Test
    void unioningTwoElementsAlreadyInTheSameSetIsANoOp() {
        NaiveUnionFind uf = new NaiveUnionFind(2);
        uf.union(0, 1);

        uf.union(0, 1);

        assertThat(uf.connected(0, 1)).isTrue();
    }

    /**
     * Sequential unions (0-1, 1-2, 2-3, 3-4) build the naive structure's worst case: a straight
     * chain with no path compression to flatten it. find(0) has to walk every hop to the root.
     */
    @Test
    void findWalksTheFullChainAfterASequenceOfUnions() {
        NaiveUnionFind uf = new NaiveUnionFind(5);
        uf.union(0, 1);
        uf.union(1, 2);
        uf.union(2, 3);
        uf.union(3, 4);

        assertThat(uf.find(0)).isEqualTo(uf.find(4));
        assertThat(uf.connected(0, 4)).isTrue();
    }
}
