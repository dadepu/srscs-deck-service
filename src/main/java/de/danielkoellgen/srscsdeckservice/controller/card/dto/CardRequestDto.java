package de.danielkoellgen.srscsdeckservice.controller.card.dto;

import de.danielkoellgen.srscsdeckservice.domain.card.domain.CardType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public record CardRequestDto(

    @NotNull
    UUID deckId,

    @NotNull
    String cardType,

    @Nullable
    HintDto hint,

    @Nullable
    ViewDto frontView,

    @Nullable
    ViewDto backView

) {
    public @NotNull CardType getCardType() {
        return switch(cardType) {
            case "default" -> CardType.DEFAULT;
            default -> throw new RuntimeException("Invalid card-type");
        };
    }
}
