package de.danielkoellgen.srscsdeckservice.domain.card.domain;

import de.danielkoellgen.srscsdeckservice.domain.deck.domain.Deck;
import de.danielkoellgen.srscsdeckservice.domain.schedulerpreset.domain.SchedulerPreset;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.UUID;

@Document("default_cards")
@Getter
public class DefaultCard extends AbstractCard {

    @Field("hint")
    private final @Nullable Hint hint;

    @Field("front_view")
    private final @Nullable View frontView;

    @Field("back_view")
    private final @Nullable View backView;

    public DefaultCard(@NotNull UUID cardId, @Nullable UUID parentCardId, @Nullable Deck deck,
            @NotNull EmbeddedDeck embeddedDeck, @NotNull Scheduler scheduler, @NotNull Boolean isActive,
            @Nullable Hint hint, @Nullable View frontView, @Nullable View backView) {
        super(cardId, parentCardId, deck, embeddedDeck, scheduler, isActive);
        this.hint = hint;
        this.frontView = frontView;
        this.backView = backView;
    }

    @PersistenceConstructor
    public DefaultCard(@NotNull UUID cardId, @Nullable UUID parentCardId, @NotNull EmbeddedDeck embeddedDeck,
            @NotNull Scheduler scheduler, @NotNull Boolean isActive, @Nullable Hint hint, @Nullable View frontView,
            @Nullable View backView) {
        super(cardId, parentCardId, embeddedDeck, scheduler, isActive);
        this.hint = hint;
        this.frontView = frontView;
        this.backView = backView;
    }

    public static @NotNull DefaultCard makeNew(@NotNull Deck deck, @NotNull SchedulerPreset schedulerPreset,
            @Nullable Hint hint, @Nullable View frontView, @Nullable View backView) {
        return new DefaultCard(UUID.randomUUID(), null, deck, new EmbeddedDeck(deck),
                new Scheduler(schedulerPreset), true, hint, frontView, backView);
    }

    public static @NotNull DefaultCard makeNewAsCloned(@NotNull DefaultCard referenceCard, @NotNull Deck deck,
            @NotNull SchedulerPreset schedulerPreset) {
        return new DefaultCard(UUID.randomUUID(), null, deck, new EmbeddedDeck(deck),
                new Scheduler(schedulerPreset), true, referenceCard.getHint(), referenceCard.getFrontView(),
                referenceCard.getFrontView());
    }

    public static @NotNull DefaultCard makeNewAsOverridden(@NotNull AbstractCard parentCard, @Nullable Hint hint,
            @Nullable View frontView, @Nullable View backView) {
        return new DefaultCard(UUID.randomUUID(), parentCard.getCardId(), null, parentCard.getEmbeddedDeck(),
                parentCard.getScheduler(), true, hint, frontView, backView);
    }

    public static @NotNull DefaultCard makeNewAsOverridden(@NotNull AbstractCard parentCard, @NotNull Deck deck,
            @Nullable Hint hint, @Nullable View frontView, @Nullable View backView) {
        return new DefaultCard(UUID.randomUUID(), parentCard.getCardId(), deck, new EmbeddedDeck(deck),
                parentCard.getScheduler(), true, hint, frontView, backView);
    }

    @Override
    public String toString() {
        return "DefaultCard{" +
                "hint=" + hint +
                ", frontView=" + frontView +
                ", backView=" + backView +
                ", " + super.toString() +
                '}';
    }
}
