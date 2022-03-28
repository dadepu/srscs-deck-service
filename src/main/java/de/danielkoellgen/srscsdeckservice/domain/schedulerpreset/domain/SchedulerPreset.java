package de.danielkoellgen.srscsdeckservice.domain.schedulerpreset.domain;

import de.danielkoellgen.srscsdeckservice.domain.user.domain.User;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.UUID;

@Getter
@Document("scheduler_presets")
public class SchedulerPreset {

    @Id
    @NotNull
    private final UUID presetId;

    @Nullable
    @Transient
    private User user;

    @NotNull
    @Field("user")
    private EmbeddedUser embeddedUser;

    @NotNull
    @Field("is_active")
    private Boolean isActive;

    @Setter
    @NotNull
    @Field("preset_name")
    private PresetName presetName;


    @Setter
    @NotNull
    @Field("mature_interval")
    private MatureInterval matureInterval;

    @Setter
    @NotNull
    @Field("learning_steps")
    private LearningSteps learningSteps;

    @Setter
    @NotNull
    @Field("lapse_steps")
    private LapseSteps lapseSteps;

    @Setter
    @NotNull
    @Field("minimum_interval")
    private MinimumInterval minimumInterval;

    @Setter
    @NotNull
    @Field("ease_factor")
    private EaseFactor easeFactor;

    @Setter
    @NotNull
    @Field("easy_factor_modifier")
    private EasyFactorModifier easyFactorModifier;

    @Setter
    @NotNull
    @Field("normal_factor_modifier")
    private NormalFactorModifier normalFactorModifier;

    @Setter
    @NotNull
    @Field("hard_factor_modifier")
    private HardFactorModifier hardFactorModifier;

    @Setter
    @NotNull
    @Field("lapse_factor_modifier")
    private LapseFactorModifier lapseFactorModifier;

    @Setter
    @NotNull
    @Field("easy_interval_modifier")
    private EasyIntervalModifier easyIntervalModifier;

    @Setter
    @NotNull
    @Field("hard_interval_modifier")
    private HardIntervalModifier hardIntervalModifier;

    @Setter
    @NotNull
    @Field("lapse_interval_modifier")
    private LapseIntervalModifier lapseIntervalModifier;

    public void disablePreset() {
        isActive = false;
    }

    public SchedulerPreset(@NotNull PresetName presetName, @NotNull User user) {
        this.presetId = UUID.randomUUID();
        this.presetName = presetName;
        this.user = user;
        this.embeddedUser = new EmbeddedUser(user);
        this.isActive = true;

        this.matureInterval = MatureInterval.makeFromDefault();
        this.learningSteps = LearningSteps.makeDefaultSteps();
        this.lapseSteps = LapseSteps.makeDefaultSteps();
        this.minimumInterval = MinimumInterval.makeFromDefault();
        this.easeFactor = EaseFactor.makeFromDefault();
        this.easyFactorModifier = EasyFactorModifier.makeFromDefault();
        this.normalFactorModifier = NormalFactorModifier.makeFromDefault();
        this.hardFactorModifier = HardFactorModifier.makeFromDefault();
        this.lapseFactorModifier = LapseFactorModifier.makeFromDefault();
        this.easyIntervalModifier = EasyIntervalModifier.makeFromDefault();
        this.hardIntervalModifier = HardIntervalModifier.makeFromDefault();
        this.lapseIntervalModifier = LapseIntervalModifier.makeFromDefault();
    }

    @PersistenceConstructor
    public SchedulerPreset(
            @NotNull UUID presetId,
            @NotNull PresetName presetName,
            @NotNull EmbeddedUser embeddedUser,
            @NotNull Boolean isActive,
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
        this.embeddedUser = embeddedUser;
        this.isActive = isActive;
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

    public void updateEmbeddedUser(@NotNull User user) {
        this.embeddedUser = new EmbeddedUser(user);
    }
}
