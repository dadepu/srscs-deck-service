package de.danielkoellgen.srscsdeckservice.domain.card.domain;

import de.danielkoellgen.srscsdeckservice.domain.schedulerpreset.domain.LearningSteps;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;

@EqualsAndHashCode
public class LearningStep {

    @Getter
    @NotNull
    public final Integer stepIndex;

    @Getter
    @NotNull
    public final LearningSteps learningSteps;

    private LearningStep(@NotNull Integer index, @NotNull LearningSteps learningSteps) {
        if (index > learningSteps.getLearningSteps().size() || index < 0) {
            throw new IllegalArgumentException("Step index out of semantic bounds");
        }
        this.stepIndex = index;
        this.learningSteps  = learningSteps;
    }

    public static LearningStep startLearningPath(@NotNull LearningSteps learningSteps) {
        return new LearningStep(0, learningSteps);
    }

    public Boolean hasNextStep(@NotNull LearningSteps updatedLearningSteps) {
        return stepIndex < updatedLearningSteps.getLearningSteps().size();
    }

    public LearningStep takeNextStep(@NotNull LearningSteps updatedLearningSteps) {
        return new LearningStep(stepIndex + 1, updatedLearningSteps);
    }

    public Duration getInterval(){
        return learningSteps.getLearningSteps().get(stepIndex);
    }
}
