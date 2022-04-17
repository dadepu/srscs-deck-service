package de.danielkoellgen.srscsdeckservice.domain.card.domain;

import de.danielkoellgen.srscsdeckservice.domain.deck.domain.Deck;
import de.danielkoellgen.srscsdeckservice.domain.schedulerpreset.domain.SchedulerPreset;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.UUID;

@Document("typing_cards")
public class TypingCard extends AbstractCard {

    public TypingCard(@NotNull AbstractCard parentCard) {
        super(parentCard);
    }

    public TypingCard(@NotNull AbstractCard parentCard, @NotNull Deck deck) {
        super(parentCard, deck);
    }

    public TypingCard(@NotNull Deck deck, @NotNull SchedulerPreset schedulerPreset) {
        super(deck, schedulerPreset);
    }

    @PersistenceConstructor
    public TypingCard(@NotNull UUID cardId, @NotNull EmbeddedDeck embeddedDeck, @NotNull Scheduler scheduler,
            @NotNull Boolean isActive) {
        super(cardId, embeddedDeck, scheduler, isActive);
    }
}
