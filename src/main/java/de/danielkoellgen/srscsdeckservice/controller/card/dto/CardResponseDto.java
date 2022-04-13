package de.danielkoellgen.srscsdeckservice.controller.card.dto;

import de.danielkoellgen.srscsdeckservice.domain.card.domain.AbstractCard;
import de.danielkoellgen.srscsdeckservice.domain.card.domain.DefaultCard;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public record CardResponseDto(

    @NotNull
    UUID cardId,

    @NotNull
    UUID deckId,

    @NotNull
    String cardType,

    @NotNull
    String cardStatus,

    @Nullable
    HintDto hint,

    @Nullable
    ViewDto frontView,

    @Nullable
    ViewDto backView

) {
    public static @NotNull CardResponseDto makeFromDefaultCard(DefaultCard card) {
        return new CardResponseDto(
                card.getCardId(), card.getEmbeddedDeck().getDeckId(), "default",
                (card.getIsActive() ? "active"  : "inactive"),
                (card.getHint() != null ? new HintDto(card.getHint()) : null),
                (card.getFrontView() != null ? new ViewDto(card.getFrontView()) : null),
                (card.getBackView() != null ? new ViewDto(card.getBackView()) : null)
        );
    }
}
