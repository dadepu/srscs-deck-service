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
        log.trace("Adding externally created User '{}'...", username);

        User user = new User(userId, username);
        userRepository.save(user);
        log.info("New User '{}' successfully added.", user.getUsername());
        log.debug("New User: {}", user);
        return user;
    }

    public void disableExternallyDisabledUser(UUID userId) throws NoSuchElementException  {
        log.trace("Disabling externally disabled User '{}'...", userId);

        log.trace("Fetching User by user-id '{}'...", userId);
        User user = userRepository.findById(userId).orElseThrow();
        log.debug("Fetched User: {}", user);

        user.disableUser();
        userRepository.save(user);
        log.info("User '{}' successfully disabled.", user.getUsername());
        log.debug("Disabled User: {}", user);
    }
}
