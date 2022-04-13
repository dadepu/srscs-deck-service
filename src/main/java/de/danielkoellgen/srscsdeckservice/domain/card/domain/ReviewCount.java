package de.danielkoellgen.srscsdeckservice.domain.card.domain;

import lombok.Getter;

public class ReviewCount {

    @Getter
    public final Integer reviewCount;

    private ReviewCount(Integer reviewCount) {
        this.reviewCount = reviewCount;
    }

    public static ReviewCount startNewCount() {
        return new ReviewCount(0);
    }

    public ReviewCount incrementedCount() {
        return new ReviewCount(reviewCount + 1);
    }
}
