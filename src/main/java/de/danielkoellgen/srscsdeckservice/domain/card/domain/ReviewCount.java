package de.danielkoellgen.srscsdeckservice.domain.card.domain;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.mongodb.core.mapping.Field;

@EqualsAndHashCode
public class ReviewCount {

    @Getter
    @Field("review_count")
    public final @NotNull Integer reviewCount;

    @PersistenceConstructor
    public ReviewCount(@NotNull Integer reviewCount) {
        this.reviewCount = reviewCount;
    }

    public static ReviewCount startNewCount() {
        return new ReviewCount(0);
    }

    public ReviewCount incrementedCount() {
        return new ReviewCount(reviewCount + 1);
    }

    @Override
    public String toString() {
        return "ReviewCount{" +
                "reviewCount=" + reviewCount +
                '}';
    }
}
