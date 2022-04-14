package de.danielkoellgen.srscsdeckservice.events.deck.dto;

import de.danielkoellgen.srscsdeckservice.domain.deck.domain.Deck;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public record DeckCreatedDto(

    @NotNull UUID deckId,

    @NotNull UUID userId,

    @NotNull String deckName

) {
    public DeckCreatedDto(@NotNull Deck deck) {
        this(deck.getDeckId(), deck.getUserId(), deck.getDeckName().getName());
    }
}
