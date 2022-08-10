package de.danielkoellgen.srscsdeckservice.domain.deck.domain;

import de.danielkoellgen.srscsdeckservice.domain.schedulerpreset.domain.SchedulerPreset;
import de.danielkoellgen.srscsdeckservice.domain.user.domain.User;
import de.danielkoellgen.srscsdeckservice.domain.user.domain.Username;
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
@Document("decks")
public class Deck {

    @Id
    @NotNull
    private UUID deckId;

    @Nullable
    @Unwrapped.Nullable
    private DeckName deckName;

    @Nullable
    @Transient
    private User user;

    @NotNull
    @Field("user")
    private EmbeddedUser embeddedUser;

    @NotNull
    @Field("is_active")
    private Boolean isActive;

    @Nullable
    @Field("scheduler_preset_id")
    private SchedulerPreset schedulerPreset;

    public Deck(@NotNull User user, @NotNull DeckName deckName) {
        this.deckId = UUID.randomUUID();
        this.deckName = deckName;
        this.user = user;
        this.embeddedUser = new EmbeddedUser(user);
        this.isActive = true;
    }

    @Transient
    private final Logger log = LoggerFactory.getLogger(Deck.class);

    /*
     *  Including @NotNull DeckName deckName leads for unknown reasons to a failed initialization of deckName.
     *  Exclusion from the persistence constructor triggers initialization via reflection.
     */
    @PersistenceConstructor
    public Deck(@NotNull UUID deckId, @NotNull EmbeddedUser embeddedUser, @NotNull Boolean isActive) {
        this.deckId = deckId;
        this.embeddedUser = embeddedUser;
        this.isActive = isActive;
    }

    public void updateEmbeddedUser(@NotNull User user) {
        this.embeddedUser = new EmbeddedUser(user);
    }

    public void disableDeck() {
        isActive = false;
        log.debug("Deck.isActive has been set to '{}'.", isActive);
    }

    public void updateSchedulerPreset(@Nullable SchedulerPreset schedulerPreset) {
        this.schedulerPreset = schedulerPreset;
        log.debug("Preset replaced with '{}'.", schedulerPreset.getPresetId());
    }

    public @NotNull UUID getUserId() {
        return embeddedUser.getUserId();
    }

    public @NotNull Username getUsername() {
        return embeddedUser.getUsername();
    }

    public @NotNull DeckName getDeckName() {
        if (deckName == null) {
            throw new IllegalStateException("DeckName must not be null.");
        }
        return deckName;
    }

    public @Nullable SchedulerPreset getSchedulerPreset() {
        if (schedulerPreset == null) {
            return null;
        }
        if (!schedulerPreset.getIsActive()) {
            return null;
        }
        return schedulerPreset;
    }

    @Override
    public String toString() {
        return "Deck{" +
                "deckId=" + deckId +
                ", deckName=" + getDeckName().getName() +
                ", userId=" + embeddedUser.getUserId() +
                ", username=" + embeddedUser.getUsername().getUsername() +
                ", isActive=" + isActive +
                ", schedulerPresetId=" + (schedulerPreset != null ? schedulerPreset.getPresetId() : "null") +
                ", schedulerPresetName=" + (schedulerPreset != null ? schedulerPreset.getPresetName().getName() : "null") +
                '}';
    }
}
