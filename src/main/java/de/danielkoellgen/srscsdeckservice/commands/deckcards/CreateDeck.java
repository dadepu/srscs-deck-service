package de.danielkoellgen.srscsdeckservice.commands.deckcards;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.danielkoellgen.srscsdeckservice.commands.deckcards.dto.CreateDeckDto;
import de.danielkoellgen.srscsdeckservice.domain.deck.application.DeckService;
import de.danielkoellgen.srscsdeckservice.events.consumer.AbstractConsumerEvent;
import lombok.Getter;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.jetbrains.annotations.NotNull;

public class CreateDeck extends AbstractConsumerEvent {

    private final DeckService deckService;

    @Getter
    private final @NotNull CreateDeckDto payload;

    public CreateDeck(@NotNull DeckService deckService, @NotNull ConsumerRecord<String, String> event)
            throws JsonProcessingException {
        super(event);
        this.deckService = deckService;
        this.payload = CreateDeckDto.makeFromSerialization(event.value());
    }

    @Override
    public void execute() {
        deckService.createNewDeck(correlationId, payload.userId(), payload.getMappedDeckName());
    }

    @Override
    public @NotNull String getSerializedContent() {
        ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();
        try {
            return objectMapper.writeValueAsString(payload);
        } catch (Exception e) {
            throw new RuntimeException("ObjectMapper conversion failed.");
        }
    }

    @Override
    public String toString() {
        return "CreateDeck{" +
                "payload=" + payload +
                ", " + super.toString() +
                '}';
    }
}
