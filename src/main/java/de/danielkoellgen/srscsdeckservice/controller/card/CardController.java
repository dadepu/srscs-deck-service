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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.sleuth.annotation.NewSpan;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

@RestController
public class CardController {

    private final CardService cardService;
    private final CardRepository cardRepository;

    private final Logger log = LoggerFactory.getLogger(CardController.class);

    @Autowired
    public CardController(CardService cardService, CardRepository cardRepository) {
        this.cardService = cardService;
        this.cardRepository = cardRepository;
    }

    @PostMapping(value = "/cards", consumes = {"application/json"}, produces = {"application/json"})
    @NewSpan("controller-create-new-card")
    public ResponseEntity<CardResponseDto> createNewCard(@RequestBody CardRequestDto requestDto) {
        log.info("POST /cards: Create new Card... {}", requestDto);

        CardType cardType;
        try {
            cardType = requestDto.getMappedCardType();
        } catch (Exception e) {
            log.info("Request failed w/ 400. Unrecognized CardType.");
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        log.debug("Mapped cardType is '{}'.", cardType);

        DefaultCard card;
        try {
            card = cardService.createDefaultCard(null, requestDto.deckId(),
                    (requestDto.hint() != null ? requestDto.hint().mapToHint() : null),
                    (requestDto.frontView() != null ? requestDto.frontView().mapToView() : null),
                    (requestDto.backView() != null ? requestDto.backView().mapToView() : null));
            CardResponseDto responseDto = CardResponseDto.makeFromDefaultCard(card);
            log.info("Request successful. Responding w/ 201.");
            log.debug("Response: {}", responseDto);
            return new ResponseEntity<>(responseDto, HttpStatus.CREATED);

        } catch (NoSuchElementException e) {
            log.info("Request failed w/ 404. {}", e.getMessage());
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping(value = "/cards/{card-id}", produces = {"application/json"})
    @NewSpan("controller-get-card-by-id")
    public ResponseEntity<CardResponseDto> getCardById(@PathVariable("card-id") UUID cardId) {
        log.info("GET /cards/{}: Fetch Card by id...", cardId);

        try {
            log.trace("Fetching Card by id '{}'...", cardId);
            AbstractCard card = cardRepository.findById(cardId).orElseThrow();
            log.debug("Fetched Card: {}", card);
            CardResponseDto cardResponseDto = CardResponseDto.makeFromDefaultCard((DefaultCard) card);
            log.info("Request successful. Responding w/ 200.");
            log.debug("Response: {}", cardResponseDto);
            return new ResponseEntity<>(cardResponseDto, HttpStatus.OK);

        } catch (NoSuchElementException e) {
            log.info("Request failed w/ 404. Card not found.");
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping(value = "/cards", produces = {"application/json"})
    @NewSpan("controller-get-all-cards")
    public List<CardResponseDto> getAllCards(@RequestParam("deck-id") UUID deckId,
            @RequestParam("card-status") Optional<String> cardStatusParam) {
        log.info("GET /cards?deck-id={}&card-status={}: Fetch Cards...", deckId, cardStatusParam);

        List<AbstractCard> cards;
        if (cardStatusParam.isEmpty()) {
            log.trace("Fetch Cards by deckId '{}'...", deckId);
            cards = cardRepository.findAllByEmbeddedDeck_DeckId(deckId);
            log.debug("Fetched Cards: {}", cards);
        } else {
            Boolean cardStatus = cardStatusParam.get().equals("active");
            log.trace("Fetch Cards by deckId '{}' and cardStatus '{}'...", deckId, cardStatus);
            cards = cardRepository.findAllByEmbeddedDeck_DeckIdAndIsActive(deckId, cardStatus);
            log.debug("Fetched Cards: {}", cards);
        }
        List<CardResponseDto> responseDtos = cards.stream().map(card ->
                CardResponseDto.makeFromDefaultCard((DefaultCard) card))
                .toList();
        log.info("Request successful. Responding w/ 200.");
        log.debug("{} Cards fetched: {}", cards.size(), responseDtos);
        return responseDtos;
    }

    @PostMapping(value = "/cards/{card-id}", consumes = {"application/json"}, produces = {"application/json"})
    @NewSpan("controller-override-card")
    public ResponseEntity<CardResponseDto> overrideCard(@PathVariable("card-id") UUID parentCardId,
            @RequestBody CardRequestDto requestDto) {
        log.info("POST /cards/{}: Override Card with: {}...", parentCardId, requestDto);

        CardType cardType;
        try {
            cardType = requestDto.getMappedCardType();
        } catch (Exception e) {
            log.info("Request failed w/ 400. Unrecognized CardType.");
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        log.debug("Mapped cardType is '{}'.", cardType);

        try {
            DefaultCard card = cardService.overrideAsDefaultCard(null, parentCardId,
                    (requestDto.hint() != null ? requestDto.hint().mapToHint() : null),
                    (requestDto.frontView() != null ? requestDto.frontView().mapToView() : null),
                    (requestDto.backView() != null ? requestDto.backView().mapToView() : null));
            CardResponseDto cardResponseDto = CardResponseDto.makeFromDefaultCard(card);
            log.info("Request successful. Responding w/ 201.");
            log.debug("Response: {}", cardResponseDto);
            return new ResponseEntity<>(cardResponseDto, HttpStatus.CREATED);

        } catch (NoSuchElementException e) {
            log.info("Request failed with 404. Entity not found.");
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping(value = "/cards/{card-id}")
    @NewSpan("controller-disable-card")
    public HttpStatus disableCard(@PathVariable("card-id") UUID cardId) {
        log.info("DELETE /cards/{}: Disable Card...", cardId);

        try {
            cardService.disableCard(cardId);
            log.info("Request successful. Responding w/ 200.");
            return HttpStatus.OK;

        } catch (NoSuchElementException e) {
            log.info("Request failed w/ 404. Card not found. {}", e.getMessage());
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Card not found.", e);
        }
    }

    @PostMapping(value = "/cards/{card-id}/scheduler/activity/reset")
    @NewSpan("controller-reset-cards-cheduler")
    public ResponseEntity<?> resetCardScheduler(@PathVariable("card-id") UUID cardId) {
        log.info("POST /cards/{}/scheduler/activity/reset: Reset Card-Scheduler...", cardId);

        try {
            cardService.resetCardScheduler(cardId);
            log.info("Request successful. Responding w/ 201.");
            return new ResponseEntity<>(HttpStatus.CREATED);

        } catch (NoSuchElementException e) {
            log.info("Request failed with 404. Card not found. {}", e.getMessage());
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Card not found.", e);
        }
    }

    @PostMapping(value = "/cards/{card-id}/scheduler/activity/graduate")
    @NewSpan("controller-graduate-card-scheduler")
    public ResponseEntity<?> graduateCardScheduler(@PathVariable("card-id") UUID cardId) {
        log.info("POST /cards/{}/scheduler/activity/graduate: Graduate Card-Scheduler...", cardId);

        try {
            cardService.graduateCard(cardId);
            log.info("Request successful. Responding w/ 201.");
            return new ResponseEntity<>(HttpStatus.CREATED);

        } catch (NoSuchElementException e) {
            log.info("Request failed with 404. Card not found. {}", e.getMessage());
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Card not found.", e);
        }
    }

    @PostMapping(value = "/cards/{card-id}/scheduler/activity/review")
    @NewSpan("controller-review-card-scheduler")
    public ResponseEntity<?> reviewCardScheduler(@PathVariable("card-id") UUID cardId,
            @RequestBody ReviewRequestDto requestDto) {
        log.info("POST /cards/{}/scheduler/activity/review: Review Card... {}", cardId, requestDto);

        ReviewAction reviewAction;
        try {
            reviewAction = requestDto.getMappedReviewAction();
        } catch (Exception e) {
            log.info("Request failed w/ 400. ReviewAction invalid. {}", e.getMessage());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Review-Action invalid.", e);
        }
        log.debug("Mapped reviewAction is '{}'.", reviewAction);

        try {
            cardService.reviewCard(cardId, reviewAction);
            log.info("Request successful. Responding w/ 201.");
            return new ResponseEntity<>(HttpStatus.CREATED);

        } catch (NoSuchElementException e) {
            log.info("Request failed w/ 404. Card not found. {}", e.getMessage());
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Card not found.", e);
        }
    }
}
