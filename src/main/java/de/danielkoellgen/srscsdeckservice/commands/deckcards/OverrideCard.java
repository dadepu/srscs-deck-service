package de.danielkoellgen.srscsdeckservice.commands.deckcards;

import com.fasterxml.jackson.core.JsonProcessingException;
import de.danielkoellgen.srscsdeckservice.commands.deckcards.dto.CreateDeckDto;
import de.danielkoellgen.srscsdeckservice.commands.deckcards.dto.OverrideCardDto;
import de.danielkoellgen.srscsdeckservice.domain.card.application.CardService;
import de.danielkoellgen.srscsdeckservice.domain.deck.application.DeckService;
import de.danielkoellgen.srscsdeckservice.events.consumer.AbstractConsumerEvent;
import lombok.Getter;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.jetbrains.annotations.NotNull;

public class OverrideCard extends AbstractConsumerEvent {

    private final CardService cardService;

    @Getter
    private final @NotNull OverrideCardDto payload;

    public OverrideCard(@NotNull CardService cardService, @NotNull ConsumerRecord<String, String> event) throws JsonProcessingException {
        super(event);
        this.cardService = cardService;
        this.payload = OverrideCardDto.makeFromSerialization(event.value());
    }

    @Override
    public void execute() {
        //TODO
    }
}
