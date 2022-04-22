package de.danielkoellgen.srscsdeckservice.events.producer;

import de.danielkoellgen.srscsdeckservice.domain.domainprimitive.EventDateTime;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public interface ProducerEvent {

    @NotNull UUID getEventId();

    @NotNull String getEventName();

    @NotNull UUID getTransactionId();

    @Nullable UUID getCorrelationId();

    @NotNull EventDateTime getOccurredAt();

    @NotNull String getTopic();

    @NotNull String getSerializedContent();
}
