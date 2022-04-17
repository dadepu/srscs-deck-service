package de.danielkoellgen.srscsdeckservice.domain.card.domain;

import de.danielkoellgen.srscsdeckservice.domain.deck.domain.Deck;
import de.danielkoellgen.srscsdeckservice.domain.schedulerpreset.domain.SchedulerPreset;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.UUID;

@Getter
public class AbstractCard {

    @Id
    @NotNull
    private final UUID cardId;

    @Nullable
    @Field("parent_card_id")
    private UUID parentCardId;

    @Setter
    @Nullable
    @Transient
    private Deck deck;

    @NotNull
    @Field("deck")
    private final EmbeddedDeck embeddedDeck;

    @NotNull
    @Field("scheduler")
    private final Scheduler scheduler;

    @NotNull
    @Field("is_active")
    private Boolean isActive;

    public AbstractCard(@NotNull Deck deck, @NotNull SchedulerPreset schedulerPreset) {
        this.cardId = UUID.randomUUID();
        this.deck = deck;
        this.embeddedDeck = new EmbeddedDeck(deck);
        this.scheduler = new Scheduler(schedulerPreset);
        this.isActive = true;
    }

    public AbstractCard(@NotNull AbstractCard parentCard) {
        this.cardId = UUID.randomUUID();
        this.parentCardId = parentCard.parentCardId;
        this.embeddedDeck = parentCard.getEmbeddedDeck();
        this.scheduler = parentCard.getScheduler();
        this.isActive = true;
    }

    public AbstractCard(@NotNull AbstractCard parentCard, @NotNull Deck deck) {
        this.cardId = UUID.randomUUID();
        this.parentCardId = parentCard.parentCardId;
        this.embeddedDeck = new EmbeddedDeck(deck);
        this.scheduler = parentCard.getScheduler();
        this.isActive = true;
    }

    @PersistenceConstructor
    public AbstractCard(@NotNull UUID cardId, @NotNull EmbeddedDeck embeddedDeck, @NotNull Scheduler scheduler,
            @NotNull Boolean isActive) {
        this.cardId = cardId;
        this.embeddedDeck = embeddedDeck;
        this.scheduler = scheduler;
        this.isActive = isActive;
    }

    public void disableCard() {
        isActive = false;
    }

    public void resetScheduler() {
        scheduler.reset();
    }

    public void graduateCard() {
        scheduler.graduate();
    }

    public void reviewCard(@NotNull ReviewAction reviewAction) {
        scheduler.review(reviewAction);
    }

    public void replaceSchedulerPreset(@NotNull SchedulerPreset schedulerPreset) {
        scheduler.updateSchedulerPreset(schedulerPreset);
    }
}
