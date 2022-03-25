package de.danielkoellgen.srscsdeckservice.domain.schedulerpreset.domain;

import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.util.List;

public class LapseSteps {

    @Getter
    @NotNull
    private final List<Duration> lapseSteps;

    private static final List<Duration> defaultVal = List.of(Duration.ofMinutes(10));

    private LapseSteps(@NotNull List<Duration> lapseSteps) {
        this.lapseSteps = lapseSteps;
    }

    public static LapseSteps makeDefaultSteps() {
        return new LapseSteps(defaultVal);
    }

    public static LapseSteps makeFromListOfDurations(@NotNull List<Duration> steps) {
        return new LapseSteps(steps);
    }
}
