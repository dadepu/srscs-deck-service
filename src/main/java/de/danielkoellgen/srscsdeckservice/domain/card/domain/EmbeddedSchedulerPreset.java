package de.danielkoellgen.srscsdeckservice.domain.card.domain;

import de.danielkoellgen.srscsdeckservice.domain.schedulerpreset.domain.*;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.Unwrapped;

import java.util.UUID;

@Getter
public class EmbeddedSchedulerPreset {

    @NotNull
    @Field("_id")
    private final UUID presetId;

    @NotNull
    @Unwrapped.Nullable
    private final PresetName presetName;

    @NotNull
    @Unwrapped.Nullable
    private final MatureInterval matureInterval;

    @NotNull
    @Unwrapped.Nullable
    private final LearningSteps learningSteps;

    @NotNull
    @Unwrapped.Nullable
    private final LapseSteps lapseSteps;

    @NotNull
    @Unwrapped.Nullable
    private final MinimumInterval minimumInterval;

    @NotNull
    @Unwrapped.Nullable
    private final EaseFactor easeFactor;

    @NotNull
    @Unwrapped.Nullable
    private final EasyFactorModifier easyFactorModifier;

    @NotNull
    @Unwrapped.Nullable
    private final NormalFactorModifier normalFactorModifier;

    @NotNull
    @Unwrapped.Nullable
    private final HardFactorModifier hardFactorModifier;

    @NotNull
    @Unwrapped.Nullable
    private final LapseFactorModifier lapseFactorModifier;

    @NotNull
    @Unwrapped.Nullable
    private final EasyIntervalModifier easyIntervalModifier;

    @NotNull
    @Unwrapped.Nullable
    private final HardIntervalModifier hardIntervalModifier;

    @NotNull
    @Unwrapped.Nullable
    private final LapseIntervalModifier lapseIntervalModifier;

    @PersistenceConstructor
    public EmbeddedSchedulerPreset(
            @NotNull UUID presetId,
            @NotNull PresetName presetName,
            @NotNull MatureInterval matureInterval,
            @NotNull LearningSteps learningSteps,
            @NotNull LapseSteps lapseSteps,
            @NotNull MinimumInterval minimumInterval,
            @NotNull EaseFactor easeFactor,
            @NotNull EasyFactorModifier easyFactorModifier,
            @NotNull NormalFactorModifier normalFactorModifier,
            @NotNull HardFactorModifier hardFactorModifier,
            @NotNull LapseFactorModifier lapseFactorModifier,
            @NotNull EasyIntervalModifier easyIntervalModifier,
            @NotNull HardIntervalModifier hardIntervalModifier,
            @NotNull LapseIntervalModifier lapseIntervalModifier
    ) {
        this.presetId = presetId;
        this.presetName = presetName;
        this.matureInterval = matureInterval;
        this.learningSteps = learningSteps;
        this.lapseSteps = lapseSteps;
        this.minimumInterval = minimumInterval;
        this.easeFactor = easeFactor;
        this.easyFactorModifier = easyFactorModifier;
        this.normalFactorModifier = normalFactorModifier;
        this.hardFactorModifier = hardFactorModifier;
        this.lapseFactorModifier = lapseFactorModifier;
        this.easyIntervalModifier = easyIntervalModifier;
        this.hardIntervalModifier = hardIntervalModifier;
        this.lapseIntervalModifier = lapseIntervalModifier;
    }

    public EmbeddedSchedulerPreset(@NotNull SchedulerPreset schedulerPreset) {
        this.presetId = schedulerPreset.getPresetId();
        this.presetName = schedulerPreset.getPresetName();
        this.matureInterval = schedulerPreset.getMatureInterval();
        this.learningSteps = schedulerPreset.getLearningSteps();
        this.lapseSteps = schedulerPreset.getLapseSteps();
        this.minimumInterval = schedulerPreset.getMinimumInterval();
        this.easeFactor = schedulerPreset.getEaseFactor();
        this.easyFactorModifier = schedulerPreset.getEasyFactorModifier();
        this.normalFactorModifier = schedulerPreset.getNormalFactorModifier();
        this.hardFactorModifier = schedulerPreset.getHardFactorModifier();
        this.lapseFactorModifier = schedulerPreset.getLapseFactorModifier();
        this.easyIntervalModifier = schedulerPreset.getEasyIntervalModifier();
        this.hardIntervalModifier = schedulerPreset.getHardIntervalModifier();
        this.lapseIntervalModifier = schedulerPreset.getLapseIntervalModifier();
    }
}
