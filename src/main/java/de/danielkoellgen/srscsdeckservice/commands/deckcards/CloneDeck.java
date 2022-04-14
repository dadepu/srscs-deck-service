package de.danielkoellgen.srscsdeckservice.commands.deckcards;

import com.fasterxml.jackson.core.JsonProcessingException;
import de.danielkoellgen.srscsdeckservice.commands.deckcards.dto.CloneDeckDto;
import de.danielkoellgen.srscsdeckservice.domain.deck.application.DeckService;
import de.danielkoellgen.srscsdeckservice.events.consumer.AbstractConsumerEvent;
import lombok.Getter;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.jetbrains.annotations.NotNull;

public class CloneDeck extends AbstractConsumerEvent {

    private final DeckService deckService;

    @Getter
    private final @NotNull CloneDeckDto payload;

    public CloneDeck(@NotNull DeckService deckService, @NotNull ConsumerRecord<String, String> event) throws JsonProcessingException {
        super(event);
        this.deckService = deckService;
        this.payload = CloneDeckDto.makeFromSerialization(event.value());
    }

    @Override
    public void execute() {
        //TODO
    }
}
