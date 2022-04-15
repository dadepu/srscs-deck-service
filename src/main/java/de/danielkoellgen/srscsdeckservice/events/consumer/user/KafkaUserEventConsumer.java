package de.danielkoellgen.srscsdeckservice.events.consumer.user;

import com.fasterxml.jackson.core.JsonProcessingException;
import de.danielkoellgen.srscsdeckservice.domain.user.application.UserService;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

@Component
public class KafkaUserEventConsumer {

    private final UserService userService;

    @Autowired
    public KafkaUserEventConsumer(UserService userService) {
        this.userService = userService;
    }

    @KafkaListener(topics = {"cdc.users.0"})
    public void receive(@NotNull ConsumerRecord<String, String> event) throws JsonProcessingException {
        String eventName = getHeaderValue(event, "type");
        switch (eventName) {
            case "user-created"     -> processUserCreatedEvent(event);
            case "user-disabled"    -> processUserDisabledEvent(event);
            default -> throw new RuntimeException("Received event on 'cdc.users.0' of unknown type '"+eventName+"'.");
        }
    }

    private void processUserCreatedEvent(@NotNull ConsumerRecord<String, String> event) throws JsonProcessingException {
        UserCreated userCreated = new UserCreated(userService, event);
        userCreated.execute();
    }

    private void processUserDisabledEvent(@NotNull ConsumerRecord<String, String> event) throws JsonProcessingException {
        UserDisabled userDisabled = new UserDisabled(userService, event);
        userDisabled.execute();
    }

    public static String getHeaderValue(ConsumerRecord<String, String> event, String key) {
        return new String(event.headers().lastHeader(key).value(), StandardCharsets.US_ASCII);
    }
}
