package de.danielkoellgen.srscsdeckservice.domain.card.repository;

import de.danielkoellgen.srscsdeckservice.domain.card.domain.AbstractCard;
import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

public interface CardRepository extends CrudRepository<AbstractCard, UUID> {

}
