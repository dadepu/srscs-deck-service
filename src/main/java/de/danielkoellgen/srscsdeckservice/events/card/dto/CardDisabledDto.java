package de.danielkoellgen.srscsdeckservice.events.card.dto;

import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public record CardDisabledDto(

    @NotNull UUID cardId

) {
}
