package de.danielkoellgen.srscsdeckservice.domain.domainprimitive;

import de.danielkoellgen.srscsdeckservice.domain.core.AbstractStringValidation;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;

@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class Username extends AbstractStringValidation {

    @Getter
    private String username;

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
