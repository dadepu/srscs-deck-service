package de.danielkoellgen.srscsdeckservice.domain.card.application;

import de.danielkoellgen.srscsdeckservice.domain.card.domain.*;
import de.danielkoellgen.srscsdeckservice.domain.card.repository.CardRepository;
import de.danielkoellgen.srscsdeckservice.domain.deck.domain.Deck;
import de.danielkoellgen.srscsdeckservice.domain.deck.repository.DeckRepository;
import de.danielkoellgen.srscsdeckservice.domain.schedulerpreset.application.SchedulerPresetService;
import de.danielkoellgen.srscsdeckservice.domain.schedulerpreset.domain.SchedulerPreset;
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

    private final Logger logger = LoggerFactory.getLogger(CardService.class);

    @Autowired
    public CardService(CardRepository cardRepository, DeckRepository deckRepository,
            SchedulerPresetService schedulerPresetService) {
        this.cardRepository = cardRepository;
        this.deckRepository = deckRepository;
        this.schedulerPresetService = schedulerPresetService;
    }

    public void createDefaultCard(@NotNull UUID transactionId, @NotNull UUID deckId, @Nullable Hint hint,
            @Nullable View frontView, @Nullable View backView) {
        Deck deck = deckRepository.findById(deckId).get();
        SchedulerPreset preset = (deck.getSchedulerPreset() != null ?
                deck.getSchedulerPreset() : schedulerPresetService.createTransientDefaultPreset(deck.getUserId()));
        DefaultCard card = new DefaultCard(deck, preset, hint, frontView, backView);
        cardRepository.save(card);

        logger.info("Card created for {} in {}. [tid={}, cardId={}, deckId={}]",
                deck.getUsername().getUsername(), deck.getDeckName().getName(), transactionId, card.getCardId(), deckId);
    }

    public void overrideAsDefaultCard(@NotNull UUID transactionId, @NotNull UUID parentCardId,
            @Nullable Hint hint, @Nullable View frontView, @Nullable View backView) {
        AbstractCard parentCard = cardRepository.findById(parentCardId).get();
        DefaultCard card = new DefaultCard(parentCard, hint, frontView, backView);
        parentCard.disableCard();

        cardRepository.save(parentCard);
        cardRepository.save(card);

        logger.info("Overrode card with DefaultCard. [tid={}, parentCardId={}, cardId={}]",
                transactionId, parentCardId, card.getCardId());
    }

    public void disableCard(@NotNull UUID transactionId, @NotNull UUID cardId) {
        AbstractCard card = cardRepository.findById(cardId).get();
        card.disableCard();
        cardRepository.save(card);
        logger.info("Card disabled. [tid={}, cardId={}]", transactionId, cardId);
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
