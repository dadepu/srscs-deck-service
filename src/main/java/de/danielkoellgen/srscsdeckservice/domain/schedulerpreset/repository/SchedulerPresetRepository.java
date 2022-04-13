package de.danielkoellgen.srscsdeckservice.domain.schedulerpreset.repository;

import de.danielkoellgen.srscsdeckservice.domain.schedulerpreset.domain.SchedulerPreset;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.UUID;

public interface SchedulerPresetRepository extends CrudRepository<SchedulerPreset, UUID> {

    List<SchedulerPreset> findSchedulerPresetsByEmbeddedUser_UserId(@NotNull UUID userId);
}
