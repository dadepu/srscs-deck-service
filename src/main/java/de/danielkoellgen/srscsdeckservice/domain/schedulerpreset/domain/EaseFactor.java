package de.danielkoellgen.srscsdeckservice.domain.schedulerpreset.domain;

import lombok.Getter;

public class EaseFactor {

    @Getter
    private final Double easeFactor;

    private static final Double minimum = 1.0;

    private static final Double maximum = 3.0;

    private static final Double defaultVal = 2.0;

    private EaseFactor(Double easeFactor) {
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

    public static EaseFactor makeDefaultFactor() {
        return new EaseFactor(defaultVal);
    }
}
