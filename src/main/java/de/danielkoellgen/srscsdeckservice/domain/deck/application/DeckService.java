package de.danielkoellgen.srscsdeckservice.domain.deck.application;

import de.danielkoellgen.srscsdeckservice.domain.deck.domain.Deck;
import de.danielkoellgen.srscsdeckservice.domain.deck.repository.DeckRepository;
import de.danielkoellgen.srscsdeckservice.domain.deck.domain.DeckName;
import de.danielkoellgen.srscsdeckservice.domain.user.application.UserService;
import de.danielkoellgen.srscsdeckservice.domain.user.domain.User;
import de.danielkoellgen.srscsdeckservice.domain.user.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class DeckService {

    private final DeckRepository deckRepository;
    private final UserRepository userRepository;

    private final Logger logger = LoggerFactory.getLogger(UserService.class);

    @Autowired
    public DeckService(DeckRepository deckRepository, UserRepository userRepository) {
        this.deckRepository = deckRepository;
        this.userRepository = userRepository;
    }

    public UUID createNewDeck(UUID transactionId, UUID userId, DeckName deckName) {
        User user = userRepository.findById(userId).get();
        Deck deck = new Deck(user, deckName);
        deckRepository.save(deck);

        logger.info("New Deck '{}' created for '{}'. [tid={}, deckId={}]",
                deckName.getName(), user.getUsername().getUsername(), transactionId, deck.getDeckId());
        logger.trace("New Deck created. [{}]", deck);

        return deck.getDeckId();
    }
}
