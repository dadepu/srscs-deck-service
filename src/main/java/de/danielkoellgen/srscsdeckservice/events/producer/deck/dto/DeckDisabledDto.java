package de.danielkoellgen.srscsdeckservice.events.producer.deck.dto;

import de.danielkoellgen.srscsdeckservice.domain.deck.domain.Deck;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public record DeckDisabledDto(

    @NotNull UUID deckId,

    @NotNull UUID userId

) {
    public DeckDisabledDto(@NotNull Deck deck) {
        this(deck.getDeckId(), deck.getUserId());
    }
}
