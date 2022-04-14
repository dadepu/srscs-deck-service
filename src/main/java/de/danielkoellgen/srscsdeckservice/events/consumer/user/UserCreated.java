package de.danielkoellgen.srscsdeckservice.events.consumer.user;

import com.fasterxml.jackson.core.JsonProcessingException;
import de.danielkoellgen.srscsdeckservice.domain.user.application.UserService;
import de.danielkoellgen.srscsdeckservice.events.consumer.AbstractConsumerEvent;
import de.danielkoellgen.srscsdeckservice.events.consumer.user.dto.UserCreatedDto;
import lombok.Getter;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.jetbrains.annotations.NotNull;

public class UserCreated extends AbstractConsumerEvent {

    private final UserService userService;

    @Getter
    private final @NotNull UserCreatedDto payload;

    public UserCreated(@NotNull UserService userService, @NotNull ConsumerRecord<String, String> event) throws JsonProcessingException {
        super(event);
        this.userService = userService;
        this.payload = UserCreatedDto.makeFromSerialization(event.value());
    }

    @Override
    public void execute() {
        userService.addNewExternallyCreatedUser(transactionId, payload.userId(), payload.getUsername());
    }
}
