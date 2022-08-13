package de.danielkoellgen.srscsdeckservice.domain.card.domain;

import de.danielkoellgen.srscsdeckservice.domain.schedulerpreset.domain.LearningSteps;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.Duration;

@EqualsAndHashCode
public class LearningStep {

    @Getter
    @Field("step_index")
    public final @NotNull Integer stepIndex;

    @Getter
    @Field("learning_steps")
    public final @NotNull LearningSteps learningSteps;

    @PersistenceConstructor
    public LearningStep(@NotNull Integer stepIndex, @NotNull LearningSteps learningSteps) {
        if (stepIndex > learningSteps.getLearningSteps().size() || stepIndex < 0) {
            throw new IllegalArgumentException("Step index out of semantic bounds");
        }
        this.stepIndex = stepIndex;
        this.learningSteps  = learningSteps;
    }

    public static LearningStep startLearningPath(@NotNull LearningSteps learningSteps) {
        return new LearningStep(0, learningSteps);
    }

    public Boolean hasNextStep(@NotNull LearningSteps updatedLearningSteps) {
        return stepIndex < updatedLearningSteps.getLearningSteps().size() - 1;
    }

    public LearningStep takeNextStep(@NotNull LearningSteps updatedLearningSteps) {
        return new LearningStep(stepIndex + 1, updatedLearningSteps);
    }

    public Duration getInterval(){
        return learningSteps.getLearningSteps().get(stepIndex);
    }

    @Override
    public String toString() {
        return "LearningStep{" +
                "stepIndex=" + stepIndex +
                ", learningSteps=" + learningSteps +
                '}';
    }
}
