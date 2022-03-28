package de.danielkoellgen.srscsdeckservice.domain.user.application;

import de.danielkoellgen.srscsdeckservice.domain.deck.domain.Deck;
import de.danielkoellgen.srscsdeckservice.domain.deck.repository.DeckRepository;
import de.danielkoellgen.srscsdeckservice.domain.schedulerpreset.domain.SchedulerPreset;
import de.danielkoellgen.srscsdeckservice.domain.schedulerpreset.repository.SchedulerPresetRepository;
import de.danielkoellgen.srscsdeckservice.domain.user.domain.Username;
import de.danielkoellgen.srscsdeckservice.domain.user.domain.User;
import de.danielkoellgen.srscsdeckservice.domain.user.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final DeckRepository deckRepository;
    private final SchedulerPresetRepository schedulerPresetRepository;

    private final Logger logger = LoggerFactory.getLogger(UserService.class);

    @Autowired
    public UserService(UserRepository userRepository, DeckRepository deckRepository,
            SchedulerPresetRepository schedulerPresetRepository) {
        this.userRepository = userRepository;
        this.deckRepository = deckRepository;
        this.schedulerPresetRepository = schedulerPresetRepository;
    }

    public void addNewExternallyCreatedUser(UUID transactionId, UUID userId, Username username) {
        User user = new User(userId, username);
        userRepository.save(user);

        logger.info("New User '{}' added. [tid={}, userId={}]",
                user.getUsername().getUsername(), transactionId, user.getUserId());
        logger.info("New User added. [{}]", user);
    }

    public void renameUser(UUID transactionId, UUID userId, Username newUsername) {
        User user = userRepository.findById(userId).get();
        Username oldName = user.getUsername();
        user.renameUser(newUsername);
        userRepository.save(user);

        logger.info("User '{}' renamed to '{}'. [tid={}, userId={}]",
                oldName.getUsername(), user.getUsername().getUsername(), transactionId, userId);

        List<Deck> userOwnedDecks = deckRepository.findDecksByEmbeddedUser_UserId(userId);
        userOwnedDecks.forEach(deck -> deck.updateEmbeddedUser(user));
        deckRepository.saveAll(userOwnedDecks);

        logger.info("Updated {} Decks' EmbeddedUser from {}. [tid={}, userId={}]", userOwnedDecks.size(),
                newUsername.getUsername(), transactionId, userId);

        List<SchedulerPreset> userOwnedPresets = schedulerPresetRepository
                .findSchedulerPresetsByEmbeddedUser_UserId(userId);
        userOwnedPresets.forEach(preset -> preset.updateEmbeddedUser(user));
        schedulerPresetRepository.saveAll(userOwnedPresets);

        logger.info("Updated {} SchedulerPresets' EmbeddedUser from {}. [tid={}, userId={}]", userOwnedPresets.size(),
                newUsername.getUsername(), transactionId, userId);

    }

    public void disableExternallyDisabledUser(UUID transactionId, UUID userId) throws NoSuchElementException  {
        User user = userRepository.findById(userId).get();
        user.disableUser();
        userRepository.save(user);

        logger.info("User '{}' disabled. [tid={}, userId={}]",
                user.getUsername().getUsername(), transactionId, user.getUserId());
    }
}
