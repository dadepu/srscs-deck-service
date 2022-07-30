package de.danielkoellgen.srscsdeckservice.domain.card.domain;

import de.danielkoellgen.srscsdeckservice.domain.domainprimitive.EventDateTime;
import de.danielkoellgen.srscsdeckservice.domain.schedulerpreset.domain.EaseFactor;
import de.danielkoellgen.srscsdeckservice.domain.schedulerpreset.domain.SchedulerPreset;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;

@Getter
@EqualsAndHashCode
public class Scheduler {

    @Nullable
    @Transient
    private SchedulerPreset schedulerPreset;

    @NotNull
    @Field("scheduler_preset")
    private EmbeddedSchedulerPreset embeddedSchedulerPreset;

    @NotNull
    @Field("maturity_state")
    private MaturityState maturityState;

    @NotNull
    @Field("review_state")
    private ReviewState reviewState;

    @NotNull
    @Field("review_count")
    private ReviewCount reviewCount;

    @NotNull
    @Field("last_review")
    private EventDateTime lastReview;

    @NotNull
    @Field("next_review")
    private EventDateTime nextReview;

    @NotNull
    @Field("ease_factor")
    private EaseFactor easeFactor;

    @NotNull
    @Field("current_interval")
    private ReviewInterval currentInterval;

    @NotNull
    @Field("learning_step")
    private LearningStep learningStep;

    @Nullable
    @Field("lapse_step")
    private LapseStep lapseStep;


    public Scheduler (@NotNull SchedulerPreset schedulerPreset) {
        this.schedulerPreset = schedulerPreset;
        this.embeddedSchedulerPreset = new EmbeddedSchedulerPreset(schedulerPreset);
        this.maturityState = MaturityState.LEARNING;
        this.reviewState = ReviewState.LEARNING;
        this.reviewCount = ReviewCount.startNewCount();
        this.easeFactor = schedulerPreset.getEaseFactor();
        this.learningStep = LearningStep.startLearningPath(schedulerPreset.getLearningSteps());
        this.lapseStep = null;
        this.currentInterval = ReviewInterval.makeFromDuration(learningStep.getInterval());
        this.lastReview = EventDateTime.makeFromLocalDateTime(LocalDateTime.now());
        this.nextReview = EventDateTime.makeFromLocalDateTime(LocalDateTime.now().plus(currentInterval.getIntervalDuration()));
    }

    @PersistenceConstructor
    public Scheduler(
            @NotNull EmbeddedSchedulerPreset embeddedSchedulerPreset,
            @NotNull MaturityState maturityState,
            @NotNull ReviewState reviewState,
            @NotNull ReviewCount reviewCount,
            @NotNull EaseFactor easeFactor,
            @NotNull LearningStep learningStep,
            @Nullable LapseStep lapseStep,
            @NotNull ReviewInterval currentInterval,
            @NotNull EventDateTime lastReview,
            @NotNull EventDateTime nextReview
    ) {
        this.embeddedSchedulerPreset = embeddedSchedulerPreset;
        this.maturityState = maturityState;
        this.reviewState = reviewState;
        this.reviewCount = reviewCount;
        this.easeFactor = easeFactor;
        this.learningStep = learningStep;
        this.lapseStep = lapseStep;
        this.currentInterval = currentInterval;
        this.lastReview = lastReview;
        this.nextReview = nextReview;
    }

    public void reset() {
        easeFactor = embeddedSchedulerPreset.getEaseFactor();
        maturityState = MaturityState.LEARNING;
        reviewState = ReviewState.LEARNING;
        reviewCount = ReviewCount.startNewCount();
        lastReview = EventDateTime.makeFromLocalDateTime(LocalDateTime.now());
        learningStep = LearningStep.startLearningPath(embeddedSchedulerPreset.getLearningSteps());
        lapseStep = null;
        currentInterval = ReviewInterval.makeFromDuration(learningStep.getInterval());
        nextReview = EventDateTime.makeFromLocalDateTime(
                LocalDateTime.now().plus(currentInterval.getIntervalDuration()));
    }

    public void graduate() {
        reviewCount = reviewCount.incrementedCount();
        reviewState = ReviewState.GRADUATED;
        lastReview = EventDateTime.makeFromLocalDateTime(LocalDateTime.now());
        currentInterval = ReviewInterval.makeFromDuration(
                embeddedSchedulerPreset.getLearningSteps().getLearningSteps().get(
                    embeddedSchedulerPreset.getLearningSteps().getLearningSteps().size() - 1
                )
        );
        nextReview = EventDateTime.makeFromLocalDateTime(
                LocalDateTime.now().plus(currentInterval.getIntervalDuration()));
        if (currentInterval.getIntervalDuration().toMinutes() >
                embeddedSchedulerPreset.getMatureInterval().getMatureInterval().toMinutes()) {
            maturityState = MaturityState.MATURED;
        }
    }

    public void review(ReviewAction action) {
        switch (action) {
            case EASY -> easyReview();
            case NORMAL -> normalReview();
            case HARD -> hardReview();
            case LAPSE -> lapseReview();
        }
        reviewCount = reviewCount.incrementedCount();
    }

    public void easyReview() {
        easeFactor = easeFactor.modifiedFactor(embeddedSchedulerPreset.getEasyFactorModifier());

        if (reviewState == ReviewState.LEARNING) {
            currentInterval = ReviewInterval.makeFromDurationWithModifier(
                    embeddedSchedulerPreset.getLapseSteps().getLastStep(),
                    embeddedSchedulerPreset.getEasyIntervalModifier()
            );
        }
        if (reviewState == ReviewState.LAPSING) {
            assert lapseStep != null;
            currentInterval = ReviewInterval.makeFromDurationWithModifier(
                    lapseStep.getPenalisedPreLapseReviewInterval().getIntervalDuration(),
                    embeddedSchedulerPreset.getEasyIntervalModifier()
            );
            lapseStep = null;
        }
        if (reviewState == ReviewState.GRADUATED) {
            currentInterval = currentInterval.reviewInterval(
                    easeFactor, embeddedSchedulerPreset.getEasyIntervalModifier());
        }
        reviewState = ReviewState.GRADUATED;
        lastReview = EventDateTime.makeFromLocalDateTime(LocalDateTime.now());
        nextReview = EventDateTime.makeFromLocalDateTime(
                LocalDateTime.now().plus(currentInterval.getIntervalDuration()));
    }

    public void normalReview() {
        if (reviewState == ReviewState.LEARNING) {
            if (learningStep.hasNextStep(embeddedSchedulerPreset.getLearningSteps())) {
                learningStep = learningStep.takeNextStep(embeddedSchedulerPreset.getLearningSteps());
                currentInterval = ReviewInterval.makeFromDuration(learningStep.getInterval());
            } else {
                easeFactor = easeFactor.modifiedFactor(embeddedSchedulerPreset.getNormalFactorModifier());
                currentInterval = currentInterval.reviewInterval(easeFactor, null);
                reviewState = ReviewState.GRADUATED;
            }
        }
        if (reviewState == ReviewState.LAPSING) {
            assert lapseStep != null;
            if (lapseStep.hasNextStep(embeddedSchedulerPreset.getLapseSteps())) {
                lapseStep = lapseStep.takeNextStep(embeddedSchedulerPreset.getLapseSteps());
                currentInterval = ReviewInterval.makeFromDuration(lapseStep.getInterval());
            } else {
                easeFactor = easeFactor.modifiedFactor(embeddedSchedulerPreset.getNormalFactorModifier());
                currentInterval = lapseStep.getPenalisedPreLapseReviewInterval();
                lapseStep = null;
                reviewState = ReviewState.GRADUATED;
            }
        }
        if (reviewState == ReviewState.GRADUATED) {
            easeFactor = easeFactor.modifiedFactor(embeddedSchedulerPreset.getNormalFactorModifier());
            currentInterval = currentInterval.reviewInterval(easeFactor, null);
        }
        lastReview = EventDateTime.makeFromLocalDateTime(LocalDateTime.now());
        nextReview = EventDateTime.makeFromLocalDateTime(
                LocalDateTime.now().plus(currentInterval.getIntervalDuration()));
    }

    public void hardReview() {
        if (reviewState == ReviewState.LEARNING) {
            // NO CHANGE
        }
        if (reviewState == ReviewState.LAPSING) {
            easeFactor = easeFactor.modifiedFactor(embeddedSchedulerPreset.getHardFactorModifier());
        }
        if (reviewState == ReviewState.GRADUATED) {
            easeFactor = easeFactor.modifiedFactor(embeddedSchedulerPreset.getHardFactorModifier());
        }
        currentInterval = currentInterval.modifyInterval(embeddedSchedulerPreset.getHardIntervalModifier());
        lastReview = EventDateTime.makeFromLocalDateTime(LocalDateTime.now());
        nextReview = EventDateTime.makeFromLocalDateTime(
                LocalDateTime.now().plus(currentInterval.getIntervalDuration()));
    }

    public void lapseReview() {
        if (reviewState == ReviewState.LEARNING) {
            learningStep = LearningStep.startLearningPath(embeddedSchedulerPreset.getLearningSteps());
            currentInterval = ReviewInterval.makeFromDuration(learningStep.getInterval());
        }
        if (reviewState == ReviewState.LAPSING) {
            assert lapseStep != null;
            easeFactor = easeFactor.modifiedFactor(embeddedSchedulerPreset.getLapseFactorModifier());
            lapseStep = LapseStep.startLapsePath(
                    embeddedSchedulerPreset.getLapseSteps(),
                    lapseStep.getPenalisedPreLapseReviewInterval().lapsedInterval(
                            embeddedSchedulerPreset.getMinimumInterval(),
                            embeddedSchedulerPreset.getLapseIntervalModifier()
                    )
            );
            currentInterval = ReviewInterval.makeFromDuration(lapseStep.getInterval());
            reviewState = ReviewState.LAPSING;
        }
        if (reviewState == ReviewState.GRADUATED) {
            easeFactor = easeFactor.modifiedFactor(embeddedSchedulerPreset.getLapseFactorModifier());
            lapseStep = LapseStep.startLapsePath(
                    embeddedSchedulerPreset.getLapseSteps(), currentInterval
            );
            currentInterval = ReviewInterval.makeFromDuration(lapseStep.getInterval());
            reviewState = ReviewState.LAPSING;
        }
        lastReview = EventDateTime.makeFromLocalDateTime(LocalDateTime.now());
        nextReview = EventDateTime.makeFromLocalDateTime(
                LocalDateTime.now().plus(currentInterval.getIntervalDuration()));
    }

    public void updateSchedulerPreset(@NotNull SchedulerPreset schedulerPreset) {
        this.schedulerPreset = schedulerPreset;
        embeddedSchedulerPreset = new EmbeddedSchedulerPreset(schedulerPreset);
    }

    @Override
    public String toString() {
        return "Scheduler{" +
                "schedulerPreset=" + schedulerPreset +
                ", embeddedSchedulerPreset=" + embeddedSchedulerPreset +
                ", maturityState=" + maturityState +
                ", reviewState=" + reviewState +
                ", reviewCount=" + reviewCount +
                ", lastReview=" + lastReview +
                ", nextReview=" + nextReview +
                ", easeFactor=" + easeFactor +
                ", currentInterval=" + currentInterval +
                ", learningStep=" + learningStep +
                ", lapseStep=" + lapseStep +
                '}';
    }
}
