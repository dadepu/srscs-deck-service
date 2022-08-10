package de.danielkoellgen.srscsdeckservice.domain.card.domain;

import de.danielkoellgen.srscsdeckservice.domain.domainprimitive.EventDateTime;
import de.danielkoellgen.srscsdeckservice.domain.schedulerpreset.domain.EaseFactor;
import de.danielkoellgen.srscsdeckservice.domain.schedulerpreset.domain.SchedulerPreset;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    @Transient
    private final Logger log = LoggerFactory.getLogger(Scheduler.class);


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
        log.trace("Resetting Scheduler...");
        easeFactor = embeddedSchedulerPreset.getEaseFactor();
        log.debug("EaseFactor set to config default '{}'.", easeFactor);
        maturityState = MaturityState.LEARNING;
        log.debug("MaturityState set to '{}'.", maturityState);
        reviewState = ReviewState.LEARNING;
        log.debug("ReviewState set to '{}'.", reviewState);
        reviewCount = ReviewCount.startNewCount();
        log.debug("ReviewCount resetted to '{}'.", reviewCount);
        lastReview = EventDateTime.makeFromLocalDateTime(LocalDateTime.now());
        log.debug("LastReview set to now '{}'.", lastReview);
        learningStep = LearningStep.startLearningPath(embeddedSchedulerPreset.getLearningSteps());
        log.debug("LearningStep resetted to config default '{}'.", learningStep);
        lapseStep = null;
        log.debug("LapseStep resetted to '{}'.", lapseStep);
        currentInterval = ReviewInterval.makeFromDuration(learningStep.getInterval());
        log.debug("CurrentInterval reset to first step '{}'.", currentInterval);
        nextReview = EventDateTime.makeFromLocalDateTime(
                LocalDateTime.now().plus(currentInterval.getIntervalDuration()));
        log.debug("NextReview set to '{}'.", nextReview);
    }

    public void graduate() {
        log.trace("Graduating Scheduler...");
        reviewCount = reviewCount.incrementedCount();
        log.debug("ReviewCount incremented to '{}'.", reviewCount);
        reviewState = ReviewState.GRADUATED;
        log.debug("ReviewState set to '{}'.", reviewState);
        lastReview = EventDateTime.makeFromLocalDateTime(LocalDateTime.now());
        log.debug("LastReview set to now '{}'.", lastReview);
        currentInterval = ReviewInterval.makeFromDuration(
                embeddedSchedulerPreset.getLearningSteps().getLearningSteps().get(
                    embeddedSchedulerPreset.getLearningSteps().getLearningSteps().size() - 1
                ));
        log.debug("CurrentInterval set to last LearningStep '{}'.", currentInterval);
        nextReview = EventDateTime.makeFromLocalDateTime(
                LocalDateTime.now().plus(currentInterval.getIntervalDuration()));
        log.debug("NextReview set to '{}.", nextReview);
        if (currentInterval.getIntervalDuration().toMinutes() >
                embeddedSchedulerPreset.getMatureInterval().getMatureInterval().toMinutes()) {
            maturityState = MaturityState.MATURED;
            log.debug("MaturityState graduated to 'MATURED'.");
        }
    }

    public void review(ReviewAction action) {
        log.trace("Updating Scheduler as '{}'-Review...", action);
        switch (action) {
            case EASY -> easyReview();
            case NORMAL -> normalReview();
            case HARD -> hardReview();
            case LAPSE -> lapseReview();
        }
        reviewCount = reviewCount.incrementedCount();
        log.debug("ReviewCount incremented to '{}'.", reviewCount);
    }

    public void easyReview() {
        easeFactor = easeFactor.modifiedFactor(embeddedSchedulerPreset.getEasyFactorModifier());
        log.debug("EaseFactor updated to '{}'.", easeFactor);

        if (reviewState == ReviewState.LEARNING) {
            log.trace("ReviewState is 'LEARNING'.");
            currentInterval = ReviewInterval.makeFromDurationWithModifier(
                    embeddedSchedulerPreset.getLapseSteps().getLastStep(),
                    embeddedSchedulerPreset.getEasyIntervalModifier()
            );
            log.debug("CurrentInterval updated to '{}'.", currentInterval);
        }
        if (reviewState == ReviewState.LAPSING) {
            log.trace("ReviewState is 'LAPSING'.");
            assert lapseStep != null;
            currentInterval = ReviewInterval.makeFromDurationWithModifier(
                    lapseStep.getPenalisedPreLapseReviewInterval().getIntervalDuration(),
                    embeddedSchedulerPreset.getEasyIntervalModifier()
            );
            log.debug("CurrentInterval updated to '{}'.", currentInterval);
            lapseStep = null;
            log.debug("LapseStep resetted to 'null'.");
        }
        if (reviewState == ReviewState.GRADUATED) {
            log.trace("ReviewState is 'GRADUATED'.");
            currentInterval = currentInterval.reviewInterval(
                    easeFactor, embeddedSchedulerPreset.getEasyIntervalModifier());
            log.debug("CurrentInterval updated to '{}'.", currentInterval);
        }
        reviewState = ReviewState.GRADUATED;
        log.debug("ReviewState set to {}.", reviewState);
        lastReview = EventDateTime.makeFromLocalDateTime(LocalDateTime.now());
        log.debug("LastReview set to now '{}'.", lastReview);
        nextReview = EventDateTime.makeFromLocalDateTime(
                LocalDateTime.now().plus(currentInterval.getIntervalDuration()));
        log.debug("NextReview set to '{}'.", nextReview);
    }

    public void normalReview() {
        if (reviewState == ReviewState.LEARNING) {
            if (learningStep.hasNextStep(embeddedSchedulerPreset.getLearningSteps())) {
                log.trace("ReviewState is 'LEARNING' and has at least one more step.");
                learningStep = learningStep.takeNextStep(embeddedSchedulerPreset.getLearningSteps());
                log.debug("LearningStep updated to next Step '{}'.", learningStep);
                currentInterval = ReviewInterval.makeFromDuration(learningStep.getInterval());
                log.debug("CurrentInterval updated to '{}'.", currentInterval);
            } else {
                log.trace("ReviewState is 'LEARNING' with no more steps.");
                easeFactor = easeFactor.modifiedFactor(embeddedSchedulerPreset.getNormalFactorModifier());
                log.debug("EaseFactor incremented to '{}'.", easeFactor);
                currentInterval = currentInterval.reviewInterval(easeFactor, null);
                log.debug("CurrentInterval updated to '{}'.", currentInterval);
                reviewState = ReviewState.GRADUATED;
                log.debug("ReviewState graduated from 'LEARNING' to 'GRADUATED'.");
            }
        }
        if (reviewState == ReviewState.LAPSING) {
            assert lapseStep != null;
            if (lapseStep.hasNextStep(embeddedSchedulerPreset.getLapseSteps())) {
                log.trace("ReviewState is 'LAPSING' with at least one more step.");
                lapseStep = lapseStep.takeNextStep(embeddedSchedulerPreset.getLapseSteps());
                log.debug("LapseStep updated to next Step '{}'.", lapseStep);
                currentInterval = ReviewInterval.makeFromDuration(lapseStep.getInterval());
                log.debug("CurrentInterval updated to '{}'.", currentInterval);
            } else {
                log.trace("ReviewState is 'LAPSING' with no more steps.");
                easeFactor = easeFactor.modifiedFactor(embeddedSchedulerPreset.getNormalFactorModifier());
                log.debug("EaseFactor incremented to '{}'.", easeFactor);
                currentInterval = lapseStep.getPenalisedPreLapseReviewInterval();
                log.debug("CurrentInterval back to regular with '{}'.", currentInterval);
                lapseStep = null;
                log.debug("LapseStep resetted to 'null'.");
                reviewState = ReviewState.GRADUATED;
                log.debug("ReviewState went from 'LAPSE' back to 'GRADUATED'.");
            }
        }
        if (reviewState == ReviewState.GRADUATED) {
            log.trace("ReviewState is 'GRADUATED'.");
            easeFactor = easeFactor.modifiedFactor(embeddedSchedulerPreset.getNormalFactorModifier());
            log.debug("EaseFactor incremented to '{}'.", easeFactor);
            currentInterval = currentInterval.reviewInterval(easeFactor, null);
            log.debug("CurrentInterval updated to '{}'.", currentInterval);
        }
        lastReview = EventDateTime.makeFromLocalDateTime(LocalDateTime.now());
        log.debug("lastReview set to now '{}'.", lastReview);
        nextReview = EventDateTime.makeFromLocalDateTime(
                LocalDateTime.now().plus(currentInterval.getIntervalDuration()));
        log.debug("NextReview set to '{}'.", nextReview);
    }

    public void hardReview() {
        if (reviewState == ReviewState.LEARNING) {
            // NO CHANGE
            log.trace("ReviewState is 'LEARNING'.");
        }
        if (reviewState == ReviewState.LAPSING) {
            log.trace("ReviewState is 'LAPSING'.");
            easeFactor = easeFactor.modifiedFactor(embeddedSchedulerPreset.getHardFactorModifier());
            log.debug("EaseFactor lowered to '{}'.", easeFactor);
        }
        if (reviewState == ReviewState.GRADUATED) {
            log.trace("ReviewState is 'GRADUATED'.");
            easeFactor = easeFactor.modifiedFactor(embeddedSchedulerPreset.getHardFactorModifier());
            log.debug("EaseFactor lowered to '{}'.", easeFactor);
        }
        currentInterval = currentInterval.modifyInterval(embeddedSchedulerPreset.getHardIntervalModifier());
        log.debug("CurrentInterval updated to '{}'.", currentInterval);
        lastReview = EventDateTime.makeFromLocalDateTime(LocalDateTime.now());
        log.debug("LastReview set to now '{}'.", lastReview);
        nextReview = EventDateTime.makeFromLocalDateTime(
                LocalDateTime.now().plus(currentInterval.getIntervalDuration()));
        log.debug("NextReview updated to '{}'.", nextReview);
    }

    public void lapseReview() {
        if (reviewState == ReviewState.LEARNING) {
            log.trace("ReviewState is 'LEARNING'.");
            learningStep = LearningStep.startLearningPath(embeddedSchedulerPreset.getLearningSteps());
            log.debug("LearningStep resetted to the first learning-step '{}'.", learningStep);
            currentInterval = ReviewInterval.makeFromDuration(learningStep.getInterval());
            log.debug("CurrentInterval updated to '{}'.", currentInterval);
        }
        if (reviewState == ReviewState.LAPSING) {
            log.trace("ReviewState is 'LAPSING'.");
            assert lapseStep != null;
            easeFactor = easeFactor.modifiedFactor(embeddedSchedulerPreset.getLapseFactorModifier());
            log.debug("EaseFactor lowered to '{}'.", easeFactor);
            lapseStep = LapseStep.startLapsePath(
                    embeddedSchedulerPreset.getLapseSteps(),
                    lapseStep.getPenalisedPreLapseReviewInterval().lapsedInterval(
                            embeddedSchedulerPreset.getMinimumInterval(),
                            embeddedSchedulerPreset.getLapseIntervalModifier()
                    )
            );
            log.debug("LapseStep set to first lapse-step '{}'.", lapseStep);
            currentInterval = ReviewInterval.makeFromDuration(lapseStep.getInterval());
            log.debug("CurrentInterval updated to '{}'.", currentInterval);
        }
        if (reviewState == ReviewState.GRADUATED) {
            log.trace("ReviewState is 'GRADUATED'.");
            easeFactor = easeFactor.modifiedFactor(embeddedSchedulerPreset.getLapseFactorModifier());
            log.debug("EaseFactor lowered to '{}'.", easeFactor);
            lapseStep = LapseStep.startLapsePath(
                    embeddedSchedulerPreset.getLapseSteps(), currentInterval
            );
            log.debug("LapseStep set to first lapse-step '{}'.", lapseStep);
            currentInterval = ReviewInterval.makeFromDuration(lapseStep.getInterval());
            log.debug("CurrentInterval updated to '{}'.", currentInterval);
            reviewState = ReviewState.LAPSING;
            log.debug("ReviewState set from 'GRADUATED' to 'LAPSING'.");
        }
        lastReview = EventDateTime.makeFromLocalDateTime(LocalDateTime.now());
        log.debug("LastReview set to now '{}'.", lastReview);
        nextReview = EventDateTime.makeFromLocalDateTime(
                LocalDateTime.now().plus(currentInterval.getIntervalDuration()));
        log.debug("NextReview updated to '{}'.", nextReview);
    }

    public void updateSchedulerPreset(@NotNull SchedulerPreset schedulerPreset) {
        this.schedulerPreset = schedulerPreset;
        embeddedSchedulerPreset = new EmbeddedSchedulerPreset(schedulerPreset);
        log.debug("Scheduler updated with Preset '{}'.", schedulerPreset.getPresetName());
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
