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

        User user = userRepository.findById(userId).orElseThrow();
        log.debug("Fetched user by id: {}", user);
        Deck deck = new Deck(user, deckName);
        log.debug("New deck created: {}", deck);

        deckRepository.save(deck);
        log.info("Deck '{}' created for '{}'.", deckName.getName(), user.getUsername().getUsername());

        kafkaProducer.send(new DeckCreated(getTraceIdOrEmptyString(), correlationId,
                new DeckCreatedDto(deck)));
        return deck;
    }

    public void cloneDeck(@Nullable UUID correlationId, @NotNull UUID referencedDeckId, @NotNull UUID userId,
            @NotNull DeckName deckName) {
        log.trace("Cloning deck...");

        User user = userRepository.findById(userId).orElseThrow();
        log.debug("Fetched user by id: {}", user);

        log.trace("Validating referenced Deck exists by id {}...", referencedDeckId);
        Deck referencedDeck = deckRepository.findById(referencedDeckId).orElseThrow();
        log.trace("Deck exists.");

        Deck newDeck = new Deck(user, deckName);
        log.trace("New Deck {} created for User {}: {}",
                newDeck.getDeckName().getName(), user.getUsername().getUsername(), newDeck);

        deckRepository.save(newDeck);
        log.info("Cloned Deck {} to new Deck {}. Copying Cards...",
                referencedDeck.getDeckName().getName(), newDeck.getDeckName().getName()
        );

        kafkaProducer.send(new DeckCreated(getTraceIdOrEmptyString(), correlationId, new DeckCreatedDto(newDeck)));
        cardService.cloneCardsToDeck(referencedDeckId, newDeck.getDeckId());
    }

    public void deleteDeck(@NotNull UUID deckId) {
        log.trace("Deleting Deck...");
        log.trace("Fetching Deck by id {}...", deckId);
        Deck deck = deckRepository.findById(deckId).orElseThrow();

        deck.disableDeck();
        log.debug("Deck disabled. isActive={}", deck.getIsActive());

        deckRepository.save(deck);
        log.trace("Saved disabled Deck.");
        log.info("Deck '{}' disabled.", deck.getDeckName().getName());

        kafkaProducer.send(new DeckDisabled(getTraceIdOrEmptyString(), new DeckDisabledDto(deck)));
    }

    public void changePreset(@NotNull UUID deckId, @NotNull UUID presetId) {
        log.trace("Changing Preset for Deck...");
        log.trace("Fetching Deck by id {}...", deckId);
        Deck deck = deckRepository.findById(deckId).orElseThrow();
        log.debug("{}", deck);

        log.trace("Fetching SchedulerPreset by id {}...", presetId);
        SchedulerPreset preset = schedulerPresetRepository.findById(presetId).orElseThrow();
        log.debug("{}", preset);

        deck.updateSchedulerPreset(preset);
        log.debug("Deck updated with Preset. Preset-Id is {}.", deck.getSchedulerPreset().getPresetId());
        deckRepository.save(deck);
        log.trace("Saved updated Deck.");

        log.trace("Updating Preset for all active Cards...");
        List<AbstractCard> cards = cardRepository
                .findAllByEmbeddedDeck_DeckIdAndIsActive(deckId, true);
        cards.forEach(element -> element.replaceSchedulerPreset(preset));
        if (!cards.isEmpty()) {
            log.debug("Card[0]: {}", cards.get(0).getScheduler());
        }
        cardRepository.saveAll(cards);
        log.trace("{} updated and saved.", cards.size());

        log.info("Deck {} updated with Preset {}.", deck.getDeckName().getName(), preset.getPresetName().getName());
    }

    private String getTraceIdOrEmptyString() {
        try {
            return tracer.currentSpan().context().traceId();
        } catch (Exception e) {
            return "";
        }
    }
}
