package de.danielkoellgen.srscsdeckservice.events.producer.deck;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.danielkoellgen.srscsdeckservice.domain.domainprimitive.EventDateTime;
import de.danielkoellgen.srscsdeckservice.events.producer.AbstractProducerEvent;
import de.danielkoellgen.srscsdeckservice.events.producer.deck.dto.DeckDisabledDto;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;
import java.util.UUID;

public class DeckDisabled extends AbstractProducerEvent {

    @NotNull
    private final DeckDisabledDto payloadDto;

    public static final String eventName = "deck-disabled";

    public static final String eventTopic = "cdc.decks-cards.0";

    public DeckDisabled(@NotNull String transactionId, @NotNull DeckDisabledDto payloadDto) {
        super(UUID.randomUUID(), transactionId, null, eventName, eventTopic,
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

    @Override
    public String toString() {
        return "DeckDisabled{" +
                "payloadDto=" + payloadDto +
                ", " + super.toString() +
                '}';
    }
}
