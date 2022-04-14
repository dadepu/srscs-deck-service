package de.danielkoellgen.srscsdeckservice.commands.deckcards.dto;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.danielkoellgen.srscsdeckservice.domain.deck.domain.DeckName;
import de.danielkoellgen.srscsdeckservice.domain.user.domain.Username;
import de.danielkoellgen.srscsdeckservice.events.consumer.user.dto.UserCreatedDto;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public record CreateDeckDto(

    @NotNull UUID userId,

    @NotNull String deckName

) {
    public static @NotNull CreateDeckDto makeFromSerialization(@NotNull String serialized) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper().findAndRegisterModules();
        return mapper.readValue(serialized, CreateDeckDto.class);
    }

    public @NotNull DeckName getDeckName() {
        try {
            return new DeckName(deckName);
        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize deckName from event-payload.");
        }
    }
}
