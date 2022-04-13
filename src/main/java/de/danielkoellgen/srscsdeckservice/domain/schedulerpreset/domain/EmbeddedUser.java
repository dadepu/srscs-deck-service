package de.danielkoellgen.srscsdeckservice.domain.schedulerpreset.domain;

import de.danielkoellgen.srscsdeckservice.domain.user.domain.User;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.UUID;

@Getter
public class EmbeddedUser {

    @NotNull
    @Field("user_id")
    private final UUID userId;

    @PersistenceConstructor
    public EmbeddedUser(@NotNull UUID userId) {
        this.userId = userId;
    }

    public EmbeddedUser(@NotNull User user) {
        this.userId = user.getUserId();
    }
}
