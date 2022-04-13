package de.danielkoellgen.srscsdeckservice.domain.deck.repository;

import de.danielkoellgen.srscsdeckservice.domain.deck.domain.Deck;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.UUID;

public interface DeckRepository extends CrudRepository<Deck, UUID> {

    List<Deck> findDecksByEmbeddedUser_UserId(@NotNull UUID userId);
}
