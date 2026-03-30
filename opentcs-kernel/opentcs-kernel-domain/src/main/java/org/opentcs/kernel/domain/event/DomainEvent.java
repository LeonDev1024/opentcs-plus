package org.opentcs.kernel.domain.event;

import lombok.Getter;

import java.time.Instant;

/**
 * 领域事件基类
 */
@Getter
public abstract class DomainEvent {

    private final String eventId;
    private final Instant timestamp;
    private final String aggregateId;

    protected DomainEvent(String aggregateId) {
        this.eventId = java.util.UUID.randomUUID().toString();
        this.timestamp = Instant.now();
        this.aggregateId = aggregateId;
    }

}
