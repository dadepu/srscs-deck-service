package de.danielkoellgen.srscsdeckservice.controller.deck.dto;

import de.danielkoellgen.srscsdeckservice.domain.deck.domain.DeckName;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public record DeckRequestDto(

    @NotNull
    UUID userId,

    @NotNull
    String deckName

) {
    public @NotNull DeckName getMappedDeckName() {
        try {
            return new DeckName(deckName);
        } catch (Exception e) {
            throw new RuntimeException("Invalid deck-name.");
        }
    }
}
