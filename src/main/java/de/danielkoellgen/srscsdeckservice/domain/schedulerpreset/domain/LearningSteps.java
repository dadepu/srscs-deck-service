package de.danielkoellgen.srscsdeckservice.domain.schedulerpreset.domain;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.util.List;

@EqualsAndHashCode
public class LearningSteps {

    @Getter
    @NotNull
    private final List<Duration> learningSteps;

    private static final List<Duration> defaultVal = List.of(
            Duration.ofHours(18),
            Duration.ofDays(5),
            Duration.ofDays(12),
            Duration.ofDays(30)
    );

    private LearningSteps(@NotNull List<Duration> steps) {
        validateStepsOrThrow(steps);
        this.learningSteps = steps;
    }

    public static LearningSteps makeDefaultSteps() {
        return new LearningSteps(defaultVal);
    }

    public static LearningSteps makeFromListOfDurations(@NotNull List<Duration> steps) {
        return new LearningSteps(steps);
    }

    private void validateStepsOrThrow(List<Duration> steps) {
        if (steps.isEmpty()) {
            throw new IllegalArgumentException("Must contain at least a single step.");
        }
    }
}
