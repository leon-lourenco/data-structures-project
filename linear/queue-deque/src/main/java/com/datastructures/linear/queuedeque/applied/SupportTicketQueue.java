package com.datastructures.linear.queuedeque.applied;

import com.datastructures.linear.queuedeque.classic.ArrayDeque;

/**
 * telecom customer support ticket triage queue: a normal ticket joins the back of the line
 * ({@link #submit}, {@code addLast} — FIFO), but a VIP ticket jumps straight to the front
 * ({@link #submitVip}, {@code addFirst}), so the next agent pull ({@link #nextTicket},
 * {@code removeFirst}) always serves whichever VIP is waiting before any normal ticket that
 * arrived earlier. Both operations are O(1) — a VIP escalation never has to shift or rescan the
 * rest of the queue, it just becomes the new front.
 */
public final class SupportTicketQueue {

    private final ArrayDeque<SupportTicket> tickets = new ArrayDeque<>();

    /** A normal ticket: joins the back of the line. */
    public void submit(SupportTicket ticket) {
        tickets.addLast(ticket);
    }

    /** A VIP ticket: jumps straight to the front of the line. */
    public void submitVip(SupportTicket ticket) {
        tickets.addFirst(ticket);
    }

    /** The next ticket for an agent to handle, removed from the front of the line. */
    public SupportTicket nextTicket() {
        return tickets.removeFirst();
    }

    public int pendingCount() {
        return tickets.size();
    }

    public boolean isEmpty() {
        return tickets.isEmpty();
    }
}
