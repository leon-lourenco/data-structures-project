package com.datastructures.trees.heap.applied;

import com.datastructures.trees.heap.classic.MinHeap;

/**
 * Orders support tickets by remaining SLA time so the ticket closest to breaching its SLA is
 * always the next one escalated to an agent — this is exactly a min-heap's job: the ticket
 * with the least remaining time is the "minimum" by {@link SlaTicket}'s natural ordering, and
 * it's always O(log n) to add a newly-arrived ticket or to pull the next one to escalate,
 * regardless of how many tickets are queued.
 */
public final class SlaEscalationQueue {

    private final MinHeap<SlaTicket> tickets = new MinHeap<>();

    public void submit(SlaTicket ticket) {
        tickets.offer(ticket);
    }

    /** Removes and returns the ticket with the least remaining SLA time. */
    public SlaTicket nextToEscalate() {
        return tickets.poll();
    }

    /** The most urgent ticket, without removing it from the queue. */
    public SlaTicket peekNext() {
        return tickets.peek();
    }

    public boolean isEmpty() {
        return tickets.isEmpty();
    }

    public int size() {
        return tickets.size();
    }
}
