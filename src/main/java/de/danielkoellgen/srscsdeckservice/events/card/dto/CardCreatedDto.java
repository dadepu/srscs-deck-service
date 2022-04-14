package de.danielkoellgen.srscsdeckservice.events.card.dto;

import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public record CardCreatedDto(

    @NotNull UUID cardId,

    @NotNull UUID deckId

) {
}
