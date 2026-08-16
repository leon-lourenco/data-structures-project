package com.datastructures.linear.dynamicarray.applied;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class BatchRecordBufferTest {

    @Test
    void drainInChunksOfSplitsRecordsIntoFixedSizeChunksWithASmallerLastChunk() {
        BatchRecordBuffer buffer = new BatchRecordBuffer();
        for (int i = 0; i < 25; i++) {
            buffer.ingest(record("policy-" + i));
        }

        List<List<PolicyBatchRecord>> chunks = new ArrayList<>();
        buffer.drainInChunksOf(10, chunks::add);

        assertThat(chunks).hasSize(3);
        assertThat(chunks.get(0)).hasSize(10);
        assertThat(chunks.get(1)).hasSize(10);
        assertThat(chunks.get(2)).hasSize(5);
        assertThat(chunks.get(0).get(0).policyId()).isEqualTo("policy-0");
        assertThat(chunks.get(2).get(4).policyId()).isEqualTo("policy-24");
    }

    @Test
    void drainInChunksOfProducesExactlyOneFullChunkWhenSizeDividesEvenly() {
        BatchRecordBuffer buffer = new BatchRecordBuffer();
        buffer.ingest(record("policy-a"));
        buffer.ingest(record("policy-b"));

        List<List<PolicyBatchRecord>> chunks = new ArrayList<>();
        buffer.drainInChunksOf(2, chunks::add);

        assertThat(chunks).hasSize(1);
        assertThat(chunks.get(0)).hasSize(2);
    }

    @Test
    void drainClearsTheBufferAfterwards() {
        BatchRecordBuffer buffer = new BatchRecordBuffer();
        buffer.ingest(record("policy-a"));
        buffer.ingest(record("policy-b"));

        buffer.drainInChunksOf(10, chunk -> { });

        assertThat(buffer.size()).isZero();
    }

    @Test
    void drainOnAnEmptyBufferInvokesNoChunks() {
        BatchRecordBuffer buffer = new BatchRecordBuffer();

        List<List<PolicyBatchRecord>> chunks = new ArrayList<>();
        buffer.drainInChunksOf(10, chunks::add);

        assertThat(chunks).isEmpty();
    }

    @Test
    void drainInChunksOfRejectsANonPositiveChunkSize() {
        BatchRecordBuffer buffer = new BatchRecordBuffer();

        assertThatThrownBy(() -> buffer.drainInChunksOf(0, chunk -> { }))
                .isInstanceOf(IllegalArgumentException.class);
    }

    private static PolicyBatchRecord record(String policyId) {
        return new PolicyBatchRecord(policyId, BigDecimal.valueOf(199.90), Instant.now());
    }
}
