package de.danielkoellgen.srscsdeckservice.domain.card.repository;

import de.danielkoellgen.srscsdeckservice.domain.card.domain.AbstractCard;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.UUID;

public interface CardRepository extends CrudRepository<AbstractCard, UUID> {

    List<AbstractCard> findAllByEmbeddedDeck_DeckId(@NotNull UUID deckId);

    List<AbstractCard> findAllByEmbeddedDeck_DeckIdAndIsActive(@NotNull UUID deckId, Boolean isActive);
}
