package de.danielkoellgen.srscsdeckservice.domain.card.domain;

import lombok.Getter;

public class PenaltyFactor {

    @Getter
    private final Double penaltyFactor;

    private static final Double minimum = 0.05;

    private static final Double maximum = 0.95;

    private static final Double defaultVal = 0.65;

    private PenaltyFactor(Double factor) {
        this.penaltyFactor = factor;
    }

    public static PenaltyFactor makeDefaultFactor() {
        return new PenaltyFactor(defaultVal);
    }
}
