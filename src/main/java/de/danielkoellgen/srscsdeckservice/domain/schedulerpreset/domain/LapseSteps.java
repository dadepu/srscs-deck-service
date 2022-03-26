package de.danielkoellgen.srscsdeckservice.domain.schedulerpreset.domain;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.util.List;

@EqualsAndHashCode
public class LapseSteps {

    @Getter
    @NotNull
    private final List<Duration> lapseSteps;

    private static final List<Duration> defaultVal = List.of(
            Duration.ofHours(18),
            Duration.ofDays(7)
    );

    private LapseSteps(@NotNull List<Duration> lapseSteps) {
        this.lapseSteps = lapseSteps;
    }

    public static LapseSteps makeDefaultSteps() {
        return new LapseSteps(defaultVal);
    }

    public static LapseSteps makeFromListOfDurations(@NotNull List<Duration> steps) {
        return new LapseSteps(steps);
    }

    public Duration getLastStep() {
        return lapseSteps.get(lapseSteps.size() - 1);
    }
}
