package com.datastructures.hashing.hashtable.applied;

import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;

import static org.assertj.core.api.Assertions.assertThat;

class IdempotencyKeyCacheTest {

    @Test
    void aKeySeenForTheFirstTimeIsNotADuplicate() {
        IdempotencyKeyCache cache = new IdempotencyKeyCache(Clock.systemUTC());

        assertThat(cache.isDuplicate("tx-1")).isFalse();
    }

    @Test
    void markingAKeyProcessedMakesTheNextCheckReportADuplicate() {
        IdempotencyKeyCache cache = new IdempotencyKeyCache(Clock.systemUTC());

        cache.markProcessed("tx-1");

        assertThat(cache.isDuplicate("tx-1")).isTrue();
    }

    @Test
    void differentKeysAreTrackedIndependently() {
        IdempotencyKeyCache cache = new IdempotencyKeyCache(Clock.systemUTC());

        cache.markProcessed("tx-1");

        assertThat(cache.isDuplicate("tx-1")).isTrue();
        assertThat(cache.isDuplicate("tx-2")).isFalse();
        assertThat(cache.size()).isEqualTo(1);
    }

    @Test
    void reMarkingTheSameKeyDoesNotGrowTheCache() {
        IdempotencyKeyCache cache = new IdempotencyKeyCache(Clock.systemUTC());

        cache.markProcessed("tx-1");
        cache.markProcessed("tx-1");

        assertThat(cache.size()).isEqualTo(1);
    }

    @Test
    void evictOlderThanRemovesOnlyExpiredEntries() {
        Instant now = Instant.parse("2026-08-16T12:00:00Z");
        MutableClock clock = new MutableClock(now);
        IdempotencyKeyCache cache = new IdempotencyKeyCache(clock);

        cache.markProcessed("old-tx");
        clock.advance(Duration.ofMinutes(10));
        cache.markProcessed("recent-tx");

        cache.evictOlderThan(now.plus(Duration.ofMinutes(5)));

        assertThat(cache.isDuplicate("old-tx")).isFalse();
        assertThat(cache.isDuplicate("recent-tx")).isTrue();
        assertThat(cache.size()).isEqualTo(1);
    }

    /** A JDK {@link Clock} whose {@code instant()} can be moved forward on demand for tests. */
    private static final class MutableClock extends Clock {
        private Instant current;

        MutableClock(Instant current) {
            this.current = current;
        }

        void advance(Duration duration) {
            current = current.plus(duration);
        }

        @Override
        public ZoneOffset getZone() {
            return ZoneOffset.UTC;
        }

        @Override
        public Clock withZone(java.time.ZoneId zone) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Instant instant() {
            return current;
        }
    }
}
