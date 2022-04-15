package de.danielkoellgen.srscsdeckservice.controller.card.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import de.danielkoellgen.srscsdeckservice.domain.card.domain.ReviewAction;
import org.jetbrains.annotations.NotNull;

public record ReviewRequestDto(

    @NotNull
    String reviewAction

) {
    @JsonIgnore
    public @NotNull ReviewAction getMappedReviewAction() {
        return switch(reviewAction) {
            case "easy"     -> ReviewAction.EASY;
            case "normal"   -> ReviewAction.NORMAL;
            case "hard"     -> ReviewAction.HARD;
            case "lapse"    -> ReviewAction.LAPSE;
            default -> throw new RuntimeException("Invalid review-action.");
        };
    }
}
