package de.danielkoellgen.srscsdeckservice.domain.user.application;

import de.danielkoellgen.srscsdeckservice.domain.user.domain.User;
import de.danielkoellgen.srscsdeckservice.domain.user.domain.Username;
import de.danielkoellgen.srscsdeckservice.domain.user.repository.UserRepository;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;
import java.util.UUID;

@Service
public class UserService {

    private final UserRepository userRepository;

    private final Logger log = LoggerFactory.getLogger(UserService.class);

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public @NotNull User addNewExternallyCreatedUser(UUID userId, Username username) {
        log.trace("Adding externally created User...");

        User user = new User(userId, username);
        log.debug("New User created: {}", user);

        userRepository.save(user);
        log.info("New User '{}' added.", user.getUsername().getUsername());
        return user;
    }

    public void disableExternallyDisabledUser(UUID userId) throws NoSuchElementException  {
        log.trace("Disabling externally disabled User...");

        User user = userRepository.findById(userId).orElseThrow();
        log.debug("User fetched by id: {}", user);

        user.disableUser();
        log.trace("User disabled.");

        userRepository.save(user);
        log.info("User '{}' disabled.", user.getUsername().getUsername());
        log.debug("User updated: {}", user);
    }
}
