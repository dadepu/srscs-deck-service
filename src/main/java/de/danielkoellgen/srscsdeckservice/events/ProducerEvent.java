package de.danielkoellgen.srscsdeckservice.events;

import de.danielkoellgen.srscsdeckservice.domain.domainprimitive.EventDateTime;

import java.util.UUID;

public interface ProducerEvent {

    UUID getEventId();

    String getEventName();

    UUID getTransactionId();

    Integer getVersion();

    EventDateTime getOccurredAt();

    String getTopic();

    String getSerializedContent();
}
