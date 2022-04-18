package de.danielkoellgen.srscsdeckservice.commands.deckcards;

import com.fasterxml.jackson.core.JsonProcessingException;
import de.danielkoellgen.srscsdeckservice.commands.deckcards.dto.CloneDeckDto;
import de.danielkoellgen.srscsdeckservice.commands.deckcards.dto.CreateDeckDto;
import de.danielkoellgen.srscsdeckservice.core.converter.ProducerEventToConsumerRecordConverter;
import de.danielkoellgen.srscsdeckservice.core.events.producer.CloneDeckCmd;
import de.danielkoellgen.srscsdeckservice.core.events.producer.CreateDeckCmd;
import de.danielkoellgen.srscsdeckservice.domain.card.application.CardService;
import de.danielkoellgen.srscsdeckservice.domain.card.domain.DefaultCard;
import de.danielkoellgen.srscsdeckservice.domain.card.repository.CardRepository;
import de.danielkoellgen.srscsdeckservice.domain.card.repository.DefaultCardRepository;
import de.danielkoellgen.srscsdeckservice.domain.deck.application.DeckService;
import de.danielkoellgen.srscsdeckservice.domain.deck.domain.Deck;
import de.danielkoellgen.srscsdeckservice.domain.deck.domain.DeckName;
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

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class KafkaDeckCardsCommandConsumerIntegrationTest {

    private final KafkaDeckCardsCommandConsumer kafkaDeckCardsCommandConsumer;

    private final UserService userService;
    private final DeckService deckService;
    private final CardService cardService;

    private final UserRepository userRepository;
    private final DeckRepository deckRepository;
    private final CardRepository cardRepository;
    private final DefaultCardRepository defaultCardRepository;

    private final ProducerEventToConsumerRecordConverter converterToConsumerRecord;

    private User user;
    private Deck deck;
    private List<DefaultCard> cards;

    @Autowired
    public KafkaDeckCardsCommandConsumerIntegrationTest(KafkaDeckCardsCommandConsumer kafkaDeckCardsCommandConsumer,
            UserService userService, DeckService deckService, CardService cardService, UserRepository userRepository,
            DeckRepository deckRepository, CardRepository cardRepository, DefaultCardRepository defaultCardRepository,
            ProducerEventToConsumerRecordConverter producerEventToConsumerRecordConverter) {
        this.kafkaDeckCardsCommandConsumer = kafkaDeckCardsCommandConsumer;
        this.userService = userService;
        this.deckService = deckService;
        this.cardService = cardService;
        this.userRepository = userRepository;
        this.deckRepository = deckRepository;
        this.cardRepository = cardRepository;
        this.defaultCardRepository = defaultCardRepository;
        this.converterToConsumerRecord = producerEventToConsumerRecordConverter;
    }

    @BeforeEach
    public void setUp() throws Exception {
        user = userService.addNewExternallyCreatedUser(
                UUID.randomUUID(), UUID.randomUUID(), new Username("dadepu")
        );
        deck = deckService.createNewDeck(
                UUID.randomUUID(), user.getUserId(), new DeckName("DECK1")
        );
        cards = List.of(
                cardService.createDefaultCard(UUID.randomUUID(), deck.getDeckId(), null, null, null),
                cardService.createDefaultCard(UUID.randomUUID(), deck.getDeckId(), null, null, null),
                cardService.createDefaultCard(UUID.randomUUID(), deck.getDeckId(), null, null, null)
        );
    }

    @AfterEach
    public void cleanUp() {
//        cardRepository.deleteAll();
        deckRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    public void shouldCreateDeckWhenReceivingCreateDeckCommand() throws Exception {
        // given
        CreateDeckCmd createDeckCmd = new CreateDeckCmd(UUID.randomUUID(), new CreateDeckDto(
                user.getUserId(), "THKoeln"
        ));

        // when
        kafkaDeckCardsCommandConsumer.receive(
                converterToConsumerRecord.convert(createDeckCmd)
        );

        // then
        assertThat(deckRepository.findDecksByEmbeddedUser_UserId(user.getUserId()))
                .hasSize(1);
    }

    @Test
    public void shouldCloneDeckWhenReceivingCloneDeckCommand() throws Exception {
        // given
        CloneDeckCmd cloneDeckCmd = new CloneDeckCmd(UUID.randomUUID(), new CloneDeckDto(
                deck.getDeckId(), user.getUserId(), "target"
        ));

        // when
        kafkaDeckCardsCommandConsumer.receive(
                converterToConsumerRecord.convert(cloneDeckCmd)
        );

        // then
        Deck clonedDeck = deckRepository.findDecksByEmbeddedUser_UserId(user.getUserId()).stream()
                .filter(deck -> deck.getDeckName().getName().equals("target"))
                .toList().get(0);

        // and then
        List<DefaultCard> clonedCards = defaultCardRepository.findAllByEmbeddedDeck_DeckId(clonedDeck.getDeckId());
        assertThat(clonedCards)
                .hasSize(3);
    }
}
