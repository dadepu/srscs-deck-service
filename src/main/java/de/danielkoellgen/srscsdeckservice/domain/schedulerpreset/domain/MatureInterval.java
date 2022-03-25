package de.danielkoellgen.srscsdeckservice.domain.schedulerpreset.domain;

import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;

public class MatureInterval {

    @Getter
    @NotNull
    private final Duration matureInterval;

    private static final Duration minimum = Duration.ofDays(1);

    private static final Duration defaultVal = Duration.ofDays(30);

    private MatureInterval(@NotNull Duration interval) {
        this.matureInterval = interval;
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
}
