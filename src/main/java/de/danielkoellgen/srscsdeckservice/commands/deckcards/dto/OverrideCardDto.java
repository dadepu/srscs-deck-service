package de.danielkoellgen.srscsdeckservice.commands.deckcards.dto;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public record OverrideCardDto(

    @NotNull UUID deckId,

    @NotNull UUID overriddenCardId,

    @NotNull UUID referencedCardId

) {
    public static @NotNull OverrideCardDto makeFromSerialization(@NotNull String serialized) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper().findAndRegisterModules();
        return mapper.readValue(serialized, OverrideCardDto.class);
    }
}
