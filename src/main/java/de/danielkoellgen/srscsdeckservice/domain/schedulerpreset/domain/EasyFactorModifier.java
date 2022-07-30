package de.danielkoellgen.srscsdeckservice.domain.schedulerpreset.domain;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.mongodb.core.mapping.Field;

@EqualsAndHashCode
public class EasyFactorModifier implements FactorModifier {

    @Getter
    @Field("easy_factor_modifier")
    private final @NotNull Double easyFactorModifier;

    private static final Double minimum = 0.0;

    private static final Double maximum = 0.5;

    private static final Double defaultVal = 0.2;

    @PersistenceConstructor
    public EasyFactorModifier(@NotNull Double easyFactorModifier) {
        validateOrThrow(easyFactorModifier);
        this.easyFactorModifier = easyFactorModifier;
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

    @Override
    public String toString() {
        return "EasyFactorModifier{" +
                "easyFactorModifier=" + easyFactorModifier +
                '}';
    }
}
