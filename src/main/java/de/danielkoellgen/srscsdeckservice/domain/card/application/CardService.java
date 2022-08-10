package de.danielkoellgen.srscsdeckservice.domain.card.application;

import de.danielkoellgen.srscsdeckservice.domain.card.domain.*;
import de.danielkoellgen.srscsdeckservice.domain.card.repository.CardRepository;
import de.danielkoellgen.srscsdeckservice.domain.deck.domain.Deck;
import de.danielkoellgen.srscsdeckservice.domain.deck.repository.DeckRepository;
import de.danielkoellgen.srscsdeckservice.domain.schedulerpreset.application.SchedulerPresetService;
import de.danielkoellgen.srscsdeckservice.domain.schedulerpreset.domain.SchedulerPreset;
import de.danielkoellgen.srscsdeckservice.events.producer.KafkaProducer;
import de.danielkoellgen.srscsdeckservice.events.producer.card.CardCreated;
import de.danielkoellgen.srscsdeckservice.events.producer.card.CardDisabled;
import de.danielkoellgen.srscsdeckservice.events.producer.card.CardOverridden;
import de.danielkoellgen.srscsdeckservice.events.producer.card.dto.CardCreatedDto;
import de.danielkoellgen.srscsdeckservice.events.producer.card.dto.CardDisabledDto;
import de.danielkoellgen.srscsdeckservice.events.producer.card.dto.CardOverriddenDto;
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
public class CardService {

    private final CardRepository cardRepository;
    private final DeckRepository deckRepository;
    private final SchedulerPresetService schedulerPresetService;
    private final KafkaProducer kafkaProducer;

    @Autowired
    private Tracer tracer;

    private final Logger log = LoggerFactory.getLogger(CardService.class);

    @Autowired
    public CardService(CardRepository cardRepository, DeckRepository deckRepository,
            SchedulerPresetService schedulerPresetService, KafkaProducer kafkaProducer) {
        this.cardRepository = cardRepository;
        this.deckRepository = deckRepository;
        this.schedulerPresetService = schedulerPresetService;
        this.kafkaProducer = kafkaProducer;
    }

    public void cloneCard(@Nullable UUID correlationId, @NotNull UUID referenceCardId,
            @NotNull UUID targetDeckId) {
        log.trace("Cloning Reference-Card '{}' to Deck '{}'...", referenceCardId, targetDeckId);

        log.trace("Fetching Deck by id '{}'...", targetDeckId);
        Deck targetDeck = deckRepository.findById(targetDeckId).orElseThrow();
        log.debug("Fetched Deck: {}", targetDeck);
        log.trace("Fetching Reference-Card by id '{}'...", referenceCardId);
        AbstractCard referenceCard = cardRepository.findById(referenceCardId).orElseThrow();
        log.trace("Reference-Card fetched.");

        SchedulerPreset preset = getPresetOrMakeDefault(targetDeck);
        AbstractCard clonedCard;
        log.trace("Clone Card...");
        switch (referenceCard.getClass().getSimpleName()) {
            case "DefaultCard" -> {
                DefaultCard newCard = DefaultCard.makeNewAsCloned(((DefaultCard) referenceCard), targetDeck, preset);
                clonedCard = newCard;
                log.debug("Cloned as 'DefaultCard': {}", newCard);
            }
            case "TypingCard" -> {
                TypingCard newCard = TypingCard.makeNewAsCloned(((TypingCard) referenceCard), targetDeck, preset);
                clonedCard = newCard;
                log.debug("Cloned as 'TypingCard': {}", newCard);
            }
            default -> {
                throw new RuntimeException("Encountered unexpectedly an unrecognized CardType that should not be in place"+
                        "while cloning a Card.");
            }
        }

        cardRepository.save(clonedCard);
        log.info("Reference-Card cloned into Deck '{}'.", targetDeck.getDeckName().getName());

        kafkaProducer.send(new CardCreated(
                getTraceIdOrEmptyString(), correlationId, new CardCreatedDto(clonedCard.getCardId(),
                targetDeckId, targetDeck.getUserId())));
    }

    public void cloneCardsToDeck(@NotNull UUID referencedDeckId, @NotNull UUID targetDeckId) {
        log.trace("Cloning Cards from Deck '{}' to Deck '{}'...", referencedDeckId, targetDeckId);

        log.trace("Fetching Target-Deck by id '{}'...", targetDeckId);
        Deck targetDeck = deckRepository.findById(targetDeckId).orElseThrow();
        log.debug("Fetched Target-Deck: {}", targetDeck);
        SchedulerPreset preset = getPresetOrMakeDefault(targetDeck);
        log.trace("Fetching all active Cards from Reference-Deck '{}'...", referencedDeckId);
        List<AbstractCard> referencedCards = cardRepository
                .findAllByEmbeddedDeck_DeckIdAndIsActive(referencedDeckId, true);
        log.debug("{} Reference-Cards fetched.", referencedCards.size());

        log.trace("Cloning Cards...");
        List<AbstractCard> clonedCards = referencedCards
                .stream()
                .map(card -> {
                    return switch (card.getClass().getSimpleName()) {
                        case "DefaultCard" -> DefaultCard
                                .makeNewAsCloned(((DefaultCard) card), targetDeck, preset);
                        case "TypingCard" -> TypingCard
                                .makeNewAsCloned(((TypingCard) card), targetDeck, preset);
                        default -> throw new RuntimeException("Card-type not implemented.");
                    };
                })
                .toList();
        log.debug("{} Cards cloned.", clonedCards.size());

        cardRepository.saveAll(clonedCards);
        log.info("{} Cards cloned to Deck '{}'.", clonedCards.size(), targetDeck.getDeckName());

        clonedCards.forEach(card ->
                kafkaProducer.send(new CardCreated(getTraceIdOrEmptyString(), null,
                        new CardCreatedDto(card.getCardId(), targetDeckId, targetDeck.getUserId()))));
    }

    public DefaultCard createDefaultCard(@Nullable UUID correlationId,
            @NotNull UUID deckId, @Nullable Hint hint, @Nullable View frontView, @Nullable View backView) {
        log.trace("Creating new DefaultCard for Deck '{}'...", deckId);

        log.trace("Fetch Deck by id '{}'...", deckId);
        Deck deck = deckRepository.findById(deckId).orElseThrow();
        log.debug("Fetched Deck: {}", deck);
        SchedulerPreset preset = getPresetOrMakeDefault(deck);
        log.trace("Make new DefaultCard...");
        DefaultCard card = DefaultCard.makeNew(deck, preset, hint, frontView, backView);

        cardRepository.save(card);
        log.info("New Default-Card successfully created for '{}' in Deck '{}'.",
                deck.getUsername(), deck.getDeckName());
        log.debug("New Default-Card: {}", card);

        kafkaProducer.send(new CardCreated(getTraceIdOrEmptyString(), correlationId, new CardCreatedDto(
                card.getCardId(), deckId, deck.getUserId())));
        return card;
    }

    public DefaultCard overrideAsDefaultCard(@Nullable UUID correlationId,
            @NotNull UUID parentCardId, @Nullable Hint hint, @Nullable View frontView, @Nullable View backView) {
        log.trace("Overriding Parent-Card as Default-Card...");

        log.trace("Fetching Parent-Card by id '{}'...", parentCardId);
        AbstractCard parentCard = cardRepository.findById(parentCardId).orElseThrow();
        log.debug("Fetched Parent-Card: {}", parentCard);
        UUID deckId = parentCard.getEmbeddedDeck().getDeckId();
        log.trace("Fetching Deck by id '{}'...", deckId);
        Deck deck = deckRepository.findById(deckId).orElseThrow();
        log.debug("Fetched Deck: {}", deck);

        log.trace("Making new Default-Card from Parent-Card with new content...");
        DefaultCard newCard = DefaultCard.makeNewAsOverridden(parentCard, hint, frontView, backView);
        log.debug("New updated Card: {}", newCard);

        log.trace("Disabling Parent-Card...");
        parentCard.disableCard();

        cardRepository.save(parentCard);
        cardRepository.save(newCard);
        log.info("Parent-Card successfully overridden with new DefaultCard.");

        kafkaProducer.send(new CardOverridden(getTraceIdOrEmptyString(), correlationId,
                new CardOverriddenDto(
                        parentCardId, newCard.getCardId(), newCard.getEmbeddedDeck().getDeckId(),
                        deck.getUserId())));
        return newCard;
    }

    public void overrideWithReferencedCard(@Nullable UUID correlationId,
            @NotNull UUID parentCardId, @NotNull UUID referenceCardId, @NotNull UUID deckId) {
        log.trace("Overriding Parent-Card '{}' with Reference-Card '{}'...",
                parentCardId, referenceCardId);

        log.trace("Fetching Deck by id '{}'...", deckId);
        Deck deck = deckRepository.findById(deckId).orElseThrow();
        log.debug("Deck fetched by id: {}", deck);
        log.trace("Fetching Parent-Card by id '{}'...", parentCardId);
        AbstractCard parentCard = cardRepository.findById(parentCardId).orElseThrow();
        log.trace("Fetching Reference-Card by id '{}'...", referenceCardId);
        AbstractCard referenceCard = cardRepository.findById(referenceCardId).orElseThrow();

        AbstractCard newCard;
        switch (referenceCard.getClass().getSimpleName()) {
            case "DefaultCard" -> {
                newCard = DefaultCard.makeNewAsOverridden(
                        parentCard, deck, ((DefaultCard) referenceCard).getHint(),
                        ((DefaultCard) referenceCard).getFrontView(), ((DefaultCard) referenceCard).getBackView()
                );
                log.debug("Parent-Card: {}", parentCard.getScheduler());
                log.debug("Reference-Card: {}", (DefaultCard) referenceCard);
                log.debug("New-Card: {}", (DefaultCard) newCard);
            }
            case "TypingCard" -> {
                newCard = TypingCard.makeNewAsOverridden(parentCard, deck);
                log.debug("Parent-Card: {}", parentCard.getScheduler());
                log.debug("Reference-Card: {}", (TypingCard) referenceCard);
                log.debug("New-Card: {}", (TypingCard) newCard);
            }
            default -> throw new RuntimeException("Encountered unrecognized Class while overriding Card.");
        };
        log.trace("Disabling Parent-Card...");
        parentCard.disableCard();

        cardRepository.save(parentCard);
        cardRepository.save(newCard);
        log.info("Parent-Card successfully overridden with Reference-Card to Deck '{}'.",
                deck.getDeckName().getName());

        kafkaProducer.send(new CardOverridden(getTraceIdOrEmptyString(), correlationId,
                new CardOverriddenDto(parentCardId, newCard.getCardId(), deckId, deck.getUserId())));
    }

    public void disableCard(@NotNull UUID cardId) {
        log.trace("Disabling Card '{}'...", cardId);

        log.trace("Fetching Card by id '{}'...", cardId);
        AbstractCard card = cardRepository.findById(cardId).orElseThrow();
        log.debug("Fetched Card: {}", card);

        UUID deckId = card.getEmbeddedDeck().getDeckId();
        log.trace("Fetching Deck by id '{}'...", deckId);
        Deck deck = deckRepository.findById(deckId).orElseThrow();
        log.debug("Fetched Deck: {}", deck);

        card.disableCard();
        cardRepository.save(card);
        log.info("Card successfully disabled.");
        log.debug("Updated Card: {}", card);

        kafkaProducer.send(new CardDisabled(getTraceIdOrEmptyString(), new CardDisabledDto(
                cardId, deck.getUserId())));
    }

    public void reviewCard(@NotNull UUID cardId, @NotNull ReviewAction reviewAction) {
        log.trace("Reviewing Card '{}' as '{}'...", cardId, reviewAction);

        log.trace("Fetching Card by id '{}'...", cardId);
        AbstractCard card = cardRepository.findById(cardId).orElseThrow();
        log.debug("Fetched Card: {}", card.getScheduler());

        card.reviewCard(reviewAction);
        cardRepository.save(card);
        log.info("Card successfully reviewed as {}.", reviewAction);
        log.debug("Updated scheduler: {}", card.getScheduler());
    }

    public void graduateCard(@NotNull UUID cardId) {
        log.trace("Graduating Card '{}'...", cardId);

        log.trace("Fetching Card by id '{}'...", cardId);
        AbstractCard card = cardRepository.findById(cardId).orElseThrow();
        log.debug("Fetched Card-Scheduler: {}", card.getScheduler());

        card.graduateCard();
        cardRepository.save(card);
        log.info("Card-Scheduler successfully graduated.");
        log.debug("Updated Scheduler: {}", card.getScheduler());
    }

    public void resetCardScheduler(@NotNull UUID cardId) {
        log.trace("Resetting Card-Scheduler for Card '{}'...", cardId);

        log.trace("Fetching Card by id '{}'...", cardId);
        AbstractCard card = cardRepository.findById(cardId).orElseThrow();
        log.debug("Fetched Card-Scheduler: {}", card.getScheduler());

        card.resetScheduler();
        cardRepository.save(card);
        log.info("Card-Scheduler successfully resetted.");
        log.debug("Updated Card-Scheduler: {}", card.getScheduler());
    }

    private String getTraceIdOrEmptyString() {
        try {
            return tracer.currentSpan().context().traceId();
        } catch (Exception e) {
            return "";
        }
    }

    private @NotNull SchedulerPreset getPresetOrMakeDefault(@NotNull Deck deck) {
        if (deck.getSchedulerPreset() != null) {
            SchedulerPreset preset = deck.getSchedulerPreset();
            log.trace("Preset '{}' fetched from Deck.", preset.getPresetName());
            return preset;
        } else {
            log.trace("Transient Default-Preset created.");
            return schedulerPresetService.createTransientDefaultPreset(deck.getUserId());
        }
    }
}
