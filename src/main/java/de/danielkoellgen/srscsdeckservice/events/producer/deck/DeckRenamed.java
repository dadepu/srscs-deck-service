package de.danielkoellgen.srscsdeckservice.events.producer.deck;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.danielkoellgen.srscsdeckservice.domain.domainprimitive.EventDateTime;
import de.danielkoellgen.srscsdeckservice.events.producer.AbstractProducerEvent;
import de.danielkoellgen.srscsdeckservice.events.producer.deck.dto.DeckRenamedDto;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;
import java.util.UUID;

public class DeckRenamed extends AbstractProducerEvent {

    @NotNull
    private final DeckRenamedDto payloadDto;

    public static final String eventName = "deck-renamed";

    public static final Integer eventVersion = 1;

    public static final String eventTopic = "cdc.decks-cards.0";

    public DeckRenamed(@NotNull UUID transactionId, @NotNull DeckRenamedDto payloadDto) {
        super(UUID.randomUUID(), transactionId, eventVersion, eventName, eventTopic,
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
