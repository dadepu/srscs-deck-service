package de.danielkoellgen.srscsdeckservice.events.producer;

import de.danielkoellgen.srscsdeckservice.domain.domainprimitive.EventDateTime;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public interface ProducerEvent {

    @NotNull UUID getEventId();

    @NotNull String getEventName();

    @NotNull UUID getTransactionId();

    @NotNull Integer getVersion();

    @NotNull EventDateTime getOccurredAt();

    @NotNull String getTopic();

    @NotNull String getSerializedContent();
}
