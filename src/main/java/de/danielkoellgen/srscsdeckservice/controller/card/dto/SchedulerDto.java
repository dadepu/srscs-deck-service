package de.danielkoellgen.srscsdeckservice.controller.card.dto;

import de.danielkoellgen.srscsdeckservice.domain.card.domain.ReviewState;
import de.danielkoellgen.srscsdeckservice.domain.card.domain.Scheduler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public record SchedulerDto(

    @NotNull
    String presetName,

    @NotNull
    String reviewState,

    @NotNull
    Integer reviewCount,

    @NotNull
    String lastReview,

    @NotNull
    String nextReview,

    @NotNull
    Double easeFactor,

    @NotNull
    Long currentInterval,

    @Nullable
    Integer learningStep,

    @Nullable
    Integer lapseStep

) {
    public SchedulerDto(@NotNull Scheduler scheduler) {
        this(scheduler.getEmbeddedSchedulerPreset().getPresetName().getName(),
                ReviewState.mapToString(scheduler.getReviewState()),
                scheduler.getReviewCount().reviewCount,
                scheduler.getLastReview().getFormatted(),
                scheduler.getNextReview().getFormatted(),
                scheduler.getEaseFactor().getEaseFactor(),
                scheduler.getCurrentInterval().getIntervalDuration().toMinutes(),
                scheduler.getLearningStep().getStepIndex(),
                (scheduler.getLapseStep() != null ? scheduler.getLapseStep().getStepIndex() : null)
        );
    }
}
