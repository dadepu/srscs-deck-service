package de.danielkoellgen.srscsdeckservice.domain.schedulerpreset.domain;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.mongodb.core.mapping.Field;

@EqualsAndHashCode
public class HardFactorModifier implements FactorModifier {

    @Getter
    @Field("hard_factor_modifier")
    private final @NotNull Double hardFactorModifier;

    private static final Double minimum = -0.5;

    private static final Double maximum = 0.0;

    private static final Double defaultVal = -0.1;

    @PersistenceConstructor
    public HardFactorModifier(@NotNull Double hardFactorModifier) {
        validateOrThrow(hardFactorModifier);
        this.hardFactorModifier = hardFactorModifier;
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

    @Override
    public String toString() {
        return "HardFactorModifier{" +
                "hardFactorModifier=" + hardFactorModifier +
                '}';
    }
}
