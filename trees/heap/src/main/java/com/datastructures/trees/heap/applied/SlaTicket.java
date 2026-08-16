package com.datastructures.trees.heap.applied;

/** A support ticket with a remaining SLA budget; the smallest remaining time is the most urgent. */
public record SlaTicket(String ticketId, long remainingSlaMillis) implements Comparable<SlaTicket> {

    @Override
    public int compareTo(SlaTicket other) {
        return Long.compare(remainingSlaMillis, other.remainingSlaMillis);
    }
}
