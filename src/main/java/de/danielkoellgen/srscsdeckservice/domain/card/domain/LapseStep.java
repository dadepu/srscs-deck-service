package de.danielkoellgen.srscsdeckservice.domain.card.domain;

import de.danielkoellgen.srscsdeckservice.domain.schedulerpreset.domain.LapseSteps;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.Duration;

@EqualsAndHashCode
public class LapseStep {

    @Getter
    @Field("step_index")
    private final @NotNull Integer stepIndex;

    @Getter
    @Field("lapse_steps")
    private final @NotNull LapseSteps lapseSteps;

    @Getter
    @Field("penalised_pre_lapse_review_interval")
    private final @NotNull ReviewInterval penalisedPreLapseReviewInterval;

    @PersistenceConstructor
    public LapseStep(@NotNull Integer stepIndex, @NotNull LapseSteps lapseSteps,
            @NotNull ReviewInterval penalisedPreLapseReviewInterval) {
        if (stepIndex < 0 || stepIndex > lapseSteps.getLapseSteps().size()) {
            throw new IllegalArgumentException("Lapse index out of bounds");
        }
        this.stepIndex = stepIndex;
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
