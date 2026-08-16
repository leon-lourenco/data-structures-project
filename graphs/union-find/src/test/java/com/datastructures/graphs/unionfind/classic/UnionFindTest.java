package com.datastructures.graphs.unionfind.classic;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class UnionFindTest {

    @Test
    void everyElementStartsInItsOwnSet() {
        UnionFind uf = new UnionFind(4);

        for (int i = 0; i < 4; i++) {
            assertThat(uf.find(i)).isEqualTo(i);
        }
        assertThat(uf.connected(0, 1)).isFalse();
        assertThat(uf.size()).isEqualTo(4);
    }

    /**
     * Exercises all three branches of union-by-rank in sequence:
     * <ol>
     *   <li>union(0,1), union(2,3): equal ranks (0 vs 0), so the second argument's root wins
     *       arbitrarily and its rank increments.</li>
     *   <li>union(4,0): rank[4]=0 &lt; rank[0]=1, so the lower-rank root (4) attaches under 0.</li>
     *   <li>union(0,2): rank[0]=1 == rank[2]=1, equal-rank case again, rank[0] becomes 2.</li>
     *   <li>union(0,5): rank[0]=2 &gt; rank[5]=0, so the lower-rank root (5) attaches under 0.</li>
     *   <li>union(1,3): 1 and 3 are already in the same set by this point (both under root 0),
     *       so this is the no-op branch.</li>
     * </ol>
     */
    @Test
    void unionByRankAttachesTheLowerRankTreeUnderTheHigherRankRootInEveryCase() {
        UnionFind uf = new UnionFind(7);

        uf.union(0, 1); // equal ranks (0,0) -> parent[1]=0, rank[0]=1
        uf.union(2, 3); // equal ranks (0,0) -> parent[3]=2, rank[2]=1
        uf.union(4, 0); // rank[4]=0 < rank[0]=1 -> parent[4]=0
        uf.union(0, 2); // rank[0]=1 == rank[2]=1 -> parent[2]=0, rank[0]=2
        uf.union(0, 5); // rank[0]=2 > rank[5]=0 -> parent[5]=0
        uf.union(1, 3); // 1 and 3 already share root 0 -> no-op

        assertThat(uf.connected(4, 5)).isTrue();
        assertThat(uf.connected(1, 3)).isTrue();
        assertThat(uf.connected(0, 6)).isFalse();
    }

    /**
     * Builds a tree three levels deep (3 -> 2 -> 0) so find(3) has to both walk a multi-hop
     * path to the root and then compress it, while find(0) (already the root) exercises the
     * zero-iteration path for both of find's internal loops.
     */
    @Test
    void findCompressesAMultiHopPathAndLeavesAnAlreadyDirectPathUntouched() {
        UnionFind uf = new UnionFind(4);
        uf.union(0, 1); // parent[1]=0, rank[0]=1
        uf.union(2, 3); // parent[3]=2, rank[2]=1
        uf.union(0, 2); // equal ranks -> parent[2]=0, rank[0]=2; now 3 -> 2 -> 0 (depth 2)

        assertThat(uf.find(3)).isEqualTo(0);
        assertThat(uf.find(0)).isEqualTo(0);
        assertThat(uf.connected(1, 3)).isTrue();
    }
}
