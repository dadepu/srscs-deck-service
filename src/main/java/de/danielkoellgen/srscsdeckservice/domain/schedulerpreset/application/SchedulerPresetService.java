package de.danielkoellgen.srscsdeckservice.domain.schedulerpreset.application;

import de.danielkoellgen.srscsdeckservice.domain.schedulerpreset.domain.*;
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

    public SchedulerPreset createPreset(@NotNull UUID transactionId, @NotNull PresetName name, @NotNull UUID userId) {
        User user = userRepository.findById(userId).get();
        SchedulerPreset preset = new SchedulerPreset(name, user);
        schedulerPresetRepository.save(preset);

        logger.info("Default-Preset '{}' created. [tid={}, presetId={}]", name, transactionId, preset.getPresetId());
        return preset;
    }

    public SchedulerPreset createPreset(@NotNull UUID transactionId, @NotNull PresetName name, @NotNull UUID userId,
            @NotNull LearningSteps learningSteps, @NotNull LapseSteps lapseSteps, @NotNull MinimumInterval minimumInterval,
            @NotNull EaseFactor easeFactor, @NotNull EasyFactorModifier easyFactorModifier,
            @NotNull NormalFactorModifier normalFactorModifier, @NotNull HardFactorModifier hardFactorModifier,
            @NotNull LapseFactorModifier lapseFactorModifier, @NotNull EasyIntervalModifier easyIntervalModifier,
            @NotNull LapseIntervalModifier lapseIntervalModifier) {
        User user = userRepository.findById(userId).get();
        SchedulerPreset preset = new SchedulerPreset(name, user, learningSteps, lapseSteps, minimumInterval, easeFactor,
            easyFactorModifier, normalFactorModifier, hardFactorModifier, lapseFactorModifier, easyIntervalModifier,
            lapseIntervalModifier);
        schedulerPresetRepository.save(preset);

        logger.info("Default-Preset '{}' created. [tid={}, presetId={}]", name, transactionId, preset.getPresetId());
        return preset;
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
