package de.danielkoellgen.srscsdeckservice.controller.deck;

import de.danielkoellgen.srscsdeckservice.controller.deck.dto.DeckRequestDto;
import de.danielkoellgen.srscsdeckservice.controller.deck.dto.DeckResponseDto;
import de.danielkoellgen.srscsdeckservice.domain.deck.application.DeckService;
import de.danielkoellgen.srscsdeckservice.domain.deck.domain.Deck;
import de.danielkoellgen.srscsdeckservice.domain.deck.domain.DeckName;
import de.danielkoellgen.srscsdeckservice.domain.deck.repository.DeckRepository;
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
import java.util.UUID;

@RestController
public class DeckController {

    private final DeckService deckService;
    private final DeckRepository deckRepository;

    private final Logger logger = LoggerFactory.getLogger(DeckController.class);

    @Autowired
    public DeckController(DeckService deckService, DeckRepository deckRepository) {
        this.deckService = deckService;
        this.deckRepository = deckRepository;
    }

    @PostMapping(value = "/decks", consumes = {"application/json"}, produces = {"application/json"})
    @NewSpan("controller-create-deck")
    public ResponseEntity<DeckResponseDto> createDeck(@RequestBody DeckRequestDto requestDto) {
        UUID transactionId = UUID.randomUUID();
        logger.trace("POST /decks: Create deck '{}'. [tid={}, payload={}]",
                requestDto.deckName(), transactionId, requestDto);

        DeckName deckName;
        try {
            deckName = requestDto.getMappedDeckName();
        } catch (Exception e) {
            logger.trace("Request failed. Mapping error. Responding 400. [tid={}, error={}]",
                    transactionId, e.getStackTrace());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Failed to map deck-name to primitive.", e);
        }
        Deck deck;
        try {
            deck = deckService.createNewDeck(transactionId, null, requestDto.userId(), deckName);
        } catch (NoSuchElementException e) {
            logger.trace("Request failed. [tid={}, error={}]",
                    transactionId, e.getStackTrace());
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        logger.trace("Deck '{}' for '{}' created. [tid={}]",
                deck.getDeckName().getName(), deck.getEmbeddedUser().getUsername().getUsername(), transactionId);
        return new ResponseEntity<>(new DeckResponseDto(deck), HttpStatus.CREATED);
    }

    @DeleteMapping(value = "/decks/{deck-id}")
    @NewSpan("controller-disable-deck")
    public HttpStatus disableDeck(@PathVariable("deck-id") UUID deckId){
        UUID transactionId = UUID.randomUUID();
        logger.trace("DELETE /decks/{}: Delete deck. [tid={}, deckId={}]",
                deckId, transactionId, deckId);
        try {
            deckService.deleteDeck(transactionId, deckId);
        } catch (NoSuchElementException e) {
            logger.trace("Request failed. Deck not found. Responding 404. [tid={}, message={}",
                    transactionId, e.getStackTrace());
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Deck not found.", e);
        }
        logger.trace("Deck '{}' disabled. [tid={}]",
                deckId, transactionId);
        return HttpStatus.OK;
    }

    @GetMapping(value = "/decks/{deck-id}", produces = {"application/json"})
    @NewSpan("controller-get-deck")
    public ResponseEntity<DeckResponseDto> getDeck(@PathVariable("deck-id") UUID deckId) {
        UUID transactionId = UUID.randomUUID();
        logger.trace("GET /decks/{}: Fetch Deck by id. [tid={}]",
                deckId, transactionId);

        Deck deck;
        try {
            deck = deckRepository.findById(deckId).get();
        } catch (NoSuchElementException e) {
            logger.trace("Request failed. Deck not found. Responding 404. [tid={}]",
                    transactionId);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        logger.trace("Deck retrieved. Responding 200. [tid={}, payload={}]",
                transactionId, new DeckResponseDto(deck));
        return new ResponseEntity<>(new DeckResponseDto(deck), HttpStatus.OK);
    }

    @GetMapping(value = "/decks", produces = {"application/json"})
    @NewSpan("controller-get-decks-by-userid")
    public List<DeckResponseDto> getDecksByUserId(@RequestParam("user-id") UUID userId) {
        UUID transactionId = UUID.randomUUID();
        logger.trace("GET /decks?user-id={}: Fetch Decks by user-id. [tid={}]",
                userId, transactionId);

        return deckRepository.findDecksByEmbeddedUser_UserId(userId)
                .stream().map(DeckResponseDto::new).toList();
    }

    @PutMapping(value = "/decks/{deck-id}/scheduler-presets/{scheduler-preset-id}")
    @NewSpan("controller-update-scheduler-preset-for-deck")
    public ResponseEntity<?> updateSchedulerPresetForDeck(
            @PathVariable("deck-id") UUID deckId, @PathVariable("scheduler-preset-id") UUID presetId) {
        UUID transactionId = UUID.randomUUID();
        logger.trace("PUT /decks/{}/scheduler-presets/{}: Change Preset. [tid={}]",
                deckId, presetId, transactionId);

        try {
            deckService.changePreset(transactionId, deckId, presetId);
        } catch (NoSuchElementException e) {
            logger.trace("Request failed. Entity not found. Responding 404. [tid={}]",
                    transactionId);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Entity not found.", e);
        }
        logger.trace("Preset changed. Responding 200. [tid={}]",
                transactionId);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
