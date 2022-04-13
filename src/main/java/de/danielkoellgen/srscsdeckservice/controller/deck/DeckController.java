package de.danielkoellgen.srscsdeckservice.controller.deck;

import de.danielkoellgen.srscsdeckservice.controller.deck.dto.DeckRequestDto;
import de.danielkoellgen.srscsdeckservice.controller.deck.dto.DeckResponseDto;
import de.danielkoellgen.srscsdeckservice.domain.deck.application.DeckService;
import de.danielkoellgen.srscsdeckservice.domain.deck.domain.Deck;
import de.danielkoellgen.srscsdeckservice.domain.deck.domain.DeckName;
import de.danielkoellgen.srscsdeckservice.domain.deck.repository.DeckRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.NoSuchElementException;
import java.util.UUID;

@RestController
public class DeckController {

    private final DeckService deckService;
    private final DeckRepository deckRepository;

    @Autowired
    public DeckController(DeckService deckService, DeckRepository deckRepository) {
        this.deckService = deckService;
        this.deckRepository = deckRepository;
    }

    @PostMapping(value = "/decks", consumes = {"application/json"}, produces = {"application/json"})
    public ResponseEntity<?> createDeck(@RequestBody DeckRequestDto requestDto) {
        UUID transactionId = UUID.randomUUID();
        DeckName deckName;
        try {
            deckName = requestDto.getDeckName();
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        Deck deck;
        try {
            deck = deckService.createNewDeck(transactionId, requestDto.userId(), deckName);
        } catch (NoSuchElementException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(new DeckResponseDto(deck), HttpStatus.CREATED);
    }
}
