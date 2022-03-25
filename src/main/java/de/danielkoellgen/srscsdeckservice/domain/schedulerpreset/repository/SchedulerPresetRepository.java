package de.danielkoellgen.srscsdeckservice.domain.schedulerpreset.repository;

import de.danielkoellgen.srscsdeckservice.domain.schedulerpreset.domain.SchedulerPreset;
import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

public interface SchedulerPresetRepository extends CrudRepository<SchedulerPreset, UUID> {
}
