package de.danielkoellgen.srscsdeckservice.domain.schedulerpreset.domain;

import lombok.EqualsAndHashCode;
import org.jetbrains.annotations.NotNull;

@EqualsAndHashCode
public class LapseFactorModifier implements FactorModifier {

    @NotNull
    private final Double lapseFactorModifier;

    private static final Double minimum = -0.75;

    private static final Double maximum = 0.0;

    private static final Double defaultVal = -0.2;

    private LapseFactorModifier(@NotNull Double modifier) {
        validateOrThrow(modifier);
        this.lapseFactorModifier = modifier;
    }

    public static LapseFactorModifier makeFromDefault() {
        return new LapseFactorModifier(defaultVal);
    }

    public static LapseFactorModifier makeFromDouble(@NotNull Double modifier) {
        return new LapseFactorModifier(modifier);
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
        return lapseFactorModifier;
    }
}
