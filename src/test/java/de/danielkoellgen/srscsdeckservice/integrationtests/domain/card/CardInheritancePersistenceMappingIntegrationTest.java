package de.danielkoellgen.srscsdeckservice.integrationtests.domain.card;

import de.danielkoellgen.srscsdeckservice.domain.card.domain.AbstractCard;
import de.danielkoellgen.srscsdeckservice.domain.card.domain.DefaultCard;
import de.danielkoellgen.srscsdeckservice.domain.card.domain.TypingCard;
import de.danielkoellgen.srscsdeckservice.domain.card.repository.CardRepository;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class CardInheritancePersistenceMappingIntegrationTest {

    private final CardRepository cardRepository;

    @Autowired
    public CardInheritancePersistenceMappingIntegrationTest(CardRepository cardRepository) {
        this.cardRepository = cardRepository;
    }

    @Disabled
    @Test
    public void shouldMapCardsAccordingToSubtypeWhenFetchingFromDatabase() throws Exception {
        //TODO
//        // given
//        AbstractCard defaultCard = new DefaultCard(UUID.randomUUID(), null, null, null, null, null);
//        AbstractCard typingCard = new TypingCard(UUID.randomUUID(), null, null);
//        cardRepository.save(defaultCard);
//        cardRepository.save(typingCard);
//
//        // when
//        AbstractCard fetchedDefaultCard = cardRepository.findById(defaultCard.getCardId()).get();
//        AbstractCard fetchedTypingCard = cardRepository.findById(typingCard.getCardId()).get();
//
//        // then
//        assertThat(fetchedDefaultCard instanceof DefaultCard)
//                .isTrue();
//        assertThat(fetchedTypingCard instanceof TypingCard)
//                .isTrue();
    }
}
