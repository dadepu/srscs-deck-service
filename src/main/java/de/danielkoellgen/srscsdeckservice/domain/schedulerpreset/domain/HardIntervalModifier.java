package de.danielkoellgen.srscsdeckservice.domain.schedulerpreset.domain;

import org.jetbrains.annotations.NotNull;

public class HardIntervalModifier implements IntervalModifier {

    @NotNull
    private final Double hardIntervalModifier;

    private static final Double minimum = -0.75;

    private static final Double maximum = 0.0;

    private static final Double defaultVal = -0.5;

    private HardIntervalModifier(@NotNull Double modifier) {
        validateOrThrow(modifier);
        this.hardIntervalModifier = modifier;
    }

    public static HardIntervalModifier makeFromDefault() {
        return new HardIntervalModifier(defaultVal);
    }

    public static HardIntervalModifier makeFromDouble(@NotNull Double modifier) {
        return new HardIntervalModifier(modifier);
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
        return hardIntervalModifier;
    }
}
