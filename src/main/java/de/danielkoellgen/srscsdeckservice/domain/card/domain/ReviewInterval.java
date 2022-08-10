package de.danielkoellgen.srscsdeckservice.domain.card.domain;

import de.danielkoellgen.srscsdeckservice.domain.schedulerpreset.domain.EaseFactor;
import de.danielkoellgen.srscsdeckservice.domain.schedulerpreset.domain.IntervalModifier;
import de.danielkoellgen.srscsdeckservice.domain.schedulerpreset.domain.LapseIntervalModifier;
import de.danielkoellgen.srscsdeckservice.domain.schedulerpreset.domain.MinimumInterval;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

@EqualsAndHashCode
public class ReviewInterval {

    @Getter
    @Field("interval_duration")
    private final @NotNull Duration intervalDuration;

    @PersistenceConstructor
    public ReviewInterval(@NotNull Duration intervalDuration) {
        this.intervalDuration = intervalDuration;
    }

    public static ReviewInterval makeFromDuration(@NotNull Duration interval) {
        return new ReviewInterval(interval);
    }

    public static ReviewInterval makeFromDurationWithModifier(@NotNull Duration interval,
            @NotNull IntervalModifier intervalModifier) {
        Duration newInterval = Duration.ofMinutes(
                Math.round(interval.toMinutes() * 1.0 + intervalModifier.getIntervalModifier())
        );
        return new ReviewInterval(newInterval);
    }

    public ReviewInterval reviewInterval(@NotNull EaseFactor easeFactor, @Nullable IntervalModifier intervalModifier) {
        Double intervalMultiplier = 1.0 + (intervalModifier == null ? 0.0 : intervalModifier.getIntervalModifier());
        Duration newInterval = Duration.ofMinutes(
                Math.round(intervalDuration.toMinutes() * easeFactor.getEaseFactor() * intervalMultiplier)
        );
        return new ReviewInterval(newInterval);
    }

    public ReviewInterval modifyInterval(@NotNull IntervalModifier intervalModifier) {
        Duration newInterval = Duration.ofMinutes(
                Math.round(intervalDuration.toMinutes() * (1.0 + intervalModifier.getIntervalModifier()))
        );
        return new ReviewInterval(newInterval);
    }

    public ReviewInterval lapsedInterval(@NotNull MinimumInterval minimumInterval, @NotNull LapseIntervalModifier modifier) {
        Duration newInterval = Duration.ofMinutes(
                Math.round(intervalDuration.toMinutes() * modifier.getIntervalModifier())
        );
        return newInterval.toMinutes() > minimumInterval.getMinimumInterval().toMinutes() ?
                new ReviewInterval(newInterval) : new ReviewInterval(minimumInterval.getMinimumInterval());
    }

    @Override
    public String toString() {
        return "ReviewInterval{" +
                "intervalDuration=" + intervalDuration +
                '}';
    }
}
