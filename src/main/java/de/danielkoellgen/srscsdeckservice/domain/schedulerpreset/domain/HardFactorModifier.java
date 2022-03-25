package de.danielkoellgen.srscsdeckservice.domain.schedulerpreset.domain;

import org.jetbrains.annotations.NotNull;

public class HardFactorModifier implements FactorModifier {

    @NotNull
    private final Double hardFactorModifier;

    private static final Double minimum = -0.5;

    private static final Double maximum = 0.0;

    private static final Double defaultVal = -0.1;

    private HardFactorModifier(@NotNull Double modifier) {
        validateOrThrow(modifier);
        this.hardFactorModifier = modifier;
    }

    public static HardFactorModifier makeFromDefault() {
        return new HardFactorModifier(defaultVal);
    }

    public static HardFactorModifier makeFromValue(@NotNull Double modifier) {
        return new HardFactorModifier(modifier);
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
    public @NotNull Double getFactorModifier() {
        return hardFactorModifier;
    }
}
