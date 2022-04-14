package de.danielkoellgen.srscsdeckservice.events.deck.dto;

import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public record DeckDisabledDto(

    @NotNull UUID deckId

) {
}
