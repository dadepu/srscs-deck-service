package de.danielkoellgen.srscsdeckservice.controller.card.dto;

import de.danielkoellgen.srscsdeckservice.domain.card.domain.ReviewState;
import de.danielkoellgen.srscsdeckservice.domain.card.domain.Scheduler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public record SchedulerDto(

    @NotNull
    UUID presetId,

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
        this(scheduler.getEmbeddedSchedulerPreset().getPresetId(),
                scheduler.getEmbeddedSchedulerPreset().getPresetName().getName(),
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
