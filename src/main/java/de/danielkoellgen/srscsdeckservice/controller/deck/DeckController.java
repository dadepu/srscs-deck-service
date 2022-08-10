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

    private final Logger log = LoggerFactory.getLogger(DeckController.class);

    @Autowired
    public DeckController(DeckService deckService, DeckRepository deckRepository) {
        this.deckService = deckService;
        this.deckRepository = deckRepository;
    }

    @PostMapping(value = "/decks", consumes = {"application/json"}, produces = {"application/json"})
    @NewSpan("controller-create-deck")
    public ResponseEntity<DeckResponseDto> createDeck(@RequestBody DeckRequestDto requestDto) {
        log.info("POST /decks: Create deck '{}'... {}", requestDto.deckName(), requestDto);

        DeckName deckName;
        try {
            deckName = requestDto.getMappedDeckName();

        } catch (Exception e) {
            log.info("Request failed w/ 400. Provided deck-name invalid. {}", e.getMessage());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Failed to map deck-name to primitive.", e);
        }

        Deck deck;
        try {
            deck = deckService.createNewDeck(null, requestDto.userId(), deckName);
            DeckResponseDto responseDto = new DeckResponseDto(deck);
            log.info("Request successful. Responding w/ 201.");
            log.debug("Response: {}", responseDto);
            return new ResponseEntity<>(responseDto, HttpStatus.CREATED);

        } catch (NoSuchElementException e) {
            log.info("Request failed w/ 404. {}", e.getMessage());
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping(value = "/decks/{deck-id}")
    @NewSpan("controller-disable-deck")
    public ResponseEntity<?> disableDeck(@PathVariable("deck-id") UUID deckId){
        log.info("DELETE /decks/{}: Delete deck...", deckId);

        try {
            deckService.deleteDeck(deckId);
            log.info("Request successful. Responding w/ 200.");
            return new ResponseEntity<>(HttpStatus.OK);

        } catch (NoSuchElementException e) {
            log.info("Request failed w/ 404. Deck not found.");
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Deck not found.", e);
        }
    }

    @GetMapping(value = "/decks/{deck-id}", produces = {"application/json"})
    @NewSpan("controller-get-deck")
    public ResponseEntity<DeckResponseDto> getDeck(@PathVariable("deck-id") UUID deckId) {
        log.info("GET /decks/{}: Fetch Deck by id...", deckId);

        Deck deck;
        try {
            deck = deckRepository.findById(deckId).orElseThrow();
            DeckResponseDto responseDto = new DeckResponseDto(deck);
            log.info("Request successful. Responding w/ 200.");
            log.debug("Response: {}", responseDto);
            return new ResponseEntity<>(responseDto, HttpStatus.OK);

        } catch (NoSuchElementException e) {
            log.info("Request failed w/ 404. Deck not found.");
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping(value = "/decks", produces = {"application/json"})
    @NewSpan("controller-get-decks-by-userid")
    public List<DeckResponseDto> getDecksByUserId(@RequestParam("user-id") UUID userId) {
        log.info("GET /decks?user-id={}: Fetch Decks by user-id...", userId);

        List<DeckResponseDto> responseDtos = deckRepository.findDecksByEmbeddedUser_UserId(userId)
                .stream()
                .map(DeckResponseDto::new)
                .toList();
        log.info("Request successful. Responding w/ 200.");
        log.debug("{} Decks fetched. {}", responseDtos.size(), responseDtos);
        return responseDtos;
    }

    @PutMapping(value = "/decks/{deck-id}/scheduler-presets/{scheduler-preset-id}")
    @NewSpan("controller-update-scheduler-preset-for-deck")
    public ResponseEntity<?> updateSchedulerPresetForDeck(
            @PathVariable("deck-id") UUID deckId, @PathVariable("scheduler-preset-id") UUID presetId) {
        log.info("PUT /decks/{}/scheduler-presets/{}: Change Preset...", deckId, presetId);

        try {
            deckService.changePreset(deckId, presetId);
            log.info("Request successful. Responding w/ 200.");
            return new ResponseEntity<>(HttpStatus.OK);

        } catch (NoSuchElementException e) {
            log.info("Request failed w/ 404. Entity not found.");
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Entity not found.", e);
        }
    }
}
