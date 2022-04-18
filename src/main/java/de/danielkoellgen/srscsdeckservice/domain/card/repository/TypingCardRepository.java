package de.danielkoellgen.srscsdeckservice.domain.card.repository;

import de.danielkoellgen.srscsdeckservice.domain.card.domain.TypingCard;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.UUID;

public interface TypingCardRepository extends CrudRepository<TypingCard, UUID> {

    List<TypingCard> findAllByEmbeddedDeck_DeckId(@NotNull UUID deckId);

    List<TypingCard> findAllByEmbeddedDeck_DeckIdAndIsActive(@NotNull UUID deckId, Boolean isActive);
}
