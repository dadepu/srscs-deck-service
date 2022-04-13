package de.danielkoellgen.srscsdeckservice.domain.card.domain;

import org.jetbrains.annotations.NotNull;

public enum ReviewState {

    LEARNING, GRADUATED, LAPSING;

    public static String mapToString(@NotNull ReviewState reviewState) {
        return switch(reviewState) {
            case LEARNING   -> "learning";
            case GRADUATED  -> "graduated";
            case LAPSING    -> "lapsing";
        };
    }
}
