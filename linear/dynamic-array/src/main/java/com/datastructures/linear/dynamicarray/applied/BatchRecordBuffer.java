package com.datastructures.linear.dynamicarray.applied;

import com.datastructures.linear.dynamicarray.classic.DynamicArray;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * In-memory staging buffer for a batch-ingestion pipeline: an extraction job keeps calling
 * {@link #ingest(PolicyBatchRecord)} as rows arrive off a source file, and a coordinator later
 * calls {@link #drainInChunksOf(int, Consumer)} to hand fixed-size chunks to parallel workers.
 *
 * <p>A dynamic array is the right backing structure here, not a linked list: ingestion is
 * pure append (amortized O(1) either way), but draining walks the whole buffer once per chunk
 * and benefits from the array's contiguous, cache-friendly layout — exactly the kind of
 * bulk-scan-then-discard shape a 3M+ rows/day batch stage runs into.
 */
public final class BatchRecordBuffer {

    private final DynamicArray<PolicyBatchRecord> buffer = new DynamicArray<>();

    public void ingest(PolicyBatchRecord record) {
        buffer.add(record);
    }

    public int size() {
        return buffer.size();
    }

    /**
     * Hands every buffered record to {@code chunkConsumer} in fixed-size chunks (the last
     * chunk may be smaller), then clears the buffer. Chunking this way lets a coordinator
     * dispatch each chunk to a different worker thread/task instead of processing 3M+ rows
     * serially.
     */
    public void drainInChunksOf(int chunkSize, Consumer<List<PolicyBatchRecord>> chunkConsumer) {
        if (chunkSize < 1) {
            throw new IllegalArgumentException("chunkSize must be >= 1");
        }
        List<PolicyBatchRecord> chunk = new ArrayList<>(chunkSize);
        for (PolicyBatchRecord record : buffer) {
            chunk.add(record);
            if (chunk.size() == chunkSize) {
                chunkConsumer.accept(chunk);
                chunk = new ArrayList<>(chunkSize);
            }
        }
        if (!chunk.isEmpty()) {
            chunkConsumer.accept(chunk);
        }
        clear();
    }

    private void clear() {
        while (buffer.size() > 0) {
            buffer.remove(buffer.size() - 1);
        }
    }
}
