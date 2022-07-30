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
        log.trace("Cloning Card...");

        Deck targetDeck = deckRepository.findById(targetDeckId).orElseThrow();
        log.debug("Fetched Target-Deck by id. {}", targetDeck);
        AbstractCard referenceCard = cardRepository.findById(referenceCardId).orElseThrow();
        log.debug("Fetched Reference-Deck by id. {}", referenceCard);

        SchedulerPreset preset = getPresetOrMakeDefault(targetDeck);
        AbstractCard clonedCard;
        switch (referenceCard.getClass().getSimpleName()) {
            case "DefaultCard" -> {
                DefaultCard newCard = DefaultCard.makeNewAsCloned(((DefaultCard) referenceCard), targetDeck, preset);
                clonedCard = newCard;
                log.debug("Cloned as 'DefaultCard'. {}", newCard);
            }
            case "TypingCard" -> {
                TypingCard newCard = TypingCard.makeNewAsCloned(((TypingCard) referenceCard), targetDeck, preset);
                clonedCard = newCard;
                log.debug("Cloned as 'TypingCard'. {}", newCard);
            }
            default -> {
                throw new RuntimeException("Encountered unexpectedly an unrecognized CardType that should not be in place"+
                        "while cloning a Card.");
            }
        }

        cardRepository.save(clonedCard);
        log.info("Card cloned into Deck '{}'.", targetDeck.getDeckName().getName());

        kafkaProducer.send(new CardCreated(
                getTraceIdOrEmptyString(), correlationId, new CardCreatedDto(clonedCard.getCardId(),
                targetDeckId, targetDeck.getUserId())));
    }

    public void cloneCardsToDeck(@NotNull UUID referencedDeckId, @NotNull UUID targetDeckId) {
        log.trace("Cloning Cards to Deck...");

        Deck targetDeck = deckRepository.findById(targetDeckId).get();
        log.debug("Target-Deck fetched by id. {}", targetDeck);
        SchedulerPreset preset = getPresetOrMakeDefault(targetDeck);
        List<AbstractCard> referencedCards = cardRepository
                .findAllByEmbeddedDeck_DeckIdAndIsActive(referencedDeckId, true);
        log.debug("Referenced-Cards fetched by status and deck. {} Cards fetched. {}", referencedCards.size(), referencedCards);

        List<AbstractCard> clonedCards = referencedCards.stream().map(card -> {
            return switch (card.getClass().getSimpleName()) {
                case "DefaultCard" -> DefaultCard
                        .makeNewAsCloned(((DefaultCard) card), targetDeck, preset);
                case "TypingCard" -> TypingCard
                        .makeNewAsCloned(((TypingCard) card), targetDeck, preset);
                default -> throw new RuntimeException("Card-type not implemented.");
            };
        }).toList();
        log.debug("{} Cards cloned. {}", clonedCards.size(), clonedCards);

        cardRepository.saveAll(clonedCards);
        log.info("{} Cards cloned to Deck '{}'.", clonedCards.size(), targetDeck.getDeckName().getName());

        clonedCards.forEach(card ->
                kafkaProducer.send(new CardCreated(getTraceIdOrEmptyString(), null, new CardCreatedDto(
                        card.getCardId(), targetDeckId, targetDeck.getUserId()))));
    }

    public DefaultCard createDefaultCard(@Nullable UUID correlationId,
            @NotNull UUID deckId, @Nullable Hint hint, @Nullable View frontView, @Nullable View backView) {
        log.trace("Creating DefaultCard...");

        Deck deck = deckRepository.findById(deckId).get();
        log.debug("Deck fetched by id. {}", deck);
        SchedulerPreset preset = getPresetOrMakeDefault(deck);
        DefaultCard card = DefaultCard.makeNew(deck, preset, hint, frontView, backView);
        log.debug("New DefaultCard: {}", card);

        cardRepository.save(card);
        log.info("DefaultCard created for {} in {}.", deck.getUsername().getUsername(), deck.getDeckName().getName());

        kafkaProducer.send(new CardCreated(getTraceIdOrEmptyString(), correlationId, new CardCreatedDto(
                card.getCardId(), deckId, deck.getUserId())));
        return card;
    }

    public DefaultCard overrideAsDefaultCard(@Nullable UUID correlationId,
            @NotNull UUID parentCardId, @Nullable Hint hint, @Nullable View frontView, @Nullable View backView) {
        log.trace("Overriding card as default-card...");

        AbstractCard parentCard = cardRepository.findById(parentCardId).get();
        log.debug("Parent-Card fetched by id: {}", parentCard);
        DefaultCard newCard = DefaultCard.makeNewAsOverridden(parentCard, hint, frontView, backView);
        log.debug("New default-card created from parent-card: {}", newCard);
        Deck deck = deckRepository.findById(parentCard.getEmbeddedDeck().getDeckId()).get();
        parentCard.disableCard();
        log.trace("Parent-Card disabled.");

        cardRepository.save(parentCard);
        cardRepository.save(newCard);
        log.info("Card overridden with new DefaultCard.");

        kafkaProducer.send(new CardOverridden(getTraceIdOrEmptyString(), correlationId,
                new CardOverriddenDto(
                        parentCardId, newCard.getCardId(), newCard.getEmbeddedDeck().getDeckId(), deck.getUserId())));
        return newCard;
    }

    public void overrideWithReferencedCard(@Nullable UUID correlationId,
            @NotNull UUID parentCardId, @NotNull UUID referenceCardId, @NotNull UUID deckId) {
        log.trace("Overriding Card with Reference-Card...");

        Deck deck = deckRepository.findById(deckId).get();
        log.debug("Deck fetched by id: {}", deck);
        AbstractCard referenceCard = cardRepository.findById(referenceCardId).get();
        AbstractCard parentCard = cardRepository.findById(parentCardId).get();

        AbstractCard newCard;
        switch (referenceCard.getClass().getSimpleName()) {
            case "DefaultCard" -> {
                newCard = DefaultCard.makeNewAsOverridden(
                        parentCard, deck, ((DefaultCard) referenceCard).getHint(),
                        ((DefaultCard) referenceCard).getFrontView(), ((DefaultCard) referenceCard).getBackView()
                );
                log.debug("Reference-Card: {}", (DefaultCard) referenceCard);
                log.debug("Parent-Card: {}", parentCard.getScheduler());
                log.debug("New-Card: {}", (DefaultCard) newCard);
            }
            case "TypingCard" -> {
                newCard = TypingCard.makeNewAsOverridden(parentCard, deck);
                log.debug("Reference-Card: {}", (TypingCard) referenceCard);
                log.debug("Parent-Card: {}", parentCard.getScheduler());
                log.debug("New-Card: {}", (TypingCard) newCard);
            }
            default -> throw new RuntimeException("Encountered unrecognized Class while overriding Card.");
        };
        parentCard.disableCard();
        log.trace("Disabled Parent-Card.");

        cardRepository.save(parentCard);
        cardRepository.save(newCard);
        log.info("Card overridden with Reference-Card to Deck '{}'.", deck.getDeckName().getName());

        kafkaProducer.send(new CardOverridden(getTraceIdOrEmptyString(), correlationId,
                new CardOverriddenDto(parentCardId, newCard.getCardId(), deckId, deck.getUserId())));
    }

    public void disableCard(@NotNull UUID cardId) {
        log.trace("Disabling Card...");

        AbstractCard card = cardRepository.findById(cardId).get();
        log.debug("Card fetched by id: {}", card);
        Deck deck = deckRepository.findById(card.getEmbeddedDeck().getDeckId()).get();

        card.disableCard();
        log.trace("Card disabled.");

        cardRepository.save(card);
        log.info("Card disabled.");

        kafkaProducer.send(new CardDisabled(getTraceIdOrEmptyString(), new CardDisabledDto(
                cardId, deck.getUserId())
        ));
    }

    public void reviewCard(@NotNull UUID cardId, @NotNull ReviewAction reviewAction) {
        log.trace("Reviewing Card...");

        AbstractCard card = cardRepository.findById(cardId).get();
        log.debug("Card fetched by id: {}", card.getScheduler());

        card.reviewCard(reviewAction);
        log.debug("Card reviewed as {}. Updated scheduler: {}", reviewAction, card.getScheduler());

        cardRepository.save(card);
        log.info("Card reviewed as {}.", reviewAction);
    }

    public void graduateCard(@NotNull UUID cardId) {
        log.trace("Graduating Card...");

        AbstractCard card = cardRepository.findById(cardId).get();
        log.debug("Card fetched by id: {}", card.getScheduler());

        card.graduateCard();
        log.debug("Card-Scheduler graduated. Updated Scheduler: {}", card.getScheduler());

        cardRepository.save(card);
        log.info("Card-Scheduler graduated.");
    }

    public void resetCardScheduler(@NotNull UUID cardId) {
        log.trace("Resetting Card-Scheduler...");

        AbstractCard card = cardRepository.findById(cardId).get();
        log.debug("Card fetched by id: {}", card.getScheduler());

        card.resetScheduler();
        log.debug("Card-Scheduler resetted. Updated Scheduler: {}", card.getScheduler());

        cardRepository.save(card);
        log.info("Card-Scheduler resetted.");
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
