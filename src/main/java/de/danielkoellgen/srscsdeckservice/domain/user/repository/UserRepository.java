package de.danielkoellgen.srscsdeckservice.domain.user.repository;

import de.danielkoellgen.srscsdeckservice.domain.user.domain.User;
import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

public interface UserRepository extends CrudRepository<User, UUID> {
    
}
