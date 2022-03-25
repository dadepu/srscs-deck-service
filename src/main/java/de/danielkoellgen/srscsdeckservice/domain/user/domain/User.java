package de.danielkoellgen.srscsdeckservice.domain.user.domain;

import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.UUID;

@Getter
@Document("users")
public class User {

    @Id
    @NotNull
    private UUID userId;

    @Field("username")
    @NotNull
    private Username username;

    @Field("is_active")
    private Boolean isActive;

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

    public void renameUser(Username newUsername) {
        this.username = newUsername;
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
