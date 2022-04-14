package de.danielkoellgen.srscsdeckservice.events.card.dto;

import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public record CardOverriddenDto(

    @NotNull UUID parentCardId,

    @NotNull UUID newCardId,

    @NotNull UUID deckId

) {
}
