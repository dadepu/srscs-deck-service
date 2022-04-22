package de.danielkoellgen.srscsdeckservice.core.events.producer;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.danielkoellgen.srscsdeckservice.commands.deckcards.dto.CloneDeckDto;
import de.danielkoellgen.srscsdeckservice.domain.domainprimitive.EventDateTime;
import de.danielkoellgen.srscsdeckservice.events.producer.AbstractProducerEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.LocalDateTime;
import java.util.UUID;

public class CloneDeckCmd extends AbstractProducerEvent {

    private final @NotNull CloneDeckDto payloadDto;

    public static final String eventName = "clone-deck";

    public static final String eventTopic = "cmd.decks-cards.0";

    public CloneDeckCmd(@NotNull UUID transactionId, @Nullable UUID correlationId, @NotNull CloneDeckDto payloadDto) {
        super(UUID.randomUUID(), transactionId, correlationId, eventName, eventTopic,
                EventDateTime.makeFromLocalDateTime(LocalDateTime.now())
        );
        this.payloadDto = payloadDto;
    }

    @Override
    public @NotNull String getSerializedContent() {
        ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();
        try {
            return objectMapper.writeValueAsString(payloadDto);
        } catch (Exception e) {
            throw new RuntimeException("ObjectMapper conversion failed.");
        }
    }
}
