package de.danielkoellgen.srscsdeckservice.domain.card.domain;

import de.danielkoellgen.srscsdeckservice.domain.deck.domain.Deck;
import de.danielkoellgen.srscsdeckservice.domain.schedulerpreset.domain.SchedulerPreset;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.UUID;

@Getter
public class DefaultCard extends AbstractCard {

    @Nullable
    @Field("hint")
    private final Hint hint;

    @Nullable
    @Field("front_view")
    private final View frontView;

    @Nullable
    @Field("back_view")
    private final View backView;

    public DefaultCard(@NotNull Deck deck, @NotNull SchedulerPreset schedulerPreset, @Nullable Hint hint,
            @Nullable View frontView, @Nullable View backView) {
        super(deck, schedulerPreset);
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
