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
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class CardService {

    private final CardRepository cardRepository;
    private final DeckRepository deckRepository;
    private final SchedulerPresetService schedulerPresetService;
    private final KafkaProducer kafkaProducer;

    private final Logger logger = LoggerFactory.getLogger(CardService.class);

    @Autowired
    public CardService(CardRepository cardRepository, DeckRepository deckRepository,
            SchedulerPresetService schedulerPresetService, KafkaProducer kafkaProducer) {
        this.cardRepository = cardRepository;
        this.deckRepository = deckRepository;
        this.schedulerPresetService = schedulerPresetService;
        this.kafkaProducer = kafkaProducer;
    }

    public DefaultCard createDefaultCard(@NotNull UUID transactionId, @NotNull UUID deckId, @Nullable Hint hint,
            @Nullable View frontView, @Nullable View backView) {
        Deck deck = deckRepository.findById(deckId).get();
        SchedulerPreset preset = (deck.getSchedulerPreset() != null ?
                deck.getSchedulerPreset() : schedulerPresetService.createTransientDefaultPreset(deck.getUserId()));
        DefaultCard card = new DefaultCard(deck, preset, hint, frontView, backView);
        cardRepository.save(card);

        logger.info("Card created for {} in {}. [tid={}, cardId={}, deckId={}]",
                deck.getUsername().getUsername(), deck.getDeckName().getName(), transactionId, card.getCardId(), deckId);

        kafkaProducer.send(new CardCreated(transactionId, new CardCreatedDto(card.getCardId(), deckId)));
        return card;
    }

    public DefaultCard overrideAsDefaultCard(@NotNull UUID transactionId, @NotNull UUID parentCardId,
            @Nullable Hint hint, @Nullable View frontView, @Nullable View backView) {
        AbstractCard parentCard = cardRepository.findById(parentCardId).get();
        DefaultCard card = new DefaultCard(parentCard, hint, frontView, backView);
        parentCard.disableCard();

        cardRepository.save(parentCard);
        cardRepository.save(card);

        logger.info("Overrode card with DefaultCard. [tid={}, parentCardId={}, cardId={}]",
                transactionId, parentCardId, card.getCardId());

        kafkaProducer.send(new CardOverridden(transactionId,
                new CardOverriddenDto(parentCardId, card.getCardId(), card.getEmbeddedDeck().getDeckId())));

        return card;
    }

    public void overrideWithCard(@NotNull UUID transactionId, @NotNull UUID parentCardId, @NotNull UUID referenceCardId,
            @NotNull UUID deckId) {
        Deck deck = deckRepository.findById(deckId).get();
        AbstractCard parentCard = cardRepository.findById(parentCardId).get();
        AbstractCard referenceCard = cardRepository.findById(referenceCardId).get();

        AbstractCard newCard = switch (referenceCard.getClass().getSimpleName()) {
            case "DefaultCard" -> new DefaultCard(parentCard, deck, ((DefaultCard) referenceCard).getHint(),
                    ((DefaultCard) referenceCard).getFrontView(), ((DefaultCard) referenceCard).getBackView());
            default -> throw new RuntimeException("Encountered unrecognized Class while overriding Card.");
        };
        parentCard.disableCard();

        cardRepository.save(parentCard);
        cardRepository.save(newCard);
    }

    public void disableCard(@NotNull UUID transactionId, @NotNull UUID cardId) {
        AbstractCard card = cardRepository.findById(cardId).get();
        card.disableCard();
        cardRepository.save(card);

        logger.info("Card disabled. [tid={}, cardId={}]", transactionId, cardId);

        kafkaProducer.send(new CardDisabled(transactionId, new CardDisabledDto(cardId)));
    }

    public void reviewCard(@NotNull UUID transactionId, @NotNull UUID cardId, @NotNull ReviewAction reviewAction) {
        AbstractCard card = cardRepository.findById(cardId).get();
        card.reviewCard(reviewAction);
        cardRepository.save(card);

        logger.info("Card reviewed as {}. [tid={}, cardId={}]",
                reviewAction, transactionId, cardId);
    }

    public void graduateCard(@NotNull UUID transactionId, @NotNull UUID cardId) {
        AbstractCard card = cardRepository.findById(cardId).get();
        card.graduateCard();
        cardRepository.save(card);

        logger.info("Card graduated. [tid={}, cardId={}]", transactionId, cardId);
    }

    public void resetCardScheduler(@NotNull UUID transactionId, @NotNull UUID cardId) {
        AbstractCard card = cardRepository.findById(cardId).get();
        card.resetScheduler();
        cardRepository.save(card);

        logger.info("Card-Scheduler resetted. [tid={}, cardId={}]", transactionId, cardId);
    }
}
