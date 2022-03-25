package de.danielkoellgen.srscsdeckservice.domain.schedulerpreset.domain;

import lombok.EqualsAndHashCode;
import org.jetbrains.annotations.NotNull;

@EqualsAndHashCode
public class LapseIntervalModifier implements IntervalModifier {

    @NotNull
    private final Double lapseIntervalModifier;

    private static final Double minimum = 0.05;

    private static final Double maximum = 0.95;

    private static final Double defaultVal = 0.65;

    private LapseIntervalModifier(@NotNull Double modifier) {
        validateOrThrow(modifier);
        this.lapseIntervalModifier = modifier;
    }

    public static LapseIntervalModifier makeDefaultFactor() {
        return new LapseIntervalModifier(defaultVal);
    }

    public static LapseIntervalModifier makeFromDouble(@NotNull Double modifier) {
        return new LapseIntervalModifier(modifier);
    }

    private static void validateOrThrow(@NotNull Double modifier) {
        if (modifier < minimum) {
            throw new IllegalArgumentException("Modifier may not be below " + minimum + ".");
        }
        if (modifier > maximum) {
            throw new IllegalArgumentException("Modifier may not be above " + maximum + ".");
        }
    }

    @Override
    public @NotNull Double getIntervalModifier() {
        return lapseIntervalModifier;
    }
}
