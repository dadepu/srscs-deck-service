package de.danielkoellgen.srscsdeckservice.domain.schedulerpreset.domain;

import de.danielkoellgen.srscsdeckservice.domain.user.domain.User;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.UUID;

@Document("scheduler_presets")
public class SchedulerPreset {

    @Id
    @Getter
    @NotNull
    private UUID presetId;

    @Getter
    @Nullable
    @Field("user_id")
    @DocumentReference(lazy = true)
    private User user;

    @Getter
    @NotNull
    @Field("is_active")
    private Boolean isActive = true;

    @Setter @Getter
    @NotNull
    @Field("preset_name")
    private PresetName presetName;

    @Setter @Getter
    @Nullable
    @Field("mature_interval")
    private MatureInterval matureInterval;

    @Setter @Getter
    @Nullable
    @Field("learning_steps")
    private LearningSteps learningSteps;

    @Setter @Getter
    @Nullable
    @Field("lapse_steps")
    private LapseSteps lapseSteps;

    @Setter @Getter
    @Nullable
    @Field("minimum_interval")
    private MinimumInterval minimumInterval;

    @Setter @Getter
    @Nullable
    @Field("ease_factor")
    private EaseFactor easeFactor;

    @Setter @Getter
    @Nullable
    @Field("easy_factor_modifier")
    private EasyFactorModifier easyFactorModifier;

    @Setter @Getter
    @Nullable
    @Field("normal_factor_modifier")
    private NormalFactorModifier normalFactorModifier;

    @Setter @Getter
    @Nullable
    @Field("hard_factor_modifier")
    private HardFactorModifier hardFactorModifier;

    @Setter @Getter
    @Nullable
    @Field("lapse_factor_modifier")
    private LapseFactorModifier lapseFactorModifier;

    @Setter @Getter
    @Nullable
    @Field("easy_interval_modifier")
    private EasyIntervalModifier easyIntervalModifier;

    @Setter @Getter
    @Nullable
    @Field("hard_interval_modifier")
    private HardIntervalModifier hardIntervalModifier;

    @Setter @Getter
    @Nullable
    @Field("lapse_interval_modifier")
    private LapseIntervalModifier lapseIntervalModifier;

    public void disablePreset() {
        isActive = false;
    }
}
