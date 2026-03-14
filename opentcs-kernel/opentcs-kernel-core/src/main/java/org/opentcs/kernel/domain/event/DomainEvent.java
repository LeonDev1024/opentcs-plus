package org.opentcs.kernel.domain.event;

import java.time.Instant;

/**
 * 领域事件基类
 */
public abstract class DomainEvent {

    private final String eventId;
    private final Instant timestamp;
    private final String aggregateId;

    protected DomainEvent(String aggregateId) {
        this.eventId = java.util.UUID.randomUUID().toString();
        this.timestamp = Instant.now();
        this.aggregateId = aggregateId;
    }

    public String getEventId() {
        return eventId;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public String getAggregateId() {
        return aggregateId;
    }
}
