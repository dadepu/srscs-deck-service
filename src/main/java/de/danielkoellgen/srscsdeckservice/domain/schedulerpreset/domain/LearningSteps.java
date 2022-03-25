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
    private final List<Duration> learningStepsInSeconds;

    private static final List<Duration> defaultVal = List.of(Duration.ofMinutes(10));

    private LearningSteps(@NotNull List<Duration> steps) {
        validateStepsOrThrow(steps);
        this.learningStepsInSeconds = steps;
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
