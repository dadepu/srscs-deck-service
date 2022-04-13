package de.danielkoellgen.srscsdeckservice.domain.card.application;

import de.danielkoellgen.srscsdeckservice.domain.card.domain.DefaultCard;
import de.danielkoellgen.srscsdeckservice.domain.card.domain.Hint;
import de.danielkoellgen.srscsdeckservice.domain.card.domain.View;
import de.danielkoellgen.srscsdeckservice.domain.card.repository.CardRepository;
import de.danielkoellgen.srscsdeckservice.domain.deck.application.DeckService;
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
                deck.getSchedulerPreset() : schedulerPresetService.makeTransientDefault(deck.getUserId()));
        DefaultCard card = new DefaultCard(deck, preset, hint, frontView, backView);
        cardRepository.save(card);

        logger.info("Card created for {} in {}. [tid={}, cardId={}, deckId={}]",
                deck.getUsername().getUsername(), deck.getDeckName().getName(), transactionId, card.getCardId(), deckId);
    }
}
