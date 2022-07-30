package de.danielkoellgen.srscsdeckservice.domain.schedulerpreset.domain;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.mongodb.core.mapping.Field;

@EqualsAndHashCode
public class EaseFactor {

    @Getter
    @Field("ease_factor")
    private final @NotNull Double easeFactor;

    private static final Double minimum = 1.0;

    private static final Double maximum = 3.0;

    private static final Double defaultVal = 2.0;

    @PersistenceConstructor
    public EaseFactor(@NotNull Double easeFactor) {
        if (easeFactor < minimum) {
            this.easeFactor = minimum;
            return;
        }
        if (easeFactor > maximum) {
            this.easeFactor = maximum;
            return;
        }
        this.easeFactor = easeFactor;
    }

    public static EaseFactor makeFromDefault() {
        return new EaseFactor(defaultVal);
    }

    public static EaseFactor makeFromDouble(@NotNull Double modifier) {
        return new EaseFactor(modifier);
    }

    public EaseFactor modifiedFactor(@NotNull FactorModifier modifier) {
        return new EaseFactor(easeFactor + modifier.getFactorModifier());
    }

    @Override
    public String toString() {
        return "EaseFactor{" +
                "easeFactor=" + easeFactor +
                '}';
    }
}
