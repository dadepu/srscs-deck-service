package de.danielkoellgen.srscsdeckservice.events.deck.dto;

import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public record DeckCreatedDto(

    @NotNull UUID deckId,

    @NotNull UUID userId,

    @NotNull String deckName

) {
}
