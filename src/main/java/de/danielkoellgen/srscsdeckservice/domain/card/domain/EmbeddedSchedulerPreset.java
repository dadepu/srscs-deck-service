package de.danielkoellgen.srscsdeckservice.domain.card.domain;

import de.danielkoellgen.srscsdeckservice.domain.schedulerpreset.domain.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.Unwrapped;

import java.util.UUID;

@Getter
@EqualsAndHashCode
public class EmbeddedSchedulerPreset {

    @Field("_id")
    private final @NotNull UUID presetId;

    @Field("preset_name")
    private final @NotNull PresetName presetName;

    @Field("mature_interval")
    private final @NotNull MatureInterval matureInterval;

    @Field("learning_steps")
    private final @NotNull LearningSteps learningSteps;

    @Field("lapse_steps")
    private final @NotNull LapseSteps lapseSteps;

    @Field("minimum_interval")
    private final @NotNull MinimumInterval minimumInterval;

    @Field("ease_factor")
    private final @NotNull EaseFactor easeFactor;

    @Field("easy_factor_modifier")
    private final @NotNull EasyFactorModifier easyFactorModifier;

    @Field("normal_factor_modifier")
    private final @NotNull NormalFactorModifier normalFactorModifier;

    @Field("hard_factor_modifier")
    private final @NotNull HardFactorModifier hardFactorModifier;

    @Field("lapse_factor_modifier")
    private final @NotNull LapseFactorModifier lapseFactorModifier;

    @Field("easy_interval_modifier")
    private final @NotNull EasyIntervalModifier easyIntervalModifier;

    @Field("hard_interval_modifier")
    private final @NotNull HardIntervalModifier hardIntervalModifier;

    @Field("lapse_interval_modifier")
    private final @NotNull LapseIntervalModifier lapseIntervalModifier;

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

    @Override
    public String toString() {
        return "EmbeddedSchedulerPreset{" +
                "presetId=" + presetId +
                ", presetName=" + presetName +
                ", matureInterval=" + matureInterval +
                ", learningSteps=" + learningSteps +
                ", lapseSteps=" + lapseSteps +
                ", minimumInterval=" + minimumInterval +
                ", easeFactor=" + easeFactor +
                ", easyFactorModifier=" + easyFactorModifier +
                ", normalFactorModifier=" + normalFactorModifier +
                ", hardFactorModifier=" + hardFactorModifier +
                ", lapseFactorModifier=" + lapseFactorModifier +
                ", easyIntervalModifier=" + easyIntervalModifier +
                ", hardIntervalModifier=" + hardIntervalModifier +
                ", lapseIntervalModifier=" + lapseIntervalModifier +
                '}';
    }
}
