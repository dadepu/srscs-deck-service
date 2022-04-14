package de.danielkoellgen.srscsdeckservice.events.deck;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.danielkoellgen.srscsdeckservice.domain.domainprimitive.EventDateTime;
import de.danielkoellgen.srscsdeckservice.events.AbstractProducerEvent;
import de.danielkoellgen.srscsdeckservice.events.deck.dto.DeckCreatedDto;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;
import java.util.UUID;

public class DeckCreated extends AbstractProducerEvent {

    @NotNull
    private final DeckCreatedDto payloadDto;

    public static final String eventName = "deck-created";

    public static final Integer eventVersion = 1;

    public static final String eventTopic = "cdc.decks-cards.0";

    public DeckCreated(@NotNull UUID transactionId, @NotNull DeckCreatedDto payloadDto) {
        super(UUID.randomUUID(), transactionId, eventVersion, eventName, eventTopic,
                EventDateTime.makeFromLocalDateTime(LocalDateTime.now())
        );
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
