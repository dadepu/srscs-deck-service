package de.danielkoellgen.srscsdeckservice.domain.schedulerpreset.domain;

import lombok.Getter;
import org.jetbrains.annotations.NotNull;

public class EaseFactor {

    @Getter
    @NotNull
    private final Double easeFactor;

    private static final Double minimum = 1.0;

    private static final Double maximum = 3.0;

    private static final Double defaultVal = 2.0;

    private EaseFactor(@NotNull Double easeFactor) {
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
}
