package de.danielkoellgen.srscsdeckservice.domain.card.repository;

import de.danielkoellgen.srscsdeckservice.domain.card.domain.AbstractCard;
import de.danielkoellgen.srscsdeckservice.domain.card.domain.DefaultCard;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.UUID;

public interface DefaultCardRepository extends CrudRepository<DefaultCard, UUID> {

    List<DefaultCard> findAllByEmbeddedDeck_DeckId(@NotNull UUID deckId);

    List<DefaultCard> findAllByEmbeddedDeck_DeckIdAndIsActive(@NotNull UUID deckId, Boolean isActive);
}
