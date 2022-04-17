package de.danielkoellgen.srscsdeckservice.controller.card;

import de.danielkoellgen.srscsdeckservice.controller.card.dto.CardRequestDto;
import de.danielkoellgen.srscsdeckservice.controller.card.dto.CardResponseDto;
import de.danielkoellgen.srscsdeckservice.controller.card.dto.ReviewRequestDto;
import de.danielkoellgen.srscsdeckservice.domain.card.application.CardService;
import de.danielkoellgen.srscsdeckservice.domain.card.domain.AbstractCard;
import de.danielkoellgen.srscsdeckservice.domain.card.domain.CardType;
import de.danielkoellgen.srscsdeckservice.domain.card.domain.DefaultCard;
import de.danielkoellgen.srscsdeckservice.domain.card.domain.ReviewAction;
import de.danielkoellgen.srscsdeckservice.domain.card.repository.CardRepository;
import de.danielkoellgen.srscsdeckservice.domain.card.repository.DefaultCardRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@RestController
public class CardController {

    private final CardService cardService;
    private final CardRepository cardRepository;

    private final Logger logger = LoggerFactory.getLogger(CardController.class);

    @Autowired
    public CardController(CardService cardService, CardRepository cardRepository) {
        this.cardService = cardService;
        this.cardRepository = cardRepository;
    }

    @PostMapping(value = "/cards", consumes = {"application/json"}, produces = {"application/json"})
    public ResponseEntity<CardResponseDto> createNewCard(@RequestBody CardRequestDto requestDto) {
        UUID transactionId = UUID.randomUUID();
        logger.trace("POST /cards: Create Card. [tid={}, payload={}]",
                transactionId, requestDto);

        CardType cardType;
        try {
            cardType = requestDto.getMappedCardType();
        } catch (Exception e) {
            logger.trace("Request failed. Unrecognized card-type. Responding 400. [tid={}]",
                    transactionId);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        DefaultCard card;
        try {
            card = cardService.createDefaultCard(transactionId, requestDto.deckId(),
                    (requestDto.hint() != null ? requestDto.hint().mapToHint() : null),
                    (requestDto.frontView() != null ? requestDto.frontView().mapToView() : null),
                    (requestDto.backView() != null ? requestDto.backView().mapToView() : null));
            logger.trace("Card created. Responding 201. [tid={}, payload={}]",
                    transactionId, CardResponseDto.makeFromDefaultCard(card));
            return new ResponseEntity<>(CardResponseDto.makeFromDefaultCard(card), HttpStatus.CREATED);
        } catch (NoSuchElementException e) {
            logger.trace("Request failed. Entity not found. Responding 404. [tid={}]",
                    transactionId);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping(value = "/cards/{card-id}", produces = {"application/json"})
    public ResponseEntity<CardResponseDto> getCardById(@PathVariable("card-id") UUID cardId) {
        UUID transactionId = UUID.randomUUID();
        logger.trace("GET /cards/{}: Fetch Card by id. [tid={}]",
                cardId, transactionId);

        AbstractCard card;
        try {
            card = cardRepository.findById(cardId).get();
        } catch (NoSuchElementException e) {
            logger.trace("Request failed. Card not found. Responding 404. [tid={}]",
                    transactionId);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        logger.trace("Card retrieved. Responding 200. [tid={}, payload={}]",
                transactionId, CardResponseDto.makeFromDefaultCard((DefaultCard) card));
        return new ResponseEntity<>(CardResponseDto.makeFromDefaultCard((DefaultCard) card), HttpStatus.OK);
    }

    @GetMapping(value = "/cards", produces = {"application/json"})
    public List<CardResponseDto> getAllCards(@RequestParam("deck-id") UUID deckId,
            @RequestParam("card-status") String cardStatusParam) {
        UUID transactionId = UUID.randomUUID();
        logger.trace("GET /cards?deck-id={}&card-status={}: Fetch Cards. [tid={}]",
                deckId, cardStatusParam, transactionId);

        List<AbstractCard> cards;
        if (cardStatusParam == null) {
            cards = cardRepository.findAllByEmbeddedDeck_DeckId(deckId);
        } else {
            Boolean cardStatus = cardStatusParam.equals("active");
            cards = cardRepository.findAllByEmbeddedDeck_DeckIdAndIsActive(deckId, cardStatus);
        }

        logger.trace("{} Cards retrieved. Responding 200. [tid={}]",
                cards.size(), transactionId);
        return cards.stream().map(card ->
            CardResponseDto.makeFromDefaultCard((DefaultCard) card)
        ).toList();
    }

    @PostMapping(value = "/cards/{card-id}", consumes = {"application/json"}, produces = {"application/json"})
    public ResponseEntity<CardResponseDto> overrideCard(@PathVariable("card-id") UUID parentCardId,
            @RequestBody CardRequestDto requestDto) {
        UUID transactionId = UUID.randomUUID();
        logger.trace("POST /cards/{}: Override Card. [tid={}, payload={}]",
                parentCardId, transactionId, requestDto);
        CardType cardType;
        try {
            cardType = requestDto.getMappedCardType();
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        DefaultCard card;
        try {
            card = cardService.overrideAsDefaultCard(transactionId, parentCardId,
                    (requestDto.hint() != null ? requestDto.hint().mapToHint() : null),
                    (requestDto.frontView() != null ? requestDto.frontView().mapToView() : null),
                    (requestDto.backView() != null ? requestDto.backView().mapToView() : null));
            logger.trace("Card overridden. Responding 201. [tid={}, payload={}]",
                    transactionId, CardResponseDto.makeFromDefaultCard(card));
            return new ResponseEntity<>(CardResponseDto.makeFromDefaultCard(card), HttpStatus.CREATED);
        } catch (NoSuchElementException e) {
            logger.trace("Request failed. Responding 404. [tid={}, message={}]",
                    transactionId, e.getStackTrace());
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping(value = "/cards/{card-id}")
    public HttpStatus disableCard(@PathVariable("card-id") UUID cardId) {
        UUID transactionId = UUID.randomUUID();
        logger.trace("DELETE /cards/{}: Disable Card. [tid={}]",
                cardId, transactionId);

        try {
            cardService.disableCard(transactionId, cardId);
        } catch (NoSuchElementException e) {
            logger.trace("Request failed. Card not found. [tid={}, message={}]",
                    transactionId, e.getStackTrace());
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Card not found.", e);
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

    @PostMapping(value = "/cards/{card-id}/scheduler/activity/graduate")
    public HttpStatus graduateCardScheduler(@PathVariable("card-id") UUID cardId) {
        UUID transactionId = UUID.randomUUID();
        try {
            cardService.graduateCard(transactionId, cardId);
        } catch (NoSuchElementException e) {
            return HttpStatus.NOT_FOUND;
        }
        return HttpStatus.CREATED;
    }

    @PostMapping(value = "/cards/{card-id}/scheduler/activity/review")
    public HttpStatus reviewCardScheduler(@PathVariable("card-id") UUID cardId, @RequestBody ReviewRequestDto requestDto) {
        UUID transactionId = UUID.randomUUID();
        ReviewAction reviewAction;
        try {
            reviewAction = requestDto.getMappedReviewAction();
        } catch (Exception e) {
            return HttpStatus.BAD_REQUEST;
        }
        try {
            cardService.reviewCard(transactionId, cardId, reviewAction);
        } catch (NoSuchElementException e) {
            return HttpStatus.NOT_FOUND;
        }
        return HttpStatus.CREATED;
    }


}
