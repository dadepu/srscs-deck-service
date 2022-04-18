package de.danielkoellgen.srscsdeckservice.events.consumer;

import de.danielkoellgen.srscsdeckservice.core.events.producer.UserCreatedProd;
import de.danielkoellgen.srscsdeckservice.domain.user.application.UserService;
import de.danielkoellgen.srscsdeckservice.domain.user.domain.User;
import de.danielkoellgen.srscsdeckservice.domain.user.domain.Username;
import de.danielkoellgen.srscsdeckservice.domain.user.repository.UserRepository;
import de.danielkoellgen.srscsdeckservice.events.consumer.user.KafkaUserEventConsumer;
import de.danielkoellgen.srscsdeckservice.events.consumer.user.UserCreated;
import de.danielkoellgen.srscsdeckservice.events.consumer.user.dto.UserCreatedDto;
import de.danielkoellgen.srscsdeckservice.events.producer.ProducerEvent;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class KafkaUserEventConsumerIntegrationTest {

    private final KafkaUserEventConsumer kafkaUserEventConsumer;

    private final UserService userService;

    private final UserRepository userRepository;

    private UUID userId;
    private Username username;

    @Autowired
    public KafkaUserEventConsumerIntegrationTest(KafkaUserEventConsumer kafkaUserEventConsumer, UserService userService,
            UserRepository userRepository) {
        this.kafkaUserEventConsumer = kafkaUserEventConsumer;
        this.userService = userService;
        this.userRepository = userRepository;
    }

    @BeforeEach
    public void setUp() throws Exception {
        userId = UUID.randomUUID();
        username = new Username("dadepu");
    }

    @AfterEach
    public void cleanUp() {
        userRepository.deleteAll();
    }

    @Test
    public void shouldCreateUserWhenReceivingUserCreatedEvent() throws Exception {
        // given
        UserCreatedProd userCreatedProd = new UserCreatedProd(
                UUID.randomUUID(), new UserCreatedDto(userId, username.getUsername())
        );

        // when
        kafkaUserEventConsumer.receive(mapToConsumerRecord(userCreatedProd));
        
        // then
        User fetchedUser = userRepository.findById(userId).orElseThrow();
        assertThat(fetchedUser.getUsername())
                .isEqualTo(username);
    }

    private static ConsumerRecord<String, String> mapToConsumerRecord(ProducerEvent event) {
        ConsumerRecord<String, String> record = new ConsumerRecord<>(
                event.getTopic(), 1, 1L, "", event.getSerializedContent()
        );
        record.headers().add(new RecordHeader("eventId", event.getEventId().toString().getBytes()));
        record.headers().add(new RecordHeader("transactionId", event.getTransactionId().toString().getBytes()));
        record.headers().add(new RecordHeader("timestamp", event.getOccurredAt().getFormatted().getBytes()));
        record.headers().add(new RecordHeader("type", event.getEventName().getBytes()));
        return record;
    }
}
