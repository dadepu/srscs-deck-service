package de.danielkoellgen.srscsdeckservice.events.deck.dto;

import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public record DeckRenamedDto(

    @NotNull UUID deckId,

    @NotNull String newDeckName

) {
}
