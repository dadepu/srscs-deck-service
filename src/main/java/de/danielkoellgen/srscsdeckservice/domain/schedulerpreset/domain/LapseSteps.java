package de.danielkoellgen.srscsdeckservice.domain.schedulerpreset.domain;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.Duration;
import java.util.List;

@EqualsAndHashCode
public class LapseSteps {

    @Getter
    @Field("lapse_steps")
    private final @NotNull List<Duration> lapseSteps;

    private static final List<Duration> defaultVal = List.of(
            Duration.ofHours(18),
            Duration.ofDays(7)
    );

    @PersistenceConstructor
    public LapseSteps(@NotNull List<Duration> lapseSteps) {
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

    @Override
    public String toString() {
        return "LapseSteps{" +
                "lapseSteps=" + lapseSteps +
                '}';
    }
}
