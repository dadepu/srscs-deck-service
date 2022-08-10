package de.danielkoellgen.srscsdeckservice.domain.deck.application;

import de.danielkoellgen.srscsdeckservice.domain.card.application.CardService;
import de.danielkoellgen.srscsdeckservice.domain.card.domain.AbstractCard;
import de.danielkoellgen.srscsdeckservice.domain.card.repository.CardRepository;
import de.danielkoellgen.srscsdeckservice.domain.deck.domain.Deck;
import de.danielkoellgen.srscsdeckservice.domain.deck.repository.DeckRepository;
import de.danielkoellgen.srscsdeckservice.domain.deck.domain.DeckName;
import de.danielkoellgen.srscsdeckservice.domain.schedulerpreset.domain.SchedulerPreset;
import de.danielkoellgen.srscsdeckservice.domain.schedulerpreset.repository.SchedulerPresetRepository;
import de.danielkoellgen.srscsdeckservice.domain.user.domain.User;
import de.danielkoellgen.srscsdeckservice.domain.user.repository.UserRepository;
import de.danielkoellgen.srscsdeckservice.events.producer.KafkaProducer;
import de.danielkoellgen.srscsdeckservice.events.producer.deck.DeckCreated;
import de.danielkoellgen.srscsdeckservice.events.producer.deck.DeckDisabled;
import de.danielkoellgen.srscsdeckservice.events.producer.deck.dto.DeckCreatedDto;
import de.danielkoellgen.srscsdeckservice.events.producer.deck.dto.DeckDisabledDto;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.sleuth.Tracer;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class DeckService {

    private final DeckRepository deckRepository;
    private final UserRepository userRepository;
    private final CardRepository cardRepository;
    private final SchedulerPresetRepository schedulerPresetRepository;
    private final KafkaProducer kafkaProducer;
    private final CardService cardService;

    @Autowired
    private Tracer tracer;

    private final Logger log = LoggerFactory.getLogger(DeckService.class);

    @Autowired
    public DeckService(DeckRepository deckRepository, UserRepository userRepository,
            CardRepository cardRepository, SchedulerPresetRepository schedulerPresetRepository,
            KafkaProducer kafkaProducer, CardService cardService) {
        this.deckRepository = deckRepository;
        this.userRepository = userRepository;
        this.cardRepository = cardRepository;
        this.schedulerPresetRepository = schedulerPresetRepository;
        this.kafkaProducer = kafkaProducer;
        this.cardService = cardService;
    }

    public Deck createNewDeck(@Nullable UUID correlationId, @NotNull UUID userId,
            @NotNull DeckName deckName) {
        log.trace("Creating new Deck '{}'...", deckName.getName());
        log.trace("Fetching User by id '{}'...", userId);
        User user = userRepository.findById(userId).orElseThrow();
        log.debug("Fetched User: {}", user);
        Deck deck = new Deck(user, deckName);

        deckRepository.save(deck);
        log.info("New Deck '{}' created for '{}'.", deckName.getName(),
                user.getUsername().getUsername());
        log.debug("New Deck: {}", deck);

        kafkaProducer.send(new DeckCreated(getTraceIdOrEmptyString(), correlationId,
                new DeckCreatedDto(deck)));
        return deck;
    }

    public void cloneDeck(@Nullable UUID correlationId, @NotNull UUID referencedDeckId,
            @NotNull UUID userId, @NotNull DeckName deckName) {
        log.trace("Cloning Reference-Deck '{}' for User '{}'...", referencedDeckId, userId);
        log.trace("Fetching User by id '{}'...", userId);
        User user = userRepository.findById(userId).orElseThrow();
        log.debug("Fetched User: {}", user);

        log.trace("Fetching Reference-Deck by id '{}'...", referencedDeckId);
        Deck referencedDeck = deckRepository.findById(referencedDeckId).orElseThrow();
        log.trace("Reference-Deck: {}", referencedDeck);

        Deck clonedDeck = new Deck(user, deckName);
        deckRepository.save(clonedDeck);
        log.info("Reference-Deck '{}' successfully cloned to new Deck '{}'. Copying Cards...",
                referencedDeck.getDeckName(), clonedDeck.getDeckName());
        log.debug("Cloned Deck: {}", clonedDeck);

        kafkaProducer.send(new DeckCreated(getTraceIdOrEmptyString(), correlationId,
                new DeckCreatedDto(clonedDeck)));
        cardService.cloneCardsToDeck(referencedDeckId, clonedDeck.getDeckId());
    }

    public void deleteDeck(@NotNull UUID deckId) {
        log.trace("Deleting Deck '{}'...", deckId);
        log.trace("Fetching Deck by id '{}'...", deckId);
        Deck deck = deckRepository.findById(deckId).orElseThrow();
        log.debug("Fetched Deck: {}", deck);

        deck.disableDeck();
        deckRepository.save(deck);
        log.info("Deck '{}' successfully disabled.", deck.getDeckName().getName());
        log.debug("Disabled Deck: {}", deck);

        kafkaProducer.send(new DeckDisabled(getTraceIdOrEmptyString(), new DeckDisabledDto(deck)));
    }

    public void changePreset(@NotNull UUID deckId, @NotNull UUID presetId) {
        log.trace("Replacing Deck's '{}' Preset with '{}'...", deckId, presetId);
        log.trace("Fetching Deck by id '{}'...", deckId);
        Deck deck = deckRepository.findById(deckId).orElseThrow();
        log.debug("Fetched Deck: {}", deck);

        log.trace("Fetching SchedulerPreset by id '{}'...", presetId);
        SchedulerPreset preset = schedulerPresetRepository.findById(presetId).orElseThrow();
        log.debug("Fetched Preset: {}", preset);

        deck.updateSchedulerPreset(preset);
        deckRepository.save(deck);
        log.info("Deck '{}' successfully updated with Preset '{}'.", deck.getDeckName(),
                preset.getPresetName());
        log.debug("Updated Deck: {}", deck);

        log.trace("Updating Preset for all active Cards in Deck...");
        log.trace("Fetching Cards by deckId '{}' and isActive 'true'.", deckId);
        List<AbstractCard> cards = cardRepository
                .findAllByEmbeddedDeck_DeckIdAndIsActive(deckId, true);
        log.debug("{} Cards fetched.", cards.size());
        cards.forEach(element -> element.replaceSchedulerPreset(preset));
        log.debug("Updated Cards: {}", cards);

        cardRepository.saveAll(cards);
        log.info("{} active Cards updated with new Preset '{}'.", cards.size(),
                preset.getPresetName());
    }

    private String getTraceIdOrEmptyString() {
        try {
            return tracer.currentSpan().context().traceId();
        } catch (Exception e) {
            return "";
        }
    }
}
