package de.danielkoellgen.srscsdeckservice.events.consumer;

import de.danielkoellgen.srscsdeckservice.domain.domainprimitive.EventDateTime;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public interface ConsumerEvent {

    @NotNull UUID getEventId();

    @NotNull UUID getTransactionId();

    @NotNull String getEventName();

    @NotNull EventDateTime getOccurredAt();

    @NotNull EventDateTime getReceivedAt();

    @NotNull String getTopic();
}
