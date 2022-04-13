package de.danielkoellgen.srscsdeckservice.controller.card;

import de.danielkoellgen.srscsdeckservice.controller.card.dto.CardRequestDto;
import de.danielkoellgen.srscsdeckservice.controller.card.dto.CardResponseDto;
import de.danielkoellgen.srscsdeckservice.domain.card.application.CardService;
import de.danielkoellgen.srscsdeckservice.domain.card.domain.AbstractCard;
import de.danielkoellgen.srscsdeckservice.domain.card.domain.CardType;
import de.danielkoellgen.srscsdeckservice.domain.card.domain.DefaultCard;
import de.danielkoellgen.srscsdeckservice.domain.card.repository.CardRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@RestController
public class CardController {

    private final CardService cardService;
    private final CardRepository cardRepository;

    @Autowired
    public CardController(CardService cardService, CardRepository cardRepository) {
        this.cardService = cardService;
        this.cardRepository = cardRepository;
    }

    @PostMapping(value = "/cards", consumes = {"application/json"}, produces = {"application/json"})
    public ResponseEntity<CardResponseDto> createNewCard(@RequestBody CardRequestDto requestDto) {
        UUID transactionId = UUID.randomUUID();
        CardType cardType;
        try {
            cardType = requestDto.getCardType();
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        DefaultCard card;
        try {
            card = cardService.createDefaultCard(transactionId, requestDto.deckId(),
                    (requestDto.hint() != null ? requestDto.hint().mapToHint() : null),
                    (requestDto.frontView() != null ? requestDto.frontView().mapToView() : null),
                    (requestDto.backView() != null ? requestDto.backView().mapToView() : null));
            return new ResponseEntity<>(CardResponseDto.makeFromDefaultCard(card), HttpStatus.CREATED);
        } catch (NoSuchElementException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping(value = "/cards/{card-id}", produces = {"application/json"})
    public ResponseEntity<CardResponseDto> getCardById(@PathVariable("card-id") UUID cardId) {
        UUID transactionId = UUID.randomUUID();
        AbstractCard card;
        try {
            card = cardRepository.findById(cardId).get();
        } catch (NoSuchElementException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(CardResponseDto.makeFromDefaultCard((DefaultCard) card), HttpStatus.OK);
    }

    @GetMapping(value = "/cards/", produces = {"application/json"})
    public List<CardResponseDto> getAllCards(@RequestParam("deck-id") UUID deckId,
            @RequestParam("card-status") String cardStatusParam) {
        UUID transactionId = UUID.randomUUID();

        List<AbstractCard> cards;
        if (cardStatusParam == null) {
            cards = cardRepository.findAllByEmbeddedDeck_DeckId(deckId);
        } else {
            Boolean cardStatus = cardStatusParam == "active";
            cards = cardRepository.findAllByEmbeddedDeck_DeckIdAndIsActive(deckId, cardStatus);
        }

        return cards.stream().map(card ->
            CardResponseDto.makeFromDefaultCard((DefaultCard) card)
        ).toList();
    }

    @PostMapping(value = "/cards/{card-id}", consumes = {"application/json"}, produces = {"application/json"})
    public ResponseEntity<CardResponseDto> overrideCard(@PathVariable("card-id") UUID parentCardId,
            @RequestBody CardRequestDto requestDto) {
        UUID transactionId = UUID.randomUUID();
        CardType cardType;
        try {
            cardType = requestDto.getCardType();
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        DefaultCard card;
        try {
            card = cardService.overrideAsDefaultCard(transactionId, parentCardId,
                    (requestDto.hint() != null ? requestDto.hint().mapToHint() : null),
                    (requestDto.frontView() != null ? requestDto.frontView().mapToView() : null),
                    (requestDto.backView() != null ? requestDto.backView().mapToView() : null));
            return new ResponseEntity<>(CardResponseDto.makeFromDefaultCard(card), HttpStatus.CREATED);
        } catch (NoSuchElementException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping(value = "/cards/{card-id}")
    public HttpStatus disableCard(@PathVariable("card-id") UUID cardId) {
        UUID transactionId = UUID.randomUUID();
        try {
            cardService.disableCard(transactionId, cardId);
        } catch (NoSuchElementException e) {
            return HttpStatus.NOT_FOUND;
        }
        return HttpStatus.OK;
    }


    @PostMapping(value = "/cards/{card-id}/scheduler/activity/reset")
    public HttpStatus resetCardScheduler(@PathVariable("card-id") UUID cardId) {
        UUID transactionId = UUID.randomUUID();
        try {
            cardService.resetCardScheduler(transactionId, cardId);
        } catch (NoSuchElementException e) {
            return HttpStatus.NOT_FOUND;
        }
        return HttpStatus.CREATED;
    }
}
