package com.datastructures.linear.queuedeque.applied;

import org.junit.jupiter.api.Test;

import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SupportTicketQueueTest {

    @Test
    void startsEmpty() {
        SupportTicketQueue queue = new SupportTicketQueue();

        assertThat(queue.isEmpty()).isTrue();
        assertThat(queue.pendingCount()).isZero();
    }

    @Test
    void normalTicketsAreServedFirstInFirstOut() {
        SupportTicketQueue queue = new SupportTicketQueue();
        queue.submit(new SupportTicket("cust-1", "no signal"));
        queue.submit(new SupportTicket("cust-2", "billing question"));

        assertThat(queue.nextTicket().customerId()).isEqualTo("cust-1");
        assertThat(queue.nextTicket().customerId()).isEqualTo("cust-2");
        assertThat(queue.isEmpty()).isTrue();
    }

    @Test
    void aVipTicketJumpsAheadOfAlreadyWaitingNormalTickets() {
        SupportTicketQueue queue = new SupportTicketQueue();
        queue.submit(new SupportTicket("cust-1", "no signal"));
        queue.submit(new SupportTicket("cust-2", "billing question"));

        queue.submitVip(new SupportTicket("vip-1", "outage escalation"));

        assertThat(queue.nextTicket().customerId()).isEqualTo("vip-1");
        assertThat(queue.nextTicket().customerId()).isEqualTo("cust-1");
        assertThat(queue.nextTicket().customerId()).isEqualTo("cust-2");
    }

    @Test
    void aSecondVipTicketJumpsAheadOfTheFirstVipTicket() {
        SupportTicketQueue queue = new SupportTicketQueue();
        queue.submit(new SupportTicket("cust-1", "no signal"));
        queue.submitVip(new SupportTicket("vip-1", "first escalation"));
        queue.submitVip(new SupportTicket("vip-2", "second escalation"));

        assertThat(queue.nextTicket().customerId()).isEqualTo("vip-2");
        assertThat(queue.nextTicket().customerId()).isEqualTo("vip-1");
        assertThat(queue.nextTicket().customerId()).isEqualTo("cust-1");
    }

    @Test
    void pendingCountTracksSubmittedAndHandledTickets() {
        SupportTicketQueue queue = new SupportTicketQueue();
        queue.submit(new SupportTicket("cust-1", "no signal"));
        queue.submitVip(new SupportTicket("vip-1", "outage"));

        assertThat(queue.pendingCount()).isEqualTo(2);

        queue.nextTicket();

        assertThat(queue.pendingCount()).isEqualTo(1);
    }

    @Test
    void pullingFromAnEmptyQueueThrows() {
        SupportTicketQueue queue = new SupportTicketQueue();

        assertThatThrownBy(queue::nextTicket).isInstanceOf(NoSuchElementException.class);
    }
}
