package de.danielkoellgen.srscsdeckservice.domain.card.domain;

import de.danielkoellgen.srscsdeckservice.domain.deck.domain.Deck;
import de.danielkoellgen.srscsdeckservice.domain.schedulerpreset.domain.SchedulerPreset;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.annotation.PersistenceConstructor;

import java.util.UUID;

public class TypingCard extends AbstractCard {


    public TypingCard(@NotNull Deck deck, @NotNull SchedulerPreset schedulerPreset) {
        super(deck, schedulerPreset);
    }

    @PersistenceConstructor
    public TypingCard(@NotNull UUID cardId, @NotNull EmbeddedDeck embeddedDeck, @NotNull Scheduler scheduler,
            @NotNull Boolean isActive) {
        super(cardId, embeddedDeck, scheduler, isActive);
    }
}
