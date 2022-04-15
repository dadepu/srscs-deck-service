package de.danielkoellgen.srscsdeckservice.controller.deck;

import de.danielkoellgen.srscsdeckservice.controller.deck.dto.DeckRequestDto;
import de.danielkoellgen.srscsdeckservice.controller.deck.dto.DeckResponseDto;
import de.danielkoellgen.srscsdeckservice.domain.deck.application.DeckService;
import de.danielkoellgen.srscsdeckservice.domain.deck.repository.DeckRepository;
import de.danielkoellgen.srscsdeckservice.domain.user.application.UserService;
import de.danielkoellgen.srscsdeckservice.domain.user.domain.User;
import de.danielkoellgen.srscsdeckservice.domain.user.domain.Username;
import de.danielkoellgen.srscsdeckservice.domain.user.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class DeckControllerIntegrationTest {

    private final WebTestClient webTestClient;

    private final DeckService deckService;
    private final UserService userService;
    private final DeckRepository deckRepository;
    private final UserRepository userRepository;

    private User user1;
    private User user2;

    @Autowired
    public DeckControllerIntegrationTest(DeckController deckController, DeckService deckService, UserService userService,
            DeckRepository deckRepository, UserRepository userRepository)
    {
        this.webTestClient = WebTestClient.bindToController(deckController).build();
        this.deckService = deckService;
        this.userService = userService;
        this.deckRepository = deckRepository;
        this.userRepository = userRepository;
    }

    @BeforeEach
    public void setUp() throws Exception {
        user1 = userService.addNewExternallyCreatedUser(UUID.randomUUID(), UUID.randomUUID(), new Username("anyName"));
        user2 = userService.addNewExternallyCreatedUser(UUID.randomUUID(), UUID.randomUUID(), new Username("anyName2"));
    }

    @AfterEach
    public void cleanUp() {
        deckRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    public void shouldAllowToCreateNewDecks() throws Exception {
        // given
        DeckRequestDto requestDto = new DeckRequestDto(user1.getUserId(), "THKoeln");

        // when
        DeckResponseDto responseDto = webTestClient.post().uri("/decks")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(requestDto)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(DeckResponseDto.class)
                .returnResult()
                .getResponseBody();
        assert responseDto != null;

        // then
        assertThat(responseDto.userId())
                .isEqualTo(user1.getUserId());
        assertThat(responseDto.deckName())
                .isEqualTo(requestDto.getMappedDeckName().getName());
    }
}
