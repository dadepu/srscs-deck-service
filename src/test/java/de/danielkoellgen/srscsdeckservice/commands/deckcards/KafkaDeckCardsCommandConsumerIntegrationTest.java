package de.danielkoellgen.srscsdeckservice.commands.deckcards;

import com.fasterxml.jackson.core.JsonProcessingException;
import de.danielkoellgen.srscsdeckservice.commands.deckcards.dto.CreateDeckDto;
import de.danielkoellgen.srscsdeckservice.core.converter.ProducerEventToConsumerRecordConverter;
import de.danielkoellgen.srscsdeckservice.core.events.producer.CreateDeckCmd;
import de.danielkoellgen.srscsdeckservice.domain.card.repository.CardRepository;
import de.danielkoellgen.srscsdeckservice.domain.deck.domain.Deck;
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

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class KafkaDeckCardsCommandConsumerIntegrationTest {

    private final KafkaDeckCardsCommandConsumer kafkaDeckCardsCommandConsumer;

    private final UserService userService;

    private final UserRepository userRepository;
    private final DeckRepository deckRepository;
    private final CardRepository cardRepository;

    private final ProducerEventToConsumerRecordConverter converterToConsumerRecord;

    private User user;

    @Autowired
    public KafkaDeckCardsCommandConsumerIntegrationTest(KafkaDeckCardsCommandConsumer kafkaDeckCardsCommandConsumer,
            UserService userService, UserRepository userRepository, DeckRepository deckRepository,
            CardRepository cardRepository, ProducerEventToConsumerRecordConverter producerEventToConsumerRecordConverter) {
        this.kafkaDeckCardsCommandConsumer = kafkaDeckCardsCommandConsumer;
        this.userService = userService;
        this.userRepository = userRepository;
        this.deckRepository = deckRepository;
        this.cardRepository = cardRepository;
        this.converterToConsumerRecord = producerEventToConsumerRecordConverter;
    }

    @BeforeEach
    public void setUp() throws Exception {
        user = userService.addNewExternallyCreatedUser(
                UUID.randomUUID(), UUID.randomUUID(), new Username("dadepu")
        );
    }

    @AfterEach
    public void cleanUp() {
        cardRepository.deleteAll();
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
}
