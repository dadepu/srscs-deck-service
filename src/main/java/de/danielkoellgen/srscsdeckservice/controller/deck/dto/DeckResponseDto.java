package de.danielkoellgen.srscsdeckservice.controller.deck.dto;

import de.danielkoellgen.srscsdeckservice.domain.deck.domain.Deck;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public record DeckResponseDto(

    @NotNull
    UUID deckId,

    @NotNull
    String deckName,

    @NotNull
    UUID userId,

    @Nullable
    UUID schedulerPresetId,

    @NotNull
    Boolean isActive

) {
    public DeckResponseDto(@NotNull Deck deck) {
        this(deck.getDeckId(),
                deck.getDeckName().getName(),
                deck.getUserId(),
                (deck.getSchedulerPreset() != null ? deck.getSchedulerPreset().getPresetId() : null),
                deck.getIsActive());
    }
}
