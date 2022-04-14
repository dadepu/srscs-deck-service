package de.danielkoellgen.srscsdeckservice.commands.deckcards;

import com.fasterxml.jackson.core.JsonProcessingException;
import de.danielkoellgen.srscsdeckservice.domain.card.application.CardService;
import de.danielkoellgen.srscsdeckservice.domain.deck.application.DeckService;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

@Component
public class KafkaDeckCardsCommandConsumer {

    private final DeckService deckService;
    private final CardService cardService;

    @Autowired
    public KafkaDeckCardsCommandConsumer(DeckService deckService, CardService cardService) {
        this.deckService = deckService;
        this.cardService = cardService;
    }

    @KafkaListener(topics = "cmd.decks-cards.0")
    public void receive(@NotNull ConsumerRecord<String, String> command) throws JsonProcessingException {
        String eventName = getHeaderValue(command, "type");
        switch (eventName) {
            case "create-deck"      -> processCreateDeckCommand(command);
            case "clone-deck"       -> processCloneDeckCommand(command);
            case "override-card"    -> processOverrideCardCommand(command);
            default -> throw new RuntimeException("Received event on 'cdc.users.0' of unknown type '"+eventName+"'.");
        }
    }

    private void processCreateDeckCommand(@NotNull ConsumerRecord<String, String> command) throws JsonProcessingException {
        CreateDeck createDeck = new CreateDeck(deckService, command);
        createDeck.execute();
    }

    private void processCloneDeckCommand(@NotNull ConsumerRecord<String, String> command) throws JsonProcessingException {
        CloneDeck cloneDeck = new CloneDeck(deckService, command);
        cloneDeck.execute();
    }

    private void processOverrideCardCommand(@NotNull ConsumerRecord<String, String> command) throws JsonProcessingException {
        OverrideCard overrideCard = new OverrideCard(cardService, command);
        overrideCard.execute();
    }

    public static String getHeaderValue(ConsumerRecord<String, String> event, String key) {
        return new String(event.headers().lastHeader(key).value(), StandardCharsets.US_ASCII);
    }
}
