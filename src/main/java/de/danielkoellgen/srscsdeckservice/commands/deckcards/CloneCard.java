package de.danielkoellgen.srscsdeckservice.commands.deckcards;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.danielkoellgen.srscsdeckservice.commands.deckcards.dto.CloneCardDto;
import de.danielkoellgen.srscsdeckservice.domain.card.application.CardService;
import de.danielkoellgen.srscsdeckservice.events.consumer.AbstractConsumerEvent;
import lombok.Getter;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.jetbrains.annotations.NotNull;

public class CloneCard extends AbstractConsumerEvent {

    private final CardService cardService;

    @Getter
    private final @NotNull CloneCardDto payload;

    public CloneCard(@NotNull CardService cardService, @NotNull ConsumerRecord<String, String> event)
            throws JsonProcessingException {
        super(event);
        this.cardService = cardService;
        this.payload = CloneCardDto.makeFromSerialization(event.value());
    }

    @Override
    public void execute() {
        cardService.cloneCard(correlationId, payload.referencedCardId(), payload.targetDeckId());
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
        return "CloneCard{" +
                "payload=" + payload +
                ", " + super.toString() +
                '}';
    }
}
