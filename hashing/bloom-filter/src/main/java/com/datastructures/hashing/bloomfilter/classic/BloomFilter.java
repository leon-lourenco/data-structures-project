package com.datastructures.hashing.bloomfilter.classic;

import java.util.Objects;

/**
 * A probabilistic set membership filter built from scratch on top of a {@code long[]} used as a
 * bitset — no external Bloom filter library. Adding an item sets {@code k} bits derived from its
 * hash; checking membership only ever reads those same {@code k} bits. That asymmetry is the
 * whole contract: a bit can get set by some *other* item that happens to hash to the same
 * position, so {@link #mightContain} can report a false positive, but it can never report a
 * false negative — every bit an {@link #add}ed item needs is guaranteed to already be set.
 *
 * <p>The {@code k} hash functions are derived from just two independent base hashes via double
 * hashing ({@code h_i(x) = h1(x) + i * h2(x)}, the standard Kirsch-Mitzenmacher construction),
 * rather than computing {@code k} genuinely different hash functions: {@code h1} is {@code
 * hashCode() ^ (h >>> 16)} (the same spread this repo's Hash Table module uses), and {@code h2}
 * is a second spread of {@code hashCode()} mixed with a different odd multiplier, so the two are
 * independent enough in practice without needing a real second hash algorithm.
 *
 * <p>Bit-array size {@code m} and hash count {@code k} are computed from the standard formulas
 * given an expected insertion count {@code n} and a target false-positive rate {@code p}:
 * {@code m = -(n * ln(p)) / (ln(2))^2} and {@code k = (m / n) * ln(2)}.
 */
public final class BloomFilter<T> {

    private static final int ODD_MIXING_MULTIPLIER = 0x9E3779B1; // 2^32 / golden ratio, odd — good bit mixing.

    private final long[] bits;
    private final int bitCount;
    private final int hashCount;

    public BloomFilter(int expectedInsertions, double falsePositiveRate) {
        if (expectedInsertions < 1) {
            throw new IllegalArgumentException("expectedInsertions must be >= 1");
        }
        if (falsePositiveRate <= 0.0 || falsePositiveRate >= 1.0) {
            throw new IllegalArgumentException("falsePositiveRate must be strictly between 0 and 1");
        }
        this.bitCount = optimalBitCount(expectedInsertions, falsePositiveRate);
        this.hashCount = optimalHashCount(bitCount, expectedInsertions);
        this.bits = new long[(bitCount + Long.SIZE - 1) / Long.SIZE];
    }

    public int bitCount() {
        return bitCount;
    }

    public int hashCount() {
        return hashCount;
    }

    /** Sets the {@code k} bits derived from {@code item}'s hash. */
    public void add(T item) {
        Objects.requireNonNull(item, "item must not be null");
        long h1 = spreadPrimary(item);
        long h2 = spreadSecondary(item);
        for (int i = 0; i < hashCount; i++) {
            setBit(bitIndexFor(h1, h2, i));
        }
    }

    /**
     * {@code false} is a guaranteed answer: {@code item} was never {@link #add}ed. {@code true}
     * means every one of its {@code k} bits happens to be set, which is guaranteed if it *was*
     * added, but can also happen by coincidence from other items — a false positive.
     */
    public boolean mightContain(T item) {
        Objects.requireNonNull(item, "item must not be null");
        long h1 = spreadPrimary(item);
        long h2 = spreadSecondary(item);
        for (int i = 0; i < hashCount; i++) {
            if (!getBit(bitIndexFor(h1, h2, i))) {
                return false;
            }
        }
        return true;
    }

    private int bitIndexFor(long h1, long h2, int probeIndex) {
        long combined = h1 + (long) probeIndex * h2;
        return (int) Math.floorMod(combined, (long) bitCount);
    }

    private void setBit(int index) {
        bits[index / Long.SIZE] |= (1L << (index % Long.SIZE));
    }

    private boolean getBit(int index) {
        return (bits[index / Long.SIZE] & (1L << (index % Long.SIZE))) != 0;
    }

    private long spreadPrimary(T item) {
        int h = item.hashCode();
        return h ^ (h >>> 16);
    }

    private long spreadSecondary(T item) {
        int h = item.hashCode() * ODD_MIXING_MULTIPLIER;
        return h ^ (h >>> 13);
    }

    private static int optimalBitCount(int expectedInsertions, double falsePositiveRate) {
        double m = -(expectedInsertions * Math.log(falsePositiveRate)) / (Math.log(2) * Math.log(2));
        return Math.max(1, (int) Math.ceil(m));
    }

    private static int optimalHashCount(int bitCount, int expectedInsertions) {
        double k = ((double) bitCount / expectedInsertions) * Math.log(2);
        return Math.max(1, (int) Math.round(k));
    }
}
