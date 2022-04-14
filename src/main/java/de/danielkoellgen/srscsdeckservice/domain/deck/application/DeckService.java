package de.danielkoellgen.srscsdeckservice.domain.deck.application;

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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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

    private final Logger logger = LoggerFactory.getLogger(DeckService.class);

    @Autowired
    public DeckService(DeckRepository deckRepository, UserRepository userRepository, CardRepository cardRepository,
            SchedulerPresetRepository schedulerPresetRepository, KafkaProducer kafkaProducer) {
        this.deckRepository = deckRepository;
        this.userRepository = userRepository;
        this.cardRepository = cardRepository;
        this.schedulerPresetRepository = schedulerPresetRepository;
        this.kafkaProducer = kafkaProducer;
    }

    public Deck createNewDeck(UUID transactionId, UUID userId, DeckName deckName) {
        User user = userRepository.findById(userId).get();
        Deck deck = new Deck(user, deckName);
        deckRepository.save(deck);

        logger.info("New Deck '{}' created for '{}'. [tid={}, deckId={}]",
                deckName.getName(), user.getUsername().getUsername(), transactionId, deck.getDeckId());
        logger.trace("New Deck created. [{}]", deck);

        kafkaProducer.send(new DeckCreated(transactionId, new DeckCreatedDto(deck)));

        return deck;
    }

    public void deleteDeck(@NotNull UUID transactionId, @NotNull UUID deckId) {
        Deck deck = deckRepository.findById(deckId).get();
        deck.disableDeck();
        deckRepository.save(deck);

        logger.info("Disabled Deck. [tid={}, deckId={}]", transactionId, deckId);

        kafkaProducer.send(new DeckDisabled(transactionId, new DeckDisabledDto(deck)));
    }

    public void changePreset(@NotNull UUID transactionId, @NotNull UUID deckId, @NotNull UUID presetId) {
        Deck deck = deckRepository.findById(deckId).get();
        SchedulerPreset preset = schedulerPresetRepository.findById(presetId).get();
        deck.updateSchedulerPreset(preset);
        deckRepository.save(deck);

        List<AbstractCard> cards = cardRepository.findAllByEmbeddedDeck_DeckIdAndIsActive(deckId, true);
        cards.forEach(element -> element.replaceSchedulerPreset(preset));
        cardRepository.saveAll(cards);

        logger.info("Deck {} updated with Preset {}. [tid={}, deckId{}, presetId={}]",
                deck.getDeckName().getName(), preset.getPresetName().getName(), transactionId, deckId, presetId);
    }
}
