package de.danielkoellgen.srscsdeckservice.domain.card;

import de.danielkoellgen.srscsdeckservice.domain.card.domain.ReviewAction;
import de.danielkoellgen.srscsdeckservice.domain.card.domain.ReviewState;
import de.danielkoellgen.srscsdeckservice.domain.card.domain.Scheduler;
import de.danielkoellgen.srscsdeckservice.domain.schedulerpreset.domain.LearningSteps;
import de.danielkoellgen.srscsdeckservice.domain.schedulerpreset.domain.PresetName;
import de.danielkoellgen.srscsdeckservice.domain.schedulerpreset.domain.SchedulerPreset;
import de.danielkoellgen.srscsdeckservice.domain.user.domain.User;
import de.danielkoellgen.srscsdeckservice.domain.user.domain.Username;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class SchedulerTest {

    /*
        Given a Scheduler with 2 learning-steps,
        when reviewed 2 times as 'NORMAL',
        then it should graduate on the last one.

     */
    @Test
    public void shouldGraduateAfterLearningPhase() throws Exception {
        // given
        LearningSteps steps = LearningSteps.makeFromListOfDurations(
                List.of(Duration.ofHours(18), Duration.ofDays(5)));
        SchedulerPreset preset = makeScheduler();
        preset.setLearningSteps(steps);
        Scheduler scheduler = new Scheduler(preset);

        // when
        scheduler.review(ReviewAction.NORMAL);
        scheduler.review(ReviewAction.NORMAL);

        // then
        assertThat(scheduler.getReviewState())
                .isEqualTo(ReviewState.GRADUATED);

    }

    private SchedulerPreset makeScheduler() throws Exception {
        User user = new User(UUID.randomUUID(), new Username("anyName"));
        return new SchedulerPreset(new PresetName("anyName"), user);
    }
}
