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
import org.springframework.data.mongodb.core.mapping.DocumentReference;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.UUID;

@Getter
@Document("cards")
public class AbstractCard {

    @Id
    @NotNull
    private final UUID cardId;

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

    public AbstractCard(@NotNull Deck deck, @NotNull SchedulerPreset schedulerPreset) {
        this.cardId = UUID.randomUUID();
        this.deck = deck;
        this.embeddedDeck = new EmbeddedDeck(deck);
        this.scheduler = new Scheduler(schedulerPreset);
    }

    @PersistenceConstructor
    public AbstractCard(@NotNull UUID cardId, @NotNull EmbeddedDeck embeddedDeck, @NotNull Scheduler scheduler) {
        this.cardId = cardId;
        this.embeddedDeck = embeddedDeck;
        this.scheduler = scheduler;
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
