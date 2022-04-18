package de.danielkoellgen.srscsdeckservice.domain.card.domain;

import de.danielkoellgen.srscsdeckservice.domain.deck.domain.Deck;
import de.danielkoellgen.srscsdeckservice.domain.schedulerpreset.domain.SchedulerPreset;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.UUID;

@Document("typing_cards")
public class TypingCard extends AbstractCard {

    public TypingCard(@NotNull UUID cardId, @Nullable UUID parentCardId, @NotNull Deck deck,
            @NotNull EmbeddedDeck embeddedDeck, @NotNull Scheduler scheduler, @NotNull Boolean isActive) {
        super(cardId, parentCardId, deck, embeddedDeck, scheduler, isActive);
    }

    @PersistenceConstructor
    public TypingCard(@NotNull UUID cardId, @Nullable UUID parentCardId, @NotNull EmbeddedDeck embeddedDeck,
            @NotNull Scheduler scheduler, @NotNull Boolean isActive) {
        super(cardId, parentCardId, embeddedDeck, scheduler, isActive);
    }

    public static @NotNull TypingCard makeNew(@NotNull Deck deck, @NotNull SchedulerPreset schedulerPreset) {
        return new TypingCard(UUID.randomUUID(), null, new EmbeddedDeck(deck),
                new Scheduler(schedulerPreset), true);
    }

    public static @NotNull TypingCard makeNewAsCloned(@NotNull TypingCard referenceCard, @NotNull Deck deck,
            @NotNull SchedulerPreset schedulerPreset) {
        return new TypingCard(UUID.randomUUID(), null, deck, new EmbeddedDeck(deck),
                new Scheduler(schedulerPreset), true);
    }

    public static @NotNull TypingCard makeNewAsOverridden(@NotNull AbstractCard parentCard) {
        return new TypingCard(UUID.randomUUID(), parentCard.getCardId(), parentCard.getEmbeddedDeck(),
                parentCard.getScheduler(), true);
    }

    public static @NotNull TypingCard makeNewAsOverridden(@NotNull AbstractCard parentCard, @NotNull Deck deck) {
        return new TypingCard(UUID.randomUUID(), parentCard.getCardId(), deck, new EmbeddedDeck(deck),
                parentCard.getScheduler(), true);
    }
}
