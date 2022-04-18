package de.danielkoellgen.srscsdeckservice.controller.deck;

import de.danielkoellgen.srscsdeckservice.controller.card.CardController;
import de.danielkoellgen.srscsdeckservice.controller.card.dto.*;
import de.danielkoellgen.srscsdeckservice.controller.deck.dto.DeckRequestDto;
import de.danielkoellgen.srscsdeckservice.controller.deck.dto.DeckResponseDto;
import de.danielkoellgen.srscsdeckservice.controller.schedulerpreset.SchedulerPresetController;
import de.danielkoellgen.srscsdeckservice.controller.schedulerpreset.dto.SchedulerPresetRequestDto;
import de.danielkoellgen.srscsdeckservice.controller.schedulerpreset.dto.SchedulerPresetResponseDto;
import de.danielkoellgen.srscsdeckservice.domain.card.domain.ImageElement;
import de.danielkoellgen.srscsdeckservice.domain.card.domain.TextElement;
import de.danielkoellgen.srscsdeckservice.domain.card.repository.CardRepository;
import de.danielkoellgen.srscsdeckservice.domain.deck.repository.DeckRepository;
import de.danielkoellgen.srscsdeckservice.domain.schedulerpreset.repository.SchedulerPresetRepository;
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

    private final WebTestClient webTestClientDeck;
    private final WebTestClient webTestClientCard;
    private final WebTestClient webTestClientPreset;

    private final UserService userService;
    private final DeckRepository deckRepository;
    private final UserRepository userRepository;
    private final CardRepository cardRepository;
    private final SchedulerPresetRepository schedulerPresetRepository;

    private User user1;
    private User user2;

    @Autowired
    public DeckControllerIntegrationTest(DeckController deckController, CardController cardController,
            SchedulerPresetController schedulerPresetController, UserService userService, DeckRepository deckRepository,
            UserRepository userRepository, CardRepository cardRepository,
            SchedulerPresetRepository schedulerPresetRepository)
    {
        this.webTestClientDeck = WebTestClient.bindToController(deckController).build();
        this.webTestClientCard = WebTestClient.bindToController(cardController).build();
        this.webTestClientPreset = WebTestClient.bindToController(schedulerPresetController).build();
        this.userService = userService;
        this.deckRepository = deckRepository;
        this.userRepository = userRepository;
        this.cardRepository = cardRepository;
        this.schedulerPresetRepository = schedulerPresetRepository;
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
        cardRepository.deleteAll();
        schedulerPresetRepository.deleteAll();
    }

    @Test
    public void shouldAllowToCreateNewDecks() {
        // given
        DeckRequestDto requestDto = new DeckRequestDto(user1.getUserId(), "THKoeln");

        // when
        DeckResponseDto responseDto = webTestClientDeck.post().uri("/decks")
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
        webTestClientDeck.delete().uri("/decks/"+deckId)
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
        List<DeckResponseDto> fetchedDecks = webTestClientDeck.get().uri("/decks?user-id="+user1.getUserId())
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

    @Test
    public void shouldAllowToChangePresets() {
        // given
        DeckResponseDto deckDto = externallyCreateDeck(new DeckRequestDto(user1.getUserId(), "anyName"));
        CardResponseDto cardDto = externallyCreateDefaultCard(deckDto.deckId());
        SchedulerPresetResponseDto presetDto = externallyCreatePreset();

        // when
        webTestClientDeck.put().uri("/decks/"+deckDto.deckId()+"/scheduler-presets/"+presetDto.schedulerPresetId())
                .exchange()
                .expectStatus().isOk();

        // then
        DeckResponseDto fetchedDeck = fetchDeck(deckDto.deckId());
        assertThat(fetchedDeck.schedulerPresetId())
                .isEqualTo(presetDto.schedulerPresetId());

        // and then
        CardResponseDto fetchedCard = fetchExternalDefaultCard(cardDto.cardId());
        assertThat(fetchedCard.scheduler().presetId())
                .isEqualTo(presetDto.schedulerPresetId());
    }

    private @NotNull DeckResponseDto externallyCreateDeck(DeckRequestDto requestDto) {
        DeckResponseDto responseDto = webTestClientDeck.post().uri("/decks")
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
        return webTestClientDeck.get().uri("/decks/"+deckId)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectBody(DeckResponseDto.class)
                .returnResult().getResponseBody();
    }

    private @NotNull CardResponseDto externallyCreateDefaultCard(UUID deckId) {
        CardRequestDto requestDto = new CardRequestDto(deckId, "default",
                new HintDto(
                        List.of(
                                ContentElementDto.makeAsText(new TextElement("text 1")),
                                ContentElementDto.makeAsImage(new ImageElement("url 1"))
                        )
                ), new ViewDto(
                List.of(
                        ContentElementDto.makeAsText(new TextElement("text 2")),
                        ContentElementDto.makeAsImage(new ImageElement("url 2"))
                )
        ), new ViewDto(
                List.of(
                        ContentElementDto.makeAsText(new TextElement("text 3")),
                        ContentElementDto.makeAsImage(new ImageElement("url 3"))
                )
        ));

        // when
        CardResponseDto responseDto = webTestClientCard.post().uri("/cards")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(requestDto)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(CardResponseDto.class)
                .returnResult()
                .getResponseBody();
        assert responseDto != null;
        return responseDto;
    }

    private @NotNull CardResponseDto fetchExternalDefaultCard(UUID cardId) {
        CardResponseDto fetchedCard = webTestClientCard.get().uri("/cards/" + cardId)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(CardResponseDto.class)
                .returnResult()
                .getResponseBody();
        assert fetchedCard != null;
        return fetchedCard;
    }

    private @NotNull SchedulerPresetResponseDto externallyCreatePreset() {
        SchedulerPresetRequestDto requestDto = new SchedulerPresetRequestDto(
                user1.getUserId(), "AnyName", List.of(1000L, 3000L), List.of(500L), 8000L,
                1.8, 0.2, 0.05, -0.1, -0.3,
                0.2, -0.5
        );

        SchedulerPresetResponseDto responseDto = webTestClientPreset.post().uri("/scheduler-presets")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(requestDto)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(SchedulerPresetResponseDto.class)
                .returnResult().getResponseBody();
        assert responseDto != null;
        return responseDto;
    }
}
