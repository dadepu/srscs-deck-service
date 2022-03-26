package de.danielkoellgen.srscsdeckservice.integrationtests.domain.deck;

import de.danielkoellgen.srscsdeckservice.domain.deck.application.DeckService;
import de.danielkoellgen.srscsdeckservice.domain.deck.domain.Deck;
import de.danielkoellgen.srscsdeckservice.domain.deck.repository.DeckRepository;
import de.danielkoellgen.srscsdeckservice.domain.deck.domain.DeckName;
import de.danielkoellgen.srscsdeckservice.domain.user.domain.Username;
import de.danielkoellgen.srscsdeckservice.domain.user.application.UserService;
import de.danielkoellgen.srscsdeckservice.domain.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class CreateNewDeckIntegrationTest {

    private final UserService userService;
    private final DeckService deckService;

    private final UserRepository userRepository;
    private final DeckRepository deckRepository;

    private final UUID userId = UUID.randomUUID();
    private final Username username = new Username("dadepu");

    @Autowired
    public CreateNewDeckIntegrationTest(UserService userService, DeckService deckService, UserRepository userRepository,
            DeckRepository deckRepository) throws Exception {
        this.userService = userService;
        this.deckService = deckService;
        this.userRepository = userRepository;
        this.deckRepository = deckRepository;
    }

    @BeforeEach
    public void setUp() throws Exception {
        userService.addNewExternallyCreatedUser(UUID.randomUUID(), userId, username);
    }

//    @AfterEach
    public void cleanUp() {
        userRepository.deleteAll();
        deckRepository.deleteAll();
    }

    @Test
    public void shouldPersistNewlyCreatedDeck() throws Exception {
        // given
        DeckName deckName = new DeckName("THKoeln");

        // when
        UUID deckId = deckService.createNewDeck(UUID.randomUUID(), userId, deckName);

        // then
        Deck fetchedDeck = deckRepository.findById(deckId).get();
        assertThat(fetchedDeck.getDeckName())
                .isEqualTo(deckName);

        // and then
        assertThat(fetchedDeck.getUserId())
                .isEqualTo(userId);
        assertThat(fetchedDeck.getUsername())
                .isEqualTo(username);
    }
}
