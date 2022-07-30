package de.danielkoellgen.srscsdeckservice.domain.schedulerpreset.domain;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.mongodb.core.mapping.Field;

@EqualsAndHashCode
public class LapseFactorModifier implements FactorModifier {

    @Getter
    @Field("lapse_factor_modifier")
    private final @NotNull Double lapseFactorModifier;

    private static final Double minimum = -0.75;

    private static final Double maximum = 0.0;

    private static final Double defaultVal = -0.3;

    @PersistenceConstructor
    public LapseFactorModifier(@NotNull Double lapseFactorModifier) {
        validateOrThrow(lapseFactorModifier);
        this.lapseFactorModifier = lapseFactorModifier;
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

    @Override
    public String toString() {
        return "LapseFactorModifier{" +
                "lapseFactorModifier=" + lapseFactorModifier +
                '}';
    }
}
