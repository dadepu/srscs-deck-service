package de.danielkoellgen.srscsdeckservice.domain.schedulerpreset.domain;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

@EqualsAndHashCode
public class MatureInterval {

    @Getter
    @Field("mature_interval")
    private final @NotNull Duration matureInterval;

    private static final Duration minimum = Duration.ofDays(1);

    private static final Duration defaultVal = Duration.ofDays(30);

    @PersistenceConstructor
    public MatureInterval(@NotNull Duration matureInterval) {
        validateOrThrow(matureInterval);
        this.matureInterval = matureInterval;
    }

    public static MatureInterval makeFromDefault() {
        return new MatureInterval(defaultVal);
    }

    public static MatureInterval makeFromDuration(@NotNull Duration interval) {
        return new MatureInterval(interval);
    }

    private static void validateOrThrow(@NotNull Duration interval) {
        if (interval.toMinutes() < minimum.toMinutes()) {
            throw new IllegalArgumentException("Interval may not be below " + minimum.toMinutes() + " minutes.");
        }
    }

    @Override
    public String toString() {
        return "MatureInterval{" +
                "matureInterval=" + matureInterval +
                '}';
    }
}
