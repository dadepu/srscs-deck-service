package de.danielkoellgen.srscsdeckservice.domain.card.domain;

import de.danielkoellgen.srscsdeckservice.domain.schedulerpreset.domain.LapseSteps;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;

@EqualsAndHashCode
public class LapseStep {

    @Getter
    @NotNull
    private final Integer stepIndex;

    @Getter
    @NotNull
    private final LapseSteps lapseSteps;

    @Getter
    @NotNull
    private final ReviewInterval penalisedPreLapseReviewInterval;

    private LapseStep(@NotNull Integer index, @NotNull LapseSteps lapseSteps,
            @NotNull ReviewInterval penalisedPreLapseReviewInterval) {
        if (index < 0 || index > lapseSteps.getLapseSteps().size()) {
            throw new IllegalArgumentException("Lapse index out of bounds");
        }
        this.stepIndex = index;
        this.lapseSteps = lapseSteps;
        this.penalisedPreLapseReviewInterval = penalisedPreLapseReviewInterval;
    }

    public static LapseStep startLapsePath(@NotNull LapseSteps lapseSteps,
            @NotNull ReviewInterval penalisedPreLapseReviewInterval) {
        return new LapseStep(0, lapseSteps, penalisedPreLapseReviewInterval);
    }

    public Boolean hasNextStep(@NotNull LapseSteps updatedLapseSteps) {
        return stepIndex < updatedLapseSteps.getLapseSteps().size();
    }

    public LapseStep takeNextStep(@NotNull LapseSteps updatedLapseSteps) {
        return new LapseStep(stepIndex + 1, updatedLapseSteps, penalisedPreLapseReviewInterval);
    }

    public Duration getInterval() {
        return lapseSteps.getLapseSteps().get(stepIndex);
    }
}
