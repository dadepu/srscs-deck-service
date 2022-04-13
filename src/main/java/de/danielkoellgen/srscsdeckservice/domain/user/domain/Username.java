package de.danielkoellgen.srscsdeckservice.domain.user.domain;

import de.danielkoellgen.srscsdeckservice.domain.core.AbstractStringValidation;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.mongodb.core.mapping.Field;

@EqualsAndHashCode(callSuper = false)
public class Username extends AbstractStringValidation {

    @Getter
    @Field("username")
    private final String username;

    @PersistenceConstructor
    public Username(@NotNull String username) throws Exception {
        validateUsernameOrThrow(username);
        this.username = username;
    }

    private void validateUsernameOrThrow(@NotNull String username) throws Exception {
        validateMinLengthOrThrow(username, 4, this::mapUserException);
        validateMaxLengthOrThrow(username, 16, this::mapUserException);
        validateRegexOrThrow(username, "^([A-Za-z0-9]){4,16}$", this::mapUserException);
    }

    private Exception mapUserException(String message) {
        return new UsernameException(message);
    }

    @Override
    public String toString() {
        return "Username{" +
                "username='" + username + '\'' +
                '}';
    }
}
