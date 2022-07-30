package de.danielkoellgen.srscsdeckservice.domain.schedulerpreset.domain;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.mongodb.core.mapping.Field;

@EqualsAndHashCode
public class HardIntervalModifier implements IntervalModifier {

    @Getter
    @Field("hard_interval_modifier")
    private final @NotNull Double hardIntervalModifier;

    private static final Double minimum = -0.75;

    private static final Double maximum = 0.0;

    private static final Double defaultVal = -0.25;

    @PersistenceConstructor
    public HardIntervalModifier(@NotNull Double hardIntervalModifier) {
        validateOrThrow(hardIntervalModifier);
        this.hardIntervalModifier = hardIntervalModifier;
    }

    public static HardIntervalModifier makeFromDefault() {
        return new HardIntervalModifier(defaultVal);
    }

    public static HardIntervalModifier makeFromDouble(@NotNull Double modifier) {
        return new HardIntervalModifier(modifier);
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
        return hardIntervalModifier;
    }

    @Override
    public String toString() {
        return "HardIntervalModifier{" +
                "hardIntervalModifier=" + hardIntervalModifier +
                '}';
    }
}
