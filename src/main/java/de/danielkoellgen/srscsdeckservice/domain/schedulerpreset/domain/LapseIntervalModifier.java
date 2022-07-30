package de.danielkoellgen.srscsdeckservice.domain.schedulerpreset.domain;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.mongodb.core.mapping.Field;

@EqualsAndHashCode
public class LapseIntervalModifier implements IntervalModifier {

    @Getter
    @Field("lapse_interval_modifier")
    private final @NotNull Double lapseIntervalModifier;

    private static final Double minimum = -0.95;

    private static final Double maximum = -0.05;

    private static final Double defaultVal = -0.6;

    private LapseIntervalModifier(@NotNull Double lapseIntervalModifier) {
        validateOrThrow(lapseIntervalModifier);
        this.lapseIntervalModifier = lapseIntervalModifier;
    }

    public static LapseIntervalModifier makeFromDefault() {
        return new LapseIntervalModifier(defaultVal);
    }

    public static LapseIntervalModifier makeFromDouble(@NotNull Double modifier) {
        return new LapseIntervalModifier(modifier);
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
        return lapseIntervalModifier;
    }

    @Override
    public String toString() {
        return "LapseIntervalModifier{" +
                "lapseIntervalModifier=" + lapseIntervalModifier +
                '}';
    }
}
