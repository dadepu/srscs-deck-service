package de.danielkoellgen.srscsdeckservice.domain.card.domain;

import de.danielkoellgen.srscsdeckservice.domain.schedulerpreset.domain.EaseFactor;
import de.danielkoellgen.srscsdeckservice.domain.schedulerpreset.domain.IntervalModifier;
import de.danielkoellgen.srscsdeckservice.domain.schedulerpreset.domain.LapseIntervalModifier;
import de.danielkoellgen.srscsdeckservice.domain.schedulerpreset.domain.MinimumInterval;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.Duration;

@EqualsAndHashCode
public class ReviewInterval {

    @Getter
    @NotNull
    private final Duration intervalDuration;

    private ReviewInterval(@NotNull Duration intervalDuration) {
        this.intervalDuration = intervalDuration;
    }

    public static ReviewInterval makeFromDuration(@NotNull Duration interval) {
        return new ReviewInterval(interval);
    }

    public ReviewInterval reviewInterval(@NotNull EaseFactor easeFactor, @Nullable IntervalModifier intervalModifier) {
        Double intervalMultiplier = 1.0 + (intervalModifier == null ? 0.0 : intervalModifier.getIntervalModifier());
        Duration newInterval = Duration.ofMinutes(
                Math.round(intervalDuration.toMinutes() * easeFactor.getEaseFactor() * intervalMultiplier)
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
}
