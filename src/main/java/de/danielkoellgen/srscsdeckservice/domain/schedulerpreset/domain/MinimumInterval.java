package de.danielkoellgen.srscsdeckservice.domain.schedulerpreset.domain;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.Duration;

@EqualsAndHashCode
public class MinimumInterval {

    @Getter
    @Field("minimum_interval")
    private final @NotNull Duration minimumInterval;

    private static final Duration minimum = Duration.ofDays(5);

    @PersistenceConstructor
    public MinimumInterval(@NotNull Duration minimumInterval) {
        validateIntervalOrThrow(minimumInterval);
        this.minimumInterval = minimumInterval;
    }

    public static MinimumInterval makeFromDefault() {
        return new MinimumInterval(minimum);
    }

    public static MinimumInterval makeFromDuration(@NotNull Duration minimumInterval) {
        return new MinimumInterval(minimumInterval);
    }

    private static void validateIntervalOrThrow(@NotNull Duration interval) {
        if (interval.toMinutes() < minimum.toMinutes()) {
            throw new IllegalArgumentException(
                    "MinimumInterval is not supposed to fall below "+ minimum.toMinutes() +" minutes.");
        }
    }
}
