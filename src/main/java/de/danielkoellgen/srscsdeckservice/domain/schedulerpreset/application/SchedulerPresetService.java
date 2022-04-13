package de.danielkoellgen.srscsdeckservice.domain.schedulerpreset.application;

import de.danielkoellgen.srscsdeckservice.domain.schedulerpreset.domain.PresetName;
import de.danielkoellgen.srscsdeckservice.domain.schedulerpreset.domain.SchedulerPreset;
import de.danielkoellgen.srscsdeckservice.domain.schedulerpreset.repository.SchedulerPresetRepository;
import de.danielkoellgen.srscsdeckservice.domain.user.domain.User;
import de.danielkoellgen.srscsdeckservice.domain.user.repository.UserRepository;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class SchedulerPresetService {

    private final SchedulerPresetRepository schedulerPresetRepository;
    private final UserRepository userRepository;

    private final Logger logger = LoggerFactory.getLogger(SchedulerPresetService.class);

    @Autowired
    public SchedulerPresetService(SchedulerPresetRepository schedulerPresetRepository, UserRepository userRepository) {
        this.schedulerPresetRepository = schedulerPresetRepository;
        this.userRepository = userRepository;
    }

    public void createPreset(@NotNull UUID transactionId, @NotNull PresetName name, @NotNull UUID userId) {
        User user = userRepository.findById(userId).get();
        SchedulerPreset preset = new SchedulerPreset(name, user);
        schedulerPresetRepository.save(preset);

        logger.info("Preset '{}' created. [tid={}, presetId={}]", name, transactionId, preset.getPresetId());
    }

    public SchedulerPreset createTransientDefaultPreset(@NotNull UUID userId) {
        User user = userRepository.findById(userId).get();
        try {
            return new SchedulerPreset(new PresetName("default"), user);
        } catch (Exception e) {
            throw new RuntimeException("Preset-name invalid.");
        }
    }

    public void disablePreset(@NotNull UUID transactionId, @NotNull UUID presetId) {
        SchedulerPreset preset = schedulerPresetRepository.findById(presetId).get();
        preset.disablePreset();
        schedulerPresetRepository.save(preset);

        logger.info("Preset {} disabled. [tid={}, presetId={}]",
                preset.getPresetName().getName(), transactionId, presetId);
    }
}
