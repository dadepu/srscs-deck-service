package de.danielkoellgen.srscsdeckservice.domain.card.repository;

import de.danielkoellgen.srscsdeckservice.domain.card.domain.AbstractCard;
import de.danielkoellgen.srscsdeckservice.domain.card.domain.DefaultCard;
import de.danielkoellgen.srscsdeckservice.domain.card.domain.TypingCard;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

@Component
@Scope("singleton")
public class CardRepositoryImpl implements CardRepository {

    private final DefaultCardRepository defaultCardRepository;
    private final TypingCardRepository typingCardRepository;

    @Autowired
    public CardRepositoryImpl(DefaultCardRepository defaultCardRepository, TypingCardRepository typingCardRepository) {
        this.defaultCardRepository = defaultCardRepository;
        this.typingCardRepository = typingCardRepository;
    }

    @Override
    public @NotNull Optional<AbstractCard> findById(@NotNull UUID cardId) {
        Optional<DefaultCard> defaultCard = defaultCardRepository.findById(cardId);
        if (defaultCard.isPresent()) {
            return Optional.of(defaultCard.get());
        }
        Optional<TypingCard> typingCard = typingCardRepository.findById(cardId);
        if (typingCard.isPresent()) {
            return Optional.of(typingCard.get());
        }
        return Optional.empty();
    }

    @Override
    public void save(@NotNull AbstractCard card) {
        if (card instanceof DefaultCard) {
            defaultCardRepository.save((DefaultCard) card);
        }
        if (card instanceof TypingCard) {
            typingCardRepository.save((TypingCard) card);
        }
    }

    @Override
    public void saveAll(@NotNull List<AbstractCard> cards) {
        List<DefaultCard> defaultCards = cards.stream()
                .filter(card -> card instanceof DefaultCard)
                .map(card -> (DefaultCard) card)
                .toList();
        defaultCardRepository.saveAll(defaultCards);
        List<TypingCard> typingCards = cards.stream()
                .filter(card -> card instanceof TypingCard)
                .map(card -> (TypingCard) card)
                .toList();
        typingCardRepository.saveAll(typingCards);
    }

    @Override
    public void deleteAll() {
        typingCardRepository.deleteAll();
        defaultCardRepository.deleteAll();
    }

    @Override
    public List<AbstractCard> findAllByEmbeddedDeck_DeckId(@NotNull UUID deckId) {
        List<AbstractCard> defaultCards = defaultCardRepository.findAllByEmbeddedDeck_DeckId(deckId)
                .stream().map(card -> (AbstractCard) card).toList();
        List<AbstractCard> typingCards = typingCardRepository.findAllByEmbeddedDeck_DeckId(deckId)
                .stream().map(card -> (AbstractCard) card).toList();
        return Stream.concat(defaultCards.stream(), typingCards.stream()).toList();
    }

    @Override
    public List<AbstractCard> findAllByEmbeddedDeck_DeckIdAndIsActive(@NotNull UUID deckId, Boolean isActive) {
        List<AbstractCard> defaultCards = defaultCardRepository.findAllByEmbeddedDeck_DeckIdAndIsActive(deckId, isActive)
                .stream().map(card -> (AbstractCard) card).toList();
        List<AbstractCard> typingCards = typingCardRepository.findAllByEmbeddedDeck_DeckIdAndIsActive(deckId, isActive)
                .stream().map(card -> (AbstractCard) card).toList();
        return Stream.concat(defaultCards.stream(), typingCards.stream()).toList();
    }
}
