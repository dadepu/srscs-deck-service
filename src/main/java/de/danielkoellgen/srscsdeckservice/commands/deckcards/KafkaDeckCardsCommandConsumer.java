package de.danielkoellgen.srscsdeckservice.commands.deckcards;

import com.fasterxml.jackson.core.JsonProcessingException;
import de.danielkoellgen.srscsdeckservice.domain.card.application.CardService;
import de.danielkoellgen.srscsdeckservice.domain.deck.application.DeckService;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.sleuth.Span;
import org.springframework.cloud.sleuth.Tracer;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

@Component
public class KafkaDeckCardsCommandConsumer {

    private final DeckService deckService;
    private final CardService cardService;

    @Autowired
    private Tracer tracer;

    @Autowired
    public KafkaDeckCardsCommandConsumer(DeckService deckService, CardService cardService) {
        this.deckService = deckService;
        this.cardService = cardService;
    }

    @KafkaListener(topics = {"cmd.decks-cards.0"})
    public void receive(@NotNull ConsumerRecord<String, String> command) throws JsonProcessingException {
        String eventName = getHeaderValue(command, "type");
        switch (eventName) {
            case "create-deck"      -> processCreateDeckCommand(command);
            case "clone-deck"       -> processCloneDeckCommand(command);
            case "override-card"    -> processOverrideCardCommand(command);
            case "clone-card"      -> processCloneCardCommand(command);
            default -> throw new RuntimeException("Received event on 'cdc.users.0' of unknown type '"+eventName+"'.");
        }
    }

    private void processCreateDeckCommand(@NotNull ConsumerRecord<String, String> command) throws JsonProcessingException {
        Span newSpan = tracer.nextSpan().name("command-create-deck");
        try (Tracer.SpanInScope ws = this.tracer.withSpan(newSpan.start())) {

            CreateDeck createDeck = new CreateDeck(deckService, command);
            createDeck.execute();

        } finally {
            newSpan.end();
        }
    }

    private void processCloneDeckCommand(@NotNull ConsumerRecord<String, String> command) throws JsonProcessingException {
        Span newSpan = tracer.nextSpan().name("command-clone-deck");
        try (Tracer.SpanInScope ws = this.tracer.withSpan(newSpan.start())) {

            CloneDeck cloneDeck = new CloneDeck(deckService, command);
            cloneDeck.execute();

        } finally {
            newSpan.end();
        }
    }

    private void processOverrideCardCommand(@NotNull ConsumerRecord<String, String> command) throws JsonProcessingException {
        Span newSpan = tracer.nextSpan().name("command-override-card");
        try (Tracer.SpanInScope ws = this.tracer.withSpan(newSpan.start())) {

            OverrideCard overrideCard = new OverrideCard(cardService, command);
            overrideCard.execute();

        } finally {
            newSpan.end();
        }
    }

    private void processCloneCardCommand(@NotNull ConsumerRecord<String, String> command) throws JsonProcessingException {
        Span newSpan = tracer.nextSpan().name("command-clone-card");
        try (Tracer.SpanInScope ws = this.tracer.withSpan(newSpan.start())) {

            CloneCard cloneCard = new CloneCard(cardService, command);
            cloneCard.execute();

        } finally {
            newSpan.end();
        }
    }

    public static String getHeaderValue(ConsumerRecord<String, String> event, String key) {
        return new String(event.headers().lastHeader(key).value(), StandardCharsets.US_ASCII);
    }
}
