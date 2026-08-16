package com.datastructures.linear.skiplist.applied;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class RateLimitWindowTest {

    @Test
    void startsEmpty() {
        RateLimitWindow window = new RateLimitWindow();

        assertThat(window.size()).isZero();
        assertThat(window.requestCountAt(1_000L)).isZero();
    }

    @Test
    void recordingARequestTracksItsTimestamp() {
        RateLimitWindow window = new RateLimitWindow();

        window.recordRequest(1_000L);

        assertThat(window.requestCountAt(1_000L)).isEqualTo(1);
        assertThat(window.size()).isEqualTo(1);
    }

    @Test
    void recordingMultipleRequestsAtTheSameTimestampIncrementsItsCount() {
        RateLimitWindow window = new RateLimitWindow();

        window.recordRequest(1_000L);
        window.recordRequest(1_000L);
        window.recordRequest(1_000L);

        assertThat(window.requestCountAt(1_000L)).isEqualTo(3);
        assertThat(window.size()).isEqualTo(1);
    }

    @Test
    void differentTimestampsAreTrackedIndependently() {
        RateLimitWindow window = new RateLimitWindow();

        window.recordRequest(1_000L);
        window.recordRequest(2_000L);
        window.recordRequest(2_000L);

        assertThat(window.requestCountAt(1_000L)).isEqualTo(1);
        assertThat(window.requestCountAt(2_000L)).isEqualTo(2);
        assertThat(window.size()).isEqualTo(2);
    }

    @Test
    void evictOlderThanRemovesOnlyTimestampsBeforeTheCutoff() {
        RateLimitWindow window = new RateLimitWindow();
        window.recordRequest(1_000L);
        window.recordRequest(2_000L);
        window.recordRequest(3_000L);
        window.recordRequest(4_000L);

        window.evictOlderThan(3_000L);

        assertThat(window.requestCountAt(1_000L)).isZero();
        assertThat(window.requestCountAt(2_000L)).isZero();
        assertThat(window.requestCountAt(3_000L)).isEqualTo(1);
        assertThat(window.requestCountAt(4_000L)).isEqualTo(1);
        assertThat(window.size()).isEqualTo(2);
    }

    @Test
    void evictOlderThanOnAnEmptyWindowIsANoOp() {
        RateLimitWindow window = new RateLimitWindow();

        window.evictOlderThan(5_000L);

        assertThat(window.size()).isZero();
    }

    @Test
    void evictOlderThanWithACutoffBeforeEveryTimestampRemovesNothing() {
        RateLimitWindow window = new RateLimitWindow();
        window.recordRequest(1_000L);
        window.recordRequest(2_000L);

        window.evictOlderThan(500L);

        assertThat(window.size()).isEqualTo(2);
    }

    @Test
    void evictOlderThanCanDrainTheEntireWindow() {
        RateLimitWindow window = new RateLimitWindow();
        window.recordRequest(1_000L);
        window.recordRequest(2_000L);
        window.recordRequest(3_000L);

        window.evictOlderThan(10_000L);

        assertThat(window.size()).isZero();
    }
}
