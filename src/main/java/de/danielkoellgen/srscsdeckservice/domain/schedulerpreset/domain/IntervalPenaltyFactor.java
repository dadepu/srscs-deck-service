package de.danielkoellgen.srscsdeckservice.domain.schedulerpreset.domain;

import lombok.Getter;

public class IntervalPenaltyFactor {

    @Getter
    private final Double penaltyFactor;

    private static final Double minimum = 0.05;

    private static final Double maximum = 0.95;

    private static final Double defaultVal = 0.65;

    private IntervalPenaltyFactor(Double factor) {
        this.penaltyFactor = factor;
    }

    public static IntervalPenaltyFactor makeDefaultFactor() {
        return new IntervalPenaltyFactor(defaultVal);
    }
}
