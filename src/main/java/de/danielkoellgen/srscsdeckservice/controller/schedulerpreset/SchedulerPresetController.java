package de.danielkoellgen.srscsdeckservice.controller.schedulerpreset;

import de.danielkoellgen.srscsdeckservice.controller.deck.DeckController;
import de.danielkoellgen.srscsdeckservice.controller.schedulerpreset.dto.SchedulerPresetRequestDto;
import de.danielkoellgen.srscsdeckservice.controller.schedulerpreset.dto.SchedulerPresetResponseDto;
import de.danielkoellgen.srscsdeckservice.domain.schedulerpreset.application.SchedulerPresetService;
import de.danielkoellgen.srscsdeckservice.domain.schedulerpreset.domain.*;
import de.danielkoellgen.srscsdeckservice.domain.schedulerpreset.repository.SchedulerPresetRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@RestController
public class SchedulerPresetController {

    private final SchedulerPresetService schedulerPresetService;
    private final SchedulerPresetRepository schedulerPresetRepository;

    private final Logger logger = LoggerFactory.getLogger(SchedulerPresetController.class);

    @Autowired
    public SchedulerPresetController(SchedulerPresetService schedulerPresetService,
            SchedulerPresetRepository schedulerPresetRepository) {
        this.schedulerPresetService = schedulerPresetService;
        this.schedulerPresetRepository = schedulerPresetRepository;
    }

    @PostMapping(value = "/scheduler-presets", consumes= {"application/json"}, produces = {"application/json"})
    public ResponseEntity<SchedulerPresetResponseDto> createPreset(@RequestBody SchedulerPresetRequestDto requestDto) {
        UUID transactionId = UUID.randomUUID();
        logger.trace("POST /scheduler-presets: Create new Preset. [tid={}, payload={}]",
                transactionId, requestDto);

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
            logger.trace("Request failed. Mapping failed. Responding 400. [tid={}, message={}]",
                    transactionId, e.getStackTrace());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Mapping error.", e);
        }
        SchedulerPreset schedulerPreset;
        try {
            schedulerPreset = schedulerPresetService.createPreset(transactionId, presetName, requestDto.userId(),
                    learningSteps, lapseSteps, minimumInterval, easeFactor, easyFactorModifier, normalFactorModifier,
                    hardFactorModifier, lapseFactorModifier, easyIntervalModifier, lapseIntervalModifier
            );
        } catch (NoSuchElementException e) {
            logger.trace("Request failed. User not found. Responding 404. [tid={}, message={}]",
                    transactionId, e.getStackTrace());
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found.", e);
        }
        logger.trace("Preset '{}' created. Responding 201. [tid={}, payload={}]",
                schedulerPreset.getPresetName().getName(), transactionId, new SchedulerPresetResponseDto(schedulerPreset));
        return new ResponseEntity<>(new SchedulerPresetResponseDto(schedulerPreset), HttpStatus.CREATED);
    }

    @DeleteMapping(value = "/scheduler-presets/{scheduler-preset-id}")
    public HttpStatus disablePreset(@PathVariable("scheduler-preset-id") UUID presetId) {
        UUID transactionId = UUID.randomUUID();
        try {
            schedulerPresetService.disablePreset(transactionId, presetId);
        } catch (NoSuchElementException e) {
            return HttpStatus.NOT_FOUND;
        }
        return HttpStatus.OK;
    }

    @GetMapping(value = "/scheduler-presets/{scheduler-preset-id}", produces = {"application/json"})
    public ResponseEntity<SchedulerPresetResponseDto> getPreset(@PathVariable("scheduler-preset-id") UUID presetId) {
        UUID transactionId = UUID.randomUUID();
        SchedulerPreset schedulerPreset;
        try {
            schedulerPreset = schedulerPresetRepository.findById(presetId).get();
        } catch (NoSuchElementException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(new SchedulerPresetResponseDto(schedulerPreset), HttpStatus.OK);
    }

    @GetMapping(value = "/scheduler-presets", produces = {"application/json"})
    public List<SchedulerPresetResponseDto> getPresetsByUserId(@RequestParam("user-id") UUID userId) {
        UUID transactionId = UUID.randomUUID();
        return schedulerPresetRepository.findSchedulerPresetsByEmbeddedUser_UserId(userId)
                .stream().map(SchedulerPresetResponseDto::new).toList();
    }
}
