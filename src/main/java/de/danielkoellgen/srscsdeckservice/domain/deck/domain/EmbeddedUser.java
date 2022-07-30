package de.danielkoellgen.srscsdeckservice.domain.deck.domain;

import de.danielkoellgen.srscsdeckservice.domain.user.domain.User;
import de.danielkoellgen.srscsdeckservice.domain.user.domain.Username;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.Unwrapped;

import java.util.UUID;


public class EmbeddedUser {
    @Getter
    @NotNull
    @Field("_id")
    private final UUID userId;

    @Setter @Getter
    @NotNull
    @Unwrapped.Nullable
    private Username username;

    public EmbeddedUser(@NotNull User user) {
        this.userId = user.getUserId();
        this.username = user.getUsername();
    }

    @PersistenceConstructor
    public EmbeddedUser(@NotNull UUID userId, @NotNull Username username) {
        this.userId = userId;
        this.username = username;
    }

    @Override
    public String toString() {
        return "EmbeddedUser{" +
                "userId=" + userId +
                ", username=" + username +
                '}';
    }
}
