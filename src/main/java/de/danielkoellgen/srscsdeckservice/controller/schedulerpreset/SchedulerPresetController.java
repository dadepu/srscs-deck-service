package de.danielkoellgen.srscsdeckservice.controller.schedulerpreset;

import de.danielkoellgen.srscsdeckservice.controller.schedulerpreset.dto.SchedulerPresetRequestDto;
import de.danielkoellgen.srscsdeckservice.controller.schedulerpreset.dto.SchedulerPresetResponseDto;
import de.danielkoellgen.srscsdeckservice.domain.schedulerpreset.application.SchedulerPresetService;
import de.danielkoellgen.srscsdeckservice.domain.schedulerpreset.domain.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.NoSuchElementException;
import java.util.UUID;

@RestController
public class SchedulerPresetController {

    private final SchedulerPresetService schedulerPresetService;

    @Autowired
    public SchedulerPresetController(SchedulerPresetService schedulerPresetService) {
        this.schedulerPresetService = schedulerPresetService;
    }

    @PostMapping(value = "/scheduler-presets", consumes= {"application/json"}, produces = {"application/json"})
    public ResponseEntity<SchedulerPresetResponseDto> createPreset(@RequestBody SchedulerPresetRequestDto requestDto) {
        UUID transactionId = UUID.randomUUID();
        PresetName presetName;
        LearningSteps learningSteps;
        LapseSteps lapseSteps;
        MinimumInterval minimumInterval;
        EaseFactor easeFactor;
        EasyFactorModifier easyFactorModifier;
        NormalFactorModifier normalFactorModifier;
        HardFactorModifier hardFactorModifier;
        LapseFactorModifier lapseFactorModifier;
        EasyIntervalModifier easyIntervalModifier;
        LapseIntervalModifier lapseIntervalModifier;
        try {
            presetName = requestDto.getPresetName();
            learningSteps = requestDto.getLearningStepsOrDefault();
            lapseSteps = requestDto.getLapseStepsOrDefault();
            minimumInterval = requestDto.getMinimumIntervalOrDefault();
            easeFactor = requestDto.getEaseFactorOrDefault();
            easyFactorModifier = requestDto.getEasyFactorModifierOrDefault();
            normalFactorModifier = requestDto.getNormalFactorModifierOrDefault();
            hardFactorModifier = requestDto.getHardFactorModifierOrDefault();
            lapseFactorModifier = requestDto.getLapseFactorModifierOrDefault();
            easyIntervalModifier = requestDto.getEasyIntervalModifierOrDefault();
            lapseIntervalModifier = requestDto.getLapseIntervalModifierOrDefault();
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        SchedulerPreset schedulerPreset;
        try {
            schedulerPreset = schedulerPresetService.createPreset(transactionId, presetName, requestDto.userId(),
                    learningSteps, lapseSteps, minimumInterval, easeFactor, easyFactorModifier, normalFactorModifier,
                    hardFactorModifier, lapseFactorModifier, easyIntervalModifier, lapseIntervalModifier
            );
        } catch (NoSuchElementException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(new SchedulerPresetResponseDto(schedulerPreset), HttpStatus.CREATED);
    }
}
