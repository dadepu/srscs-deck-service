package de.danielkoellgen.srscsdeckservice.controller.schedulerpreset.dto;

import de.danielkoellgen.srscsdeckservice.domain.schedulerpreset.domain.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.Duration;
import java.util.List;
import java.util.UUID;

public record SchedulerPresetRequestDto(
    @NotNull
    UUID userId,

    @NotNull
    String name,

    @Nullable
    List<Long> learningSteps,

    @Nullable
    List<Long> lapseSteps,

    @Nullable
    Long minimumInterval,

    @Nullable
    Double easeFactor,

    @Nullable
    Double easyFactorModifier,

    @Nullable
    Double normalFactorModifier,

    @Nullable
    Double hardFactorModifier,

    @Nullable
    Double lapseFactorModifier,

    @Nullable
    Double easyIntervalModifier,

    @Nullable
    Double lapseIntervalModifier

) {

    public @NotNull UUID getUserId(){
        return userId;
    }

    public @NotNull PresetName getPresetName() {
        try {
            return new PresetName(name);
        } catch (Exception e) {
            throw new RuntimeException("Invalid name.");
        }
    }

    public @NotNull LearningSteps getLearningStepsOrDefault() {
        return learningSteps != null
                ? LearningSteps.makeFromListOfDurations(learningSteps.stream()
                        .map(Duration::ofMinutes)
                        .toList())
                : LearningSteps.makeDefaultSteps();
    }

    public @NotNull LapseSteps getLapseStepsOrDefault() {
        return lapseSteps != null
                ? LapseSteps.makeFromListOfDurations(lapseSteps.stream()
                        .map(Duration::ofMinutes)
                        .toList())
                : LapseSteps.makeDefaultSteps();
    }

    public @NotNull MinimumInterval getMinimumIntervalOrDefault() {
        return minimumInterval != null
                ? MinimumInterval.makeFromDuration(Duration.ofMinutes(minimumInterval))
                : MinimumInterval.makeFromDefault();
    }

    public @NotNull EaseFactor getEaseFactorOrDefault() {
        return easeFactor != null ?
                EaseFactor.makeFromDouble(easeFactor) : EaseFactor.makeFromDefault();
    }

    public @NotNull EasyFactorModifier getEasyFactorModifierOrDefault() {
        return easyFactorModifier != null
                ? EasyFactorModifier.makeFromValue(easyFactorModifier)
                : EasyFactorModifier.makeFromDefault();
    }

    public @NotNull NormalFactorModifier getNormalFactorModifierOrDefault() {
        return normalFactorModifier != null
                ? NormalFactorModifier.makeFromValue(normalFactorModifier)
                : NormalFactorModifier.makeFromDefault();
    }

    public @NotNull HardFactorModifier getHardFactorModifierOrDefault() {
        return hardFactorModifier != null
                ? HardFactorModifier.makeFromValue(hardFactorModifier)
                : HardFactorModifier.makeFromDefault();
    }

    public @NotNull LapseFactorModifier getLapseFactorModifierOrDefault() {
        return lapseFactorModifier != null
                ? LapseFactorModifier.makeFromDouble(lapseFactorModifier)
                : LapseFactorModifier.makeFromDefault();
    }

    public @NotNull EasyIntervalModifier getEasyIntervalModifierOrDefault() {
        return easyIntervalModifier != null
                ? EasyIntervalModifier.makeFromDouble(easyFactorModifier)
                : EasyIntervalModifier.makeFromDefault();
    }

    public @NotNull LapseIntervalModifier getLapseIntervalModifierOrDefault() {
        return lapseIntervalModifier != null
                ? LapseIntervalModifier.makeFromDouble(lapseIntervalModifier)
                : LapseIntervalModifier.makeFromDefault();
    }
}
