package de.danielkoellgen.srscsdeckservice.domain.schedulerpreset.application;

import de.danielkoellgen.srscsdeckservice.domain.schedulerpreset.domain.*;
import de.danielkoellgen.srscsdeckservice.domain.schedulerpreset.repository.SchedulerPresetRepository;
import de.danielkoellgen.srscsdeckservice.domain.user.domain.User;
import de.danielkoellgen.srscsdeckservice.domain.user.repository.UserRepository;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.sleuth.Tracer;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class SchedulerPresetService {

    private final SchedulerPresetRepository schedulerPresetRepository;
    private final UserRepository userRepository;

    @Autowired
    private Tracer tracer;

    private final Logger log = LoggerFactory.getLogger(SchedulerPresetService.class);

    @Autowired
    public SchedulerPresetService(SchedulerPresetRepository schedulerPresetRepository,
            UserRepository userRepository) {
        this.schedulerPresetRepository = schedulerPresetRepository;
        this.userRepository = userRepository;
    }

    public SchedulerPreset createPreset(@NotNull PresetName name, @NotNull UUID userId) {
        log.trace("Creating new Default-Preset '{}' for User '{}'...", name.getName(), userId);

        log.trace("Fetching User by id '{}'...", userId);
        User user = userRepository.findById(userId).orElseThrow();
        log.debug("Fetched User: {}", user);

        SchedulerPreset preset = new SchedulerPreset(name, user);
        schedulerPresetRepository.save(preset);
        log.info("New Default-Preset '{}' successfully created.", preset.getPresetName());
        log.debug("New Preset: {}", preset);
        return preset;
    }

    public SchedulerPreset createPreset(@NotNull PresetName name, @NotNull UUID userId,
            @NotNull LearningSteps learningSteps, @NotNull LapseSteps lapseSteps,
            @NotNull MinimumInterval minimumInterval, @NotNull EaseFactor easeFactor,
            @NotNull EasyFactorModifier easyFactorModifier,
            @NotNull NormalFactorModifier normalFactorModifier,
            @NotNull HardFactorModifier hardFactorModifier,
            @NotNull LapseFactorModifier lapseFactorModifier,
            @NotNull EasyIntervalModifier easyIntervalModifier,
            @NotNull LapseIntervalModifier lapseIntervalModifier) {
        log.trace("Creating new Preset '{}'...", name.getName());

        log.trace("Fetching User by id '{}'...", userId);
        User user = userRepository.findById(userId).orElseThrow();
        log.debug("Fetched User: {}", user);

        SchedulerPreset preset = new SchedulerPreset(name, user, learningSteps, lapseSteps,
                minimumInterval, easeFactor, easyFactorModifier, normalFactorModifier,
                hardFactorModifier, lapseFactorModifier, easyIntervalModifier, lapseIntervalModifier);
        schedulerPresetRepository.save(preset);
        log.info("New Preset '{}' successfully created.", preset.getPresetName());
        log.debug("New Preset: {}", preset);
        return preset;
    }

    public SchedulerPreset createTransientDefaultPreset(@NotNull UUID userId) {
        log.trace("Creating new transient Default-Preset...");

        log.trace("Fetching User by id '{}'...", userId);
        User user = userRepository.findById(userId).orElseThrow();
        log.debug("Fetched User: {}", user);

        PresetName name;
        try {
            name = new PresetName("default");
        } catch (Exception e) {
            log.error("Failed to create PresetName 'default' even though it should be creatable. " +
                    "Input validation corrupted.");
            throw new RuntimeException("Failed to create transient Default-Preset. PresetName invalid.");
        }
        SchedulerPreset preset = new SchedulerPreset(name, user);
        log.debug("New Transient-Preset: {}", preset);
        return preset;
    }

    public void disablePreset(@NotNull UUID presetId) {
        log.trace("Disabling Preset '{}'...", presetId);

        log.trace("Fetching Preset by id '{}'...", presetId);
        SchedulerPreset preset = schedulerPresetRepository.findById(presetId).orElseThrow();
        log.debug("Fetched Preset: {}", preset.toStringIdent());

        preset.disablePreset();
        schedulerPresetRepository.save(preset);
        log.info("Preset '{}' successfully disabled.", preset.getPresetName());
        log.debug("Updated Preset: {}", preset.toStringIdent());
    }
}
