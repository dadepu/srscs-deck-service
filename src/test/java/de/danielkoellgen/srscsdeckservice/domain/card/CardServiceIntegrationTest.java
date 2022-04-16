package de.danielkoellgen.srscsdeckservice.domain.card;

import de.danielkoellgen.srscsdeckservice.domain.card.application.CardService;
import de.danielkoellgen.srscsdeckservice.domain.card.domain.AbstractCard;
import de.danielkoellgen.srscsdeckservice.domain.card.domain.DefaultCard;
import de.danielkoellgen.srscsdeckservice.domain.card.repository.CardRepository;
import de.danielkoellgen.srscsdeckservice.domain.deck.application.DeckService;
import de.danielkoellgen.srscsdeckservice.domain.deck.domain.Deck;
import de.danielkoellgen.srscsdeckservice.domain.deck.domain.DeckName;
import de.danielkoellgen.srscsdeckservice.domain.deck.repository.DeckRepository;
import de.danielkoellgen.srscsdeckservice.domain.user.application.UserService;
import de.danielkoellgen.srscsdeckservice.domain.user.domain.User;
import de.danielkoellgen.srscsdeckservice.domain.user.domain.Username;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class CardServiceIntegrationTest {

    private final CardService cardService;
    private final UserService userService;
    private final DeckService deckService;
    private final CardRepository cardRepository;
    private final DeckRepository deckRepository;

    private User user1;
    private Deck deck1;

    @Autowired
    public CardServiceIntegrationTest(CardService cardService, UserService userService, DeckService deckService,
                CardRepository cardRepository, DeckRepository deckRepository) {
        this.cardService = cardService;
        this.userService = userService;
        this.deckService = deckService;
        this.cardRepository = cardRepository;
        this.deckRepository = deckRepository;
    }

    @BeforeEach
    public void setUp() throws Exception {
        user1 = userService.addNewExternallyCreatedUser(UUID.randomUUID(), UUID.randomUUID(), new Username("anyName"));
        deck1 = deckService.createNewDeck(UUID.randomUUID(), user1.getUserId(), new DeckName("anyDeckName"));
    }

    @AfterEach
    public void cleanUp() {
        deckRepository.deleteAll();
        cardRepository.deleteAll();
    }

    @Test
    public void shouldAllowToPersistAndFetchAbstractCards() throws Exception {
        // given
        // when
        DefaultCard createdCard = cardService.createDefaultCard(
                UUID.randomUUID(), deck1.getDeckId(), null, null, null
        );
        AbstractCard fetchedCard = cardRepository.findById(createdCard.getCardId()).orElseThrow();

        // then
        assertThat(createdCard.getScheduler().getCurrentInterval())
                .isEqualTo(fetchedCard.getScheduler().getCurrentInterval());
    }
}
