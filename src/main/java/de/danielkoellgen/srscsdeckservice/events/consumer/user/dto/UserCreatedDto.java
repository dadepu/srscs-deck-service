package de.danielkoellgen.srscsdeckservice.events.consumer.user.dto;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.danielkoellgen.srscsdeckservice.domain.user.domain.Username;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public record UserCreatedDto(

    @NotNull UUID userId,

    @NotNull String username

) {
    public static @NotNull UserCreatedDto makeFromSerialization(@NotNull String serialized) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper().findAndRegisterModules();
        return mapper.readValue(serialized, UserCreatedDto.class);
    }

    public @NotNull Username getUsername() {
        try {
            return new Username(username);
        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize username from event-payload.");
        }
    }
}
