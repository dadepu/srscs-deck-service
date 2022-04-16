package de.danielkoellgen.srscsdeckservice.domain.schedulerpreset.domain;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.mongodb.core.mapping.Field;

@EqualsAndHashCode
public class NormalFactorModifier implements FactorModifier {

    @Getter
    @Field("normal_factor_modifier")
    private final @NotNull Double normalFactorModifier;

    private static final Double minimum = -0.1;

    private static final Double maximum = 0.2;

    private static final Double defaultVal = 0.05;

    @PersistenceConstructor
    public NormalFactorModifier(@NotNull Double normalFactorModifier) {
        validateOrThrow(normalFactorModifier);
        this.normalFactorModifier = normalFactorModifier;
    }

    public static NormalFactorModifier makeFromDefault() {
        return new NormalFactorModifier(defaultVal);
    }

    public static NormalFactorModifier makeFromValue(@NotNull Double value) {
        return new NormalFactorModifier(value);
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
        return normalFactorModifier;
    }
}
