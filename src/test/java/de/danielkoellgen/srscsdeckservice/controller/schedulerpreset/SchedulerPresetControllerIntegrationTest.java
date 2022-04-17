package de.danielkoellgen.srscsdeckservice.controller.schedulerpreset;

import de.danielkoellgen.srscsdeckservice.controller.card.CardController;
import de.danielkoellgen.srscsdeckservice.controller.card.dto.*;
import de.danielkoellgen.srscsdeckservice.controller.deck.DeckController;
import de.danielkoellgen.srscsdeckservice.controller.deck.dto.DeckRequestDto;
import de.danielkoellgen.srscsdeckservice.controller.deck.dto.DeckResponseDto;
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
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.List;
import java.util.UUID;

@SpringBootTest
public class SchedulerPresetControllerIntegrationTest {

    private final WebTestClient webTestClientDeck;
    private final WebTestClient webTestClientCard;
    private final WebTestClient webTestClientPreset;

    private final UserService userService;

    private final UserRepository userRepository;
    private final DeckRepository deckRepository;
    private final CardRepository cardRepository;
    private final SchedulerPresetRepository schedulerPresetRepository;

    private User user1;
    private User user2;
    private DeckResponseDto deck1;

    @Autowired
    public SchedulerPresetControllerIntegrationTest(DeckController deckController, CardController cardController,
            SchedulerPresetController schedulerPresetController, UserService userService, UserRepository userRepository,
            DeckRepository deckRepository, CardRepository cardRepository,
            SchedulerPresetRepository schedulerPresetRepository) {
        this.webTestClientDeck = WebTestClient.bindToController(deckController).build();
        this.webTestClientCard = WebTestClient.bindToController(cardController).build();
        this.webTestClientPreset = WebTestClient.bindToController(schedulerPresetController).build();
        this.userService = userService;
        this.userRepository = userRepository;
        this.deckRepository = deckRepository;
        this.cardRepository = cardRepository;
        this.schedulerPresetRepository = schedulerPresetRepository;
    }

    @BeforeEach
    public void setUp() throws Exception {
        user1 = userService.addNewExternallyCreatedUser(UUID.randomUUID(), UUID.randomUUID(), new Username("anyName"));
        user2 = userService.addNewExternallyCreatedUser(UUID.randomUUID(), UUID.randomUUID(), new Username("anyName2"));

        DeckRequestDto deckRequestDto = new DeckRequestDto(user1.getUserId(), "ANYNAME");
        deck1 = externallyCreateDeck(deckRequestDto);
    }

    @AfterEach
    public void cleanUp() {
        userRepository.deleteAll();
        deckRepository.deleteAll();
        cardRepository.deleteAll();
        schedulerPresetRepository.deleteAll();
    }

    @Test
    public void shouldAllowToCreateNewPresets() {
        // given
        SchedulerPresetRequestDto requestDto = new SchedulerPresetRequestDto(
                user1.getUserId(), "AnyName", List.of(1000L, 3000L), List.of(500L), 8000L,
                1.8, 0.2, 0.05, -0.1, -0.3,
                0.2, -0.5
        );

        // when
        SchedulerPresetResponseDto responseDto = webTestClientPreset.post().uri("/scheduler-presets")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(requestDto)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(SchedulerPresetResponseDto.class)
                .returnResult().getResponseBody();
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
}
