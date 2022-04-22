package de.danielkoellgen.srscsdeckservice.events.producer.card.dto;

import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public record CardDisabledDto(

    @NotNull UUID cardId,

    @NotNull UUID userId

) {
}
