package de.danielkoellgen.srscsdeckservice.events.producer.card.dto;

import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public record CardOverriddenDto(

    @NotNull UUID parentCardId,

    @NotNull UUID newCardId,

    @NotNull UUID deckId,

    @NotNull UUID userId

) {
}
