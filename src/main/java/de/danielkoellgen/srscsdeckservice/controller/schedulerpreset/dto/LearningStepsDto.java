package de.danielkoellgen.srscsdeckservice.controller.schedulerpreset.dto;

import de.danielkoellgen.srscsdeckservice.domain.schedulerpreset.domain.LearningSteps;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.util.List;

public record LearningStepsDto(

    @NotNull
    List<Long> learningSteps

) {
    public LearningStepsDto(@NotNull LearningSteps learningSteps) {
        this(learningSteps.getLearningSteps().stream().map(Duration::toMinutes).toList());
    }

    public LearningSteps mapToLearningSteps() {
        return LearningSteps.makeFromListOfDurations(
                learningSteps.stream().map(Duration::ofMinutes).toList()
        );
    }
}
