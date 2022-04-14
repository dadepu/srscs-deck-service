package de.danielkoellgen.srscsdeckservice.events.consumer;

import de.danielkoellgen.srscsdeckservice.domain.domainprimitive.EventDateTime;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;
import java.util.UUID;

abstract public class AbstractConsumerEvent implements ConsumerEvent {

    private final @NotNull UUID eventId;

    private final @NotNull UUID transactionId;

    private final @NotNull String eventName;

    private final @NotNull Integer version;

    private final @NotNull EventDateTime occurredAt;

    private final @NotNull EventDateTime receivedAt;

    private final @NotNull String topic;

    public AbstractConsumerEvent(@NotNull UUID eventId, @NotNull UUID transactionId, @NotNull String eventName,
            @NotNull Integer version, @NotNull EventDateTime occurredAt, @NotNull String topic) {
        this.eventId = eventId;
        this.transactionId = transactionId;
        this.eventName = eventName;
        this.version = version;
        this.occurredAt = occurredAt;
        this.receivedAt = new EventDateTime(LocalDateTime.now());
        this.topic = topic;
    }

    @Override
    public @NotNull UUID getEventId() {
        return eventId;
    }

    @Override
    public @NotNull UUID getTransactionId() {
        return transactionId;
    }

    @Override
    public @NotNull String getEventName() {
        return eventName;
    }

    @Override
    public @NotNull Integer getVersion() {
        return version;
    }

    @Override
    public @NotNull EventDateTime getOccurredAt() {
        return occurredAt;
    }

    @Override
    public @NotNull EventDateTime getReceivedAt() {
        return receivedAt;
    }

    @Override
    public @NotNull String getTopic() {
        return topic;
    }
}
