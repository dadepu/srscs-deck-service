package de.danielkoellgen.srscsdeckservice.events.consumer.user;

import com.fasterxml.jackson.core.JsonProcessingException;
import de.danielkoellgen.srscsdeckservice.domain.user.application.UserService;
import de.danielkoellgen.srscsdeckservice.events.consumer.AbstractConsumerEvent;
import de.danielkoellgen.srscsdeckservice.events.consumer.user.dto.UserCreatedDto;
import de.danielkoellgen.srscsdeckservice.events.consumer.user.dto.UserDisabledDto;
import lombok.Getter;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.jetbrains.annotations.NotNull;

public class UserDisabled extends AbstractConsumerEvent {

    private final UserService userService;

    @Getter
    private final @NotNull UserDisabledDto payload;

    public UserDisabled(@NotNull UserService userService, @NotNull ConsumerRecord<String, String> event) throws JsonProcessingException {
        super(event);
        this.userService = userService;
        this.payload = UserDisabledDto.makeFromSerialization(event.value());
    }

    @Override
    public void execute() {
        userService.disableExternallyDisabledUser(transactionId, payload.userId());
    }

    @Override
    public String toString() {
        return "UserDisabled{" +
                "eventId=" + eventId +
                ", transactionId=" + transactionId +
                ", eventName='" + eventName + '\'' +
                ", occurredAt=" + occurredAt +
                ", receivedAt=" + receivedAt +
                ", topic='" + topic + '\'' +
                ", payload=" + payload +
                '}';
    }
}
