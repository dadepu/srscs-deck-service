package de.danielkoellgen.srscsdeckservice.events.consumer;

import de.danielkoellgen.srscsdeckservice.domain.domainprimitive.EventDateTime;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public interface ConsumerEvent {

    @NotNull UUID getEventId();

    @NotNull String getTransactionId();

    @Nullable UUID getCorrelationId();

    @NotNull String getEventName();

    @NotNull EventDateTime getOccurredAt();

    @NotNull EventDateTime getReceivedAt();

    @NotNull String getTopic();

    @NotNull String getSerializedContent();
}
