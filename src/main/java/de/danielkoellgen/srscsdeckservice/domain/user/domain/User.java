package de.danielkoellgen.srscsdeckservice.domain.user.domain;

import de.danielkoellgen.srscsdeckservice.domain.domainprimitive.Username;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@Getter
@AllArgsConstructor
public class User {

    private UUID userId;

    private Username username;

    private Boolean isActive;

    public User(UUID userId, Username username) {
        this.userId = userId;
        this.username = username;
    }

    public void disableUser() {
        isActive = false;
    }

    @Override
    public String toString() {
        return "User{" +
                "userId=" + userId +
                ", username=" + username +
                ", isActive=" + isActive +
                '}';
    }
}
