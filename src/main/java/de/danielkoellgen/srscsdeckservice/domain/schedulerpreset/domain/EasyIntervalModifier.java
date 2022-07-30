package de.danielkoellgen.srscsdeckservice.domain.schedulerpreset.domain;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.mongodb.core.mapping.Field;

@EqualsAndHashCode
public class EasyIntervalModifier implements IntervalModifier {

    @Getter
    @Field("easy_interval_modifier")
    private final @NotNull Double easyIntervalModifier;

    private static final Double minimum = 0.0;

    private static final Double maximum = 0.5;

    private static final Double defaultVal = 0.2;

    @PersistenceConstructor
    public EasyIntervalModifier(@NotNull Double easyIntervalModifier) {
        validateOrThrow(easyIntervalModifier);
        this.easyIntervalModifier = easyIntervalModifier;
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

    @Override
    public String toString() {
        return "EasyIntervalModifier{" +
                "easyIntervalModifier=" + easyIntervalModifier +
                '}';
    }
}
