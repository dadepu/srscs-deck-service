package de.danielkoellgen.srscsdeckservice.domain.user.domain;

import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.Unwrapped;

import java.util.UUID;

@Getter
@Document("users")
public class User {

    @Id
    @NotNull
    private UUID userId;

    @NotNull
    @Unwrapped.Nullable
    private final Username username;

    @Field("is_active")
    @Nullable
    private Boolean isActive;

    @Transient
    private final Logger log = LoggerFactory.getLogger(User.class);

    public User(@NotNull UUID userId, @NotNull Username username) {
        this.userId = userId;
        this.username = username;
        this.isActive = true;
    }

    @PersistenceConstructor
    public User(@NotNull UUID userId, @NotNull Username username, @NotNull Boolean isActive) {
        this.userId = userId;
        this.username = username;
        this.isActive = isActive;
    }

    public void disableUser() {
        isActive = false;
        log.debug("User.isActive has been set to '{}'.", isActive);
    }

    @Override
    public String toString() {
        return "User{" +
                "userId=" + userId +
                ", username=" + username.getUsername() +
                ", isActive=" + isActive +
                '}';
    }
}
