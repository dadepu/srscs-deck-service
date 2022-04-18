package de.danielkoellgen.srscsdeckservice.domain.card.repository;

import de.danielkoellgen.srscsdeckservice.domain.card.domain.AbstractCard;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CardRepository {

    @NotNull Optional<AbstractCard> findById(@NotNull UUID cardId);

    void save(@NotNull AbstractCard card);

    void saveAll(@NotNull List<AbstractCard> cards);

    void deleteAll();

    List<AbstractCard> findAllByEmbeddedDeck_DeckId(@NotNull UUID deckId);

    List<AbstractCard> findAllByEmbeddedDeck_DeckIdAndIsActive(@NotNull UUID deckId, Boolean isActive);
}