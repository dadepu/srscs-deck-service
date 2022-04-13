package de.danielkoellgen.srscsdeckservice.controller.card.dto;

import de.danielkoellgen.srscsdeckservice.domain.card.domain.ReviewAction;
import org.jetbrains.annotations.NotNull;

public record ReviewRequestDto(

    @NotNull
    String reviewAction

) {
    public @NotNull ReviewAction getReviewAction() {
        return switch(reviewAction) {
            case "easy"     -> ReviewAction.EASY;
            case "normal"   -> ReviewAction.NORMAL;
            case "hard"     -> ReviewAction.HARD;
            case "lapse"    -> ReviewAction.LAPSE;
            default -> throw new RuntimeException("Invalid review-action.");
        };
    }
}
