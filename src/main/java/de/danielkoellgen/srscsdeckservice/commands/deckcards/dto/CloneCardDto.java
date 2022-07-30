package de.danielkoellgen.srscsdeckservice.commands.deckcards.dto;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public record CloneCardDto(

    @NotNull UUID referencedCardId,

    @NotNull UUID targetDeckId

) {
    public static @NotNull CloneCardDto makeFromSerialization(@NotNull String serialized) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper().findAndRegisterModules();
        return mapper.readValue(serialized, CloneCardDto.class);
    }
}
