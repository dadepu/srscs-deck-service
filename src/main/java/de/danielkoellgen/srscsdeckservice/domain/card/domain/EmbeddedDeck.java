package de.danielkoellgen.srscsdeckservice.domain.card.domain;

import de.danielkoellgen.srscsdeckservice.domain.deck.domain.Deck;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.UUID;

@Getter
public class EmbeddedDeck {

    @NotNull
    @Field("deck_id")
    private final UUID deckId;

    @PersistenceConstructor
    public EmbeddedDeck(@NotNull UUID deckId) {
        this.deckId = deckId;
    }

    public EmbeddedDeck(@NotNull Deck deck) {
        this.deckId = deck.getDeckId();
    }
}
