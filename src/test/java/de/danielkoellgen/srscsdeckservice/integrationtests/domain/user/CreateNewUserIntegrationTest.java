package de.danielkoellgen.srscsdeckservice.integrationtests.domain.user;

import de.danielkoellgen.srscsdeckservice.domain.user.domain.Username;
import de.danielkoellgen.srscsdeckservice.domain.user.application.UserService;
import de.danielkoellgen.srscsdeckservice.domain.user.domain.User;
import de.danielkoellgen.srscsdeckservice.domain.user.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class CreateNewUserIntegrationTest {

    private final UserService userService;
    private final UserRepository userRepository;

    @Autowired
    public CreateNewUserIntegrationTest(UserService userService, UserRepository userRepository) {
        this.userService = userService;
        this.userRepository = userRepository;
    }

    @AfterEach
    public void cleanUp() {
        userRepository.deleteAll();
    }

    @Test
    public void shouldPersistUserWhenCreatingNewOne() throws Exception {
        // given
        UUID userId = UUID.randomUUID();
        Username username = new Username("dadepu");

        // when
        userService.addNewExternallyCreatedUser(UUID.randomUUID(), userId, username);

        // then
        User fetchedUser = userRepository.findById(userId).get();
        assertThat(fetchedUser.getUserId())
                .isEqualTo(userId);
        assertThat(fetchedUser.getUsername())
                .isEqualTo(username);
        assertThat(fetchedUser.getIsActive())
                .isTrue();
    }
}
