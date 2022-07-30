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
    public SchedulerPresetService(SchedulerPresetRepository schedulerPresetRepository, UserRepository userRepository) {
        this.schedulerPresetRepository = schedulerPresetRepository;
        this.userRepository = userRepository;
    }

    public SchedulerPreset createPreset(@NotNull PresetName name, @NotNull UUID userId) {
        log.trace("Creating new Default-Preset '{}'...", name.getName());

        User user = userRepository.findById(userId).orElseThrow();
        log.debug("User fetched by id: {}", user);

        SchedulerPreset preset = new SchedulerPreset(name, user);
        log.trace("Default-Preset {} created.", preset.getPresetName().getName());

        schedulerPresetRepository.save(preset);
        log.info("Default-Preset '{}' created.", name);
        log.debug("{}", preset);
        return preset;
    }

    public SchedulerPreset createPreset(@NotNull PresetName name, @NotNull UUID userId,
            @NotNull LearningSteps learningSteps, @NotNull LapseSteps lapseSteps, @NotNull MinimumInterval minimumInterval,
            @NotNull EaseFactor easeFactor, @NotNull EasyFactorModifier easyFactorModifier,
            @NotNull NormalFactorModifier normalFactorModifier, @NotNull HardFactorModifier hardFactorModifier,
            @NotNull LapseFactorModifier lapseFactorModifier, @NotNull EasyIntervalModifier easyIntervalModifier,
            @NotNull LapseIntervalModifier lapseIntervalModifier) {
        log.trace("Creating new Custom-Preset '{}'...", name.getName());

        User user = userRepository.findById(userId).orElseThrow();
        log.debug("User fetched by id: {}", user);

        SchedulerPreset preset = new SchedulerPreset(name, user, learningSteps, lapseSteps, minimumInterval, easeFactor,
            easyFactorModifier, normalFactorModifier, hardFactorModifier, lapseFactorModifier, easyIntervalModifier,
            lapseIntervalModifier);
        log.trace("New Preset: {}", preset);

        schedulerPresetRepository.save(preset);
        log.info("Custom-Preset '{}' created.", name);
        return preset;
    }

    public SchedulerPreset createTransientDefaultPreset(@NotNull UUID userId) {
        log.trace("Creating transient Default-Preset...");

        User user = userRepository.findById(userId).orElseThrow();
        log.debug("User fetched by id: {}", user);

        PresetName name;
        try {
            return new SchedulerPreset(new PresetName("default"), user);
        } catch (Exception e) {
            throw new RuntimeException("Failed to create transient Default-Preset. PresetName invalid.");
        }
        SchedulerPreset preset = new SchedulerPreset(name, user);
        log.debug("New Transient-Preset: {}", preset);
        return preset;
    }

    public void disablePreset(@NotNull UUID presetId) {
        log.trace("Disabling Preset...");

        SchedulerPreset preset = schedulerPresetRepository.findById(presetId).orElseThrow();
        log.debug("Preset fetched by id: {}", preset.toStringIdent());

        preset.disablePreset();
        log.trace("Preset disabled.");

        schedulerPresetRepository.save(preset);
        log.info("Preset {} disabled.", preset.getPresetName().getName());
        log.debug("Preset updated: {}", preset.toStringIdent());
    }

    private String getTraceIdOrEmptyString() {
        try {
            return tracer.currentSpan().context().traceId();
        } catch (Exception e) {
            return "";
        }
    }
}
