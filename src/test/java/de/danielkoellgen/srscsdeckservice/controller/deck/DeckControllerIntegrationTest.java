package de.danielkoellgen.srscsdeckservice.controller.deck;

import de.danielkoellgen.srscsdeckservice.controller.deck.dto.DeckRequestDto;
import de.danielkoellgen.srscsdeckservice.controller.deck.dto.DeckResponseDto;
import de.danielkoellgen.srscsdeckservice.domain.deck.application.DeckService;
import de.danielkoellgen.srscsdeckservice.domain.deck.repository.DeckRepository;
import de.danielkoellgen.srscsdeckservice.domain.user.application.UserService;
import de.danielkoellgen.srscsdeckservice.domain.user.domain.User;
import de.danielkoellgen.srscsdeckservice.domain.user.domain.Username;
import de.danielkoellgen.srscsdeckservice.domain.user.repository.UserRepository;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class DeckControllerIntegrationTest {

    private final WebTestClient webTestClient;

    private final UserService userService;
    private final DeckRepository deckRepository;
    private final UserRepository userRepository;

    private User user1;
    private User user2;

    @Autowired
    public DeckControllerIntegrationTest(DeckController deckController, UserService userService,
            DeckRepository deckRepository, UserRepository userRepository)
    {
        this.webTestClient = WebTestClient.bindToController(deckController).build();
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
    public void shouldAllowToCreateNewDecks()
    {
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

    @Test
    public void shouldAllowToDisableDecks() {
        // given
        DeckRequestDto requestDto = new DeckRequestDto(user1.getUserId(), "THKoeln");
        DeckResponseDto deckCreatedResponseDto = externallyCreateDeck(requestDto);
        UUID deckId = deckCreatedResponseDto.deckId();

        // then
        DeckResponseDto fetchedDeck = fetchDeck(deckId);
        assertThat(fetchedDeck.isActive())
                .isTrue();

        // when
        webTestClient.delete().uri("/decks/"+deckId)
                .exchange()
                .expectStatus().isOk();

        // then
        fetchedDeck = fetchDeck(deckId);
        assertThat(fetchedDeck.isActive())
                .isFalse();
    }

    @Test
    public void shouldAllowToFetchDecksByUserId() {
        // given
        DeckRequestDto requestDtoDeckOne = new DeckRequestDto(user1.getUserId(), "Deck1");
        DeckResponseDto responseDtoDeckOne = externallyCreateDeck(requestDtoDeckOne);
        DeckRequestDto requestDtoDeckTwo = new DeckRequestDto(user1.getUserId(), "Deck2");
        DeckResponseDto responseDtoDeckTwo = externallyCreateDeck(requestDtoDeckTwo);
        DeckRequestDto requestDtoDeckThree = new DeckRequestDto(user1.getUserId(), "Deck3");
        DeckResponseDto responseDtoDeckThree = externallyCreateDeck(requestDtoDeckThree);
        DeckRequestDto requestDtoDeckFour = new DeckRequestDto(user2.getUserId(), "Deck3");
        DeckResponseDto responseDtoDeckFour = externallyCreateDeck(requestDtoDeckFour);

        // when
        List<DeckResponseDto> fetchedDecks = webTestClient.get().uri("/decks?user-id="+user1.getUserId())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectBodyList(DeckResponseDto.class)
                .returnResult()
                .getResponseBody();

        // then
        assertThat(fetchedDecks)
                .hasSize(3);
        assertThat(fetchedDecks)
                .contains(responseDtoDeckOne)
                .contains(responseDtoDeckTwo)
                .contains(responseDtoDeckThree)
                .doesNotContain(responseDtoDeckFour);
    }

    private @NotNull DeckResponseDto externallyCreateDeck(DeckRequestDto requestDto) {
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
        return responseDto;
    }

    private @Nullable DeckResponseDto fetchDeck(UUID deckId) {
        return webTestClient.get().uri("/decks/"+deckId)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectBody(DeckResponseDto.class)
                .returnResult().getResponseBody();
    }
}
