package de.danielkoellgen.srscsdeckservice.domain.schedulerpreset.domain;

import org.jetbrains.annotations.NotNull;

public class EasyIntervalModifier implements IntervalModifier {

    @NotNull
    private final Double easyIntervalModifier;

    private static final Double minimum = 0.0;

    private static final Double maximum = 0.5;

    private static final Double defaultVal = 0.2;

    private EasyIntervalModifier(@NotNull Double modifier) {
        validateOrThrow(modifier);
        this.easyIntervalModifier = modifier;
    }

    public static EasyIntervalModifier makeFromDefault() {
        return new EasyIntervalModifier(defaultVal);
    }

    public static EasyIntervalModifier makeFromDouble(@NotNull Double modifier) {
        return new EasyIntervalModifier(modifier);
    }

    private static void validateOrThrow(@NotNull Double modifier) {
        if (modifier < minimum) {
            throw new IllegalArgumentException("IntervalModifier may not be below " + minimum + ".");
        }
        if (modifier > maximum) {
            throw new IllegalArgumentException("IntervalModifier may not be above " + maximum + ".");
        }
    }

    @Override
    public @NotNull Double getIntervalModifier() {
        return easyIntervalModifier;
    }
}
