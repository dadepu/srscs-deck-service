package de.danielkoellgen.srscsdeckservice.domain.card.domain;

import de.danielkoellgen.srscsdeckservice.domain.deck.domain.Deck;
import de.danielkoellgen.srscsdeckservice.domain.schedulerpreset.domain.SchedulerPreset;
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

    public DefaultCard(@NotNull Deck deck, @NotNull SchedulerPreset schedulerPreset, @Nullable Hint hint,
            @Nullable View frontView, @Nullable View backView) {
        super(deck, schedulerPreset);
        this.hint = hint;
        this.frontView = frontView;
        this.backView = backView;
    }

    public DefaultCard(@NotNull AbstractCard parentCard,
            @Nullable Hint hint, @Nullable View frontView, @Nullable View backView) {
        super(parentCard);
        this.hint = hint;
        this.frontView = frontView;
        this.backView = backView;
    }

    public DefaultCard(@NotNull AbstractCard parentCard, @NotNull Deck deck,
            @Nullable Hint hint, @Nullable View frontView, @Nullable View backView) {
        super(parentCard, deck);
        this.hint = hint;
        this.frontView = frontView;
        this.backView = backView;
    }

    @PersistenceConstructor
    public DefaultCard(@NotNull UUID cardId, @NotNull EmbeddedDeck embeddedDeck, @NotNull Scheduler scheduler,
            @Nullable Hint hint, @Nullable View frontView, @Nullable View backView, @NotNull Boolean isActive) {
        super(cardId, embeddedDeck, scheduler, isActive);
        this.hint = hint;
        this.frontView = frontView;
        this.backView = backView;
    }
}
