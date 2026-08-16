package com.datastructures.trees.heap.applied;

import org.junit.jupiter.api.Test;

import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SlaEscalationQueueTest {

    @Test
    void startsEmpty() {
        SlaEscalationQueue queue = new SlaEscalationQueue();

        assertThat(queue.isEmpty()).isTrue();
        assertThat(queue.size()).isZero();
    }

    @Test
    void ticketsAreEscalatedInAscendingRemainingSlaOrderRegardlessOfSubmissionOrder() {
        SlaEscalationQueue queue = new SlaEscalationQueue();
        queue.submit(new SlaTicket("TCK-1", 60_000L));
        queue.submit(new SlaTicket("TCK-2", 5_000L));
        queue.submit(new SlaTicket("TCK-3", 30_000L));

        assertThat(queue.nextToEscalate().ticketId()).isEqualTo("TCK-2");
        assertThat(queue.nextToEscalate().ticketId()).isEqualTo("TCK-3");
        assertThat(queue.nextToEscalate().ticketId()).isEqualTo("TCK-1");
        assertThat(queue.isEmpty()).isTrue();
    }

    @Test
    void peekNextReturnsTheMostUrgentTicketWithoutRemovingIt() {
        SlaEscalationQueue queue = new SlaEscalationQueue();
        queue.submit(new SlaTicket("TCK-1", 60_000L));
        queue.submit(new SlaTicket("TCK-2", 5_000L));

        assertThat(queue.peekNext().ticketId()).isEqualTo("TCK-2");
        assertThat(queue.size()).isEqualTo(2);
    }

    @Test
    void escalatingFromAnEmptyQueueThrows() {
        SlaEscalationQueue queue = new SlaEscalationQueue();

        assertThatThrownBy(queue::nextToEscalate).isInstanceOf(NoSuchElementException.class);
    }
}
