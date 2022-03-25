package de.danielkoellgen.srscsdeckservice.domain.deck.domain;

import de.danielkoellgen.srscsdeckservice.domain.user.domain.User;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.UUID;

@Getter
@Document("decks")
public class Deck {

    @Id
    private final UUID deckId;

    @Field("deck_name")
    private final DeckName deckName;

    @DocumentReference
    @Field("user_id")
    private final User user;

    public Deck(User user, DeckName deckName) {
        this.deckId = UUID.randomUUID();
        this.deckName = deckName;
        this.user = user;
    }

    @PersistenceConstructor
    public Deck(UUID deckId, DeckName deckName, User user) {
        this.deckId = deckId;
        this.deckName = deckName;
        this.user = user;
    }

    @Override
    public String toString() {
        return "Deck{" +
                "deckId=" + deckId +
                ", deckName=" + deckName +
                ", userId=" + user.getUserId() +
                '}';
    }
}
