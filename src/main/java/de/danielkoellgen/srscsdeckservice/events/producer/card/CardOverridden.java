package de.danielkoellgen.srscsdeckservice.events.producer.card;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.danielkoellgen.srscsdeckservice.domain.domainprimitive.EventDateTime;
import de.danielkoellgen.srscsdeckservice.events.producer.AbstractProducerEvent;
import de.danielkoellgen.srscsdeckservice.events.producer.card.dto.CardOverriddenDto;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.LocalDateTime;
import java.util.UUID;

public class CardOverridden extends AbstractProducerEvent {

    @NotNull
    private final CardOverriddenDto payloadDto;

    public static final String eventName = "card-overridden";

    public static final String eventTopic = "cdc.decks-cards.0";

    public CardOverridden(@NotNull UUID transactionId, @Nullable UUID correlationId, @NotNull CardOverriddenDto payloadDto) {
        super(UUID.randomUUID(), transactionId, correlationId, eventName, eventTopic,
                EventDateTime.makeFromLocalDateTime(LocalDateTime.now()));
        this.payloadDto = payloadDto;
    }

    @Override
    public @NotNull String getSerializedContent() {
        ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();
        try {
            return objectMapper.writeValueAsString(payloadDto);
        } catch (Exception e) {
            throw new RuntimeException("ObjectMapper conversion failed.");
        }
    }
}
