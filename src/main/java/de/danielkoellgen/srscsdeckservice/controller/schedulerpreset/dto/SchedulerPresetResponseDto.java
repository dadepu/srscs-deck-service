package de.danielkoellgen.srscsdeckservice.controller.schedulerpreset.dto;

import de.danielkoellgen.srscsdeckservice.domain.schedulerpreset.domain.SchedulerPreset;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.util.List;
import java.util.UUID;

public record SchedulerPresetResponseDto(
    @NotNull
    UUID schedulerPresetId,

    @NotNull
    String name,

    @NotNull
    Boolean isActive,

    @NotNull
    List<Long> learningSteps,

    @NotNull
    List<Long> lapseSteps,

    @NotNull
    Long minimumInterval,

    @NotNull
    Double easeFactor,

    @NotNull
    Double easyFactorModifier,

    @NotNull
    Double normalFactorModifier,

    @NotNull
    Double hardFactorModifier,

    @NotNull
    Double lapseFactorModifier,

    @NotNull
    Double easyIntervalModifier,

    @NotNull
    Double lapseIntervalModifier

) {
    public SchedulerPresetResponseDto(@NotNull SchedulerPreset preset) {
        this(preset.getPresetId(),
                preset.getPresetName().getName(),
                preset.getIsActive(),
                preset.getLearningSteps().getLearningSteps().stream().map(Duration::toMinutes).toList(),
                preset.getLapseSteps().getLapseSteps().stream().map(Duration::toMinutes).toList(),
                preset.getMinimumInterval().getMinimumInterval().toMinutes(),
                preset.getEaseFactor().getEaseFactor(),
                preset.getEasyFactorModifier().getFactorModifier(),
                preset.getNormalFactorModifier().getFactorModifier(),
                preset.getHardFactorModifier().getFactorModifier(),
                preset.getLapseFactorModifier().getFactorModifier(),
                preset.getEasyIntervalModifier().getIntervalModifier(),
                preset.getLapseIntervalModifier().getIntervalModifier());
    }
}
