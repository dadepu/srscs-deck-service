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
}
