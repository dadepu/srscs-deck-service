package de.danielkoellgen.srscsdeckservice.domain.schedulerpreset.domain;

import lombok.EqualsAndHashCode;
import org.jetbrains.annotations.NotNull;

@EqualsAndHashCode
public class EasyFactorModifier implements FactorModifier {

    @NotNull
    private final Double easyFactorModifier;

    private static final Double minimum = 0.0;

    private static final Double maximum = 0.5;

    private static final Double defaultVal = 0.25;

    private EasyFactorModifier(@NotNull Double modifier) {
        validateOrThrow(modifier);
        this.easyFactorModifier = modifier;
    }

    private static void validateOrThrow(@NotNull Double modifier) {
        if (modifier < minimum) {
            throw new IllegalArgumentException("Modifier may not be below "+ minimum +".");
        }
        if (modifier > maximum) {
            throw new IllegalArgumentException("Modifier may not be above "+ maximum +".");
        }
    }

    public static EasyFactorModifier makeFromDefault() {
        return new EasyFactorModifier(defaultVal);
    }

    public static EasyFactorModifier makeFromValue(@NotNull Double modifier) {
        return new EasyFactorModifier(modifier);
    }

    @Override
    public @NotNull Double getFactorModifier() {
        return easyFactorModifier;
    }
}
