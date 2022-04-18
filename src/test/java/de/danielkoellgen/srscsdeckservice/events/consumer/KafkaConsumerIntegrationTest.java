package de.danielkoellgen.srscsdeckservice.events.consumer;

import de.danielkoellgen.srscsdeckservice.core.events.producer.UserCreatedProd;
import de.danielkoellgen.srscsdeckservice.domain.user.domain.User;
import de.danielkoellgen.srscsdeckservice.domain.user.domain.Username;
import de.danielkoellgen.srscsdeckservice.domain.user.repository.UserRepository;
import de.danielkoellgen.srscsdeckservice.events.consumer.user.dto.UserCreatedDto;
import de.danielkoellgen.srscsdeckservice.events.producer.KafkaProducer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class KafkaConsumerIntegrationTest {

    private final KafkaProducer kafkaProducer;

    private final UserRepository userRepository;

    private UUID userId;
    private Username username;

    @Autowired
    public KafkaConsumerIntegrationTest(KafkaProducer kafkaProducer, UserRepository userRepository) {
        this.kafkaProducer = kafkaProducer;
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
    @Disabled
    public void shouldConsumeLiveEvent() throws Exception {
        // given
        UserCreatedProd userCreated = new UserCreatedProd(UUID.randomUUID(), new UserCreatedDto(
                userId, username.getUsername()
        ));

        // when
        kafkaProducer.send(userCreated);
        Thread.sleep(500);

        // then
        assertThat(userRepository.existsById(userId))
                .isTrue();
    }
}
